package top.leemer.ssoserver.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import top.leemer.ssoserver.common.bean.AjaxAuthFailureHandler;
import top.leemer.ssoserver.common.security.service.MyLdapUserDetailsMapper;

import java.util.Arrays;

/**
 * @author LEEMER
 * Create Date: 2019-09-20
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MyLdapUserDetailsMapper whLdapUserDetailsMapper;

    /**
     * ajax请求失败处理器。
     */
    @Autowired
    private AjaxAuthFailureHandler ajaxAuthFailureHandler;

    @Value("${app.ldap.url}")
    private String ldapUrl;

    @Value("${app.ldap.domain}")
    private String ldapDomain;

    //定义AD认证方法
    @Bean
    public ActiveDirectoryLdapAuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        final ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(
                ldapDomain, ldapUrl
        );
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        provider.setUseAuthenticationRequestCredentials(true);

        //设置角色权限
        provider.setUserDetailsContextMapper(whLdapUserDetailsMapper);
        return provider;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/css/**", "/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/login")
                .failureHandler(ajaxAuthFailureHandler)
                .and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .csrf()
                .disable()
                .cors();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //配置多种认证方式，即多个AuthenticationProvider（用ProviderManager的Arrays.asList添加多个认证方法）
    @Override
    protected AuthenticationManager authenticationManager(){
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(activeDirectoryLdapAuthenticationProvider()));
        //不擦除认证密码，擦除会导致TokenBasedRememberMeServices因为找不到Credentials再调用UserDetailsService而抛出UsernameNotFoundException
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
    }

}
