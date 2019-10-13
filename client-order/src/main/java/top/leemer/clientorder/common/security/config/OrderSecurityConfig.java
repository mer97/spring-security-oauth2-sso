package top.leemer.clientorder.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoRestTemplateFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import top.leemer.clientorder.common.bean.AjaxAuthFailureHandler;
import top.leemer.clientorder.common.bean.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

/**
 * @author LEEMER
 * Create Date: 2019-09-21
 */
@Configuration
@EnableOAuth2Sso
public class OrderSecurityConfig extends WebSecurityConfigurerAdapter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private AjaxAuthFailureHandler ajaxAuthFailureHandler;

    @Value("${app.sso.login.url}")
    private String ssoLoginUrl;

    @Value("${app.sso.logout.url}")
    private String ssoLogoutUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(oAuth2ClientAuthenticationProcessingFilter(),
                AbstractPreAuthenticatedProcessingFilter.class);

        http.exceptionHandling()
                .accessDeniedHandler(handleAccessDeniedForUser())
                .and()
            .headers()
                .frameOptions()
                .disable()
                .and()
            .authorizeRequests()
                .antMatchers("/public/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
            .formLogin()
                .failureHandler(ajaxAuthFailureHandler)
                .and()
            .logout()
                .logoutSuccessUrl(ssoLogoutUrl)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .and()
            .csrf()
                .disable()
                .cors();
    }

    /**
     * 自定义AccessDeniedHandler来处理Ajax请求。
     * @return
     */
    private AccessDeniedHandler handleAccessDeniedForUser() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                AccessDeniedException accessDeniedException) -> {
            String requestedWithHeader = request.getHeader("X-Requested-With");
            System.err.println(accessDeniedException.getMessage());
            if ("XMLHttpRequest".equals(requestedWithHeader)) {
                ErrorResponse errorResponse = new ErrorResponse(accessDeniedException.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
            } else {
                response.sendRedirect(ssoLoginUrl);
            }
        };
    }

    /**
     * 在实际操作中发现访问登录受限接口时，假如认证服务器认证过程报错的话，
     * 客户端会登录失败且又会重定向到认证服务器的登录授权接口。
     * 假如认证服务器宕机的话或服务暂时不可用的话会导致异常访问的堆积，
     * 增加了服务器的压力，最终导致认证服务器彻底崩溃。
     * 故客户端需要在向认证服务器登录授权失败时要有自己的异常处理。
     * @return
     */
    private OAuth2ClientAuthenticationProcessingFilter oAuth2ClientAuthenticationProcessingFilter() {
        OAuth2RestOperations restTemplate = this.applicationContext
                .getBean(UserInfoRestTemplateFactory.class).getUserInfoRestTemplate();
        ResourceServerTokenServices tokenServices = this.applicationContext
                .getBean(ResourceServerTokenServices.class);
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login");
        filter.setRestTemplate(restTemplate);
        filter.setTokenServices(tokenServices);
        filter.setApplicationEventPublisher(this.applicationContext);
        filter.setAuthenticationFailureHandler(failureHandler());
        return filter;
    }

    private AuthenticationFailureHandler failureHandler() {
        return (request, response, e) -> {
            response.setStatus(INTERNAL_SERVER_ERROR.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            response.getWriter().append("error json response");
        };
    }

}
