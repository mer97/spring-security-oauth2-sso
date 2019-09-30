package top.leemer.ssoserver.common.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.leemer.ssoserver.common.bean.AjaxAuthFailureHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LEEMER
 * Create Date: 2019-09-20
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static Map<String, String> USER_MAP;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 模拟数据库用户名和密码
     */
    static {
        USER_MAP = new HashMap();
        USER_MAP.put("zhangsan", "123456");
        USER_MAP.put("lisi", "123456");
    }

    /**
     * ajax请求失败处理器。
     */
    @Autowired
    private AjaxAuthFailureHandler ajaxAuthFailureHandler;

    /**
     * 验证用户名、密码和授权。
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetailsService userDetailsService() throws UsernameNotFoundException {
        return (username) -> {
            if (USER_MAP.get(username) == null) {
                throw new UsernameNotFoundException("User Not Found: " + username);
            }
            /**
             * 用户授权，用户名为lisi的拥有访问订单列表的权限
             */
            List simpleGrantedAuthorities = new ArrayList<>();
            if ("lisi".equals(username)){
                simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ORDER"));
            }
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("ADMIN"));
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority("USER"));

            return User.withUsername(username)
                    .password(passwordEncoder.encode(USER_MAP.get(username)))
                    .authorities(simpleGrantedAuthorities)
                    .build();
        };
    }

    /**
     * 配置自定义验证用户名、密码和授权的服务。
     * @param authenticationManagerBuilder
     * @throws Exception
     */
    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder)
            throws Exception {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
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
                .and().csrf().disable().cors();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
