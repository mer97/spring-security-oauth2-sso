package top.leemer.clientorder.common.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.leemer.clientorder.common.bean.AjaxAuthFailureHandler;
import top.leemer.clientorder.common.bean.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LEEMER
 * Create Date: 2019-09-21
 */
@Configuration
@EnableOAuth2Sso
public class OrderSecurityConfig extends WebSecurityConfigurerAdapter {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AjaxAuthFailureHandler ajaxAuthFailureHandler;

    @Value("${app.sso.login.url}")
    private String ssoLoginUrl;

    @Value("${app.sso.logout.url}")
    private String ssoLogoutUrl;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
                .logoutUrl("/api/v1/logout")
                .logoutSuccessUrl(ssoLogoutUrl)
                .invalidateHttpSession(true)
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

}
