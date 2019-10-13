package top.leemer.ssoserver.common.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

/**
 * @author LEEMER
 * Create Date: 2019-09-20
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

//    @Autowired
//    private DataSource dataSource;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("permitAll()").tokenKeyAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//        clients.jdbc(dataSource);
        clients.inMemory()
                .withClient("OrderManagement")
                .secret("$2a$10$8yVwRGY6zB8wv5o0kRgD0ep/HVcvtSZUZsYu/586Egxc1hv3cI9Q6")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8082/orderSystem/login")
                .accessTokenValiditySeconds(60) //s
                .autoApprove(true)
                .scopes("all")
                .and()
                .withClient("UserManagement")
                .secret("$2a$10$ZRmPFVgE6o2aoaK6hv49pOt5BZIKBDLywCaFkuAs6zYmRkpKHgyuO")
                .authorizedGrantTypes("authorization_code", "refresh_token")
                .redirectUris("http://localhost:8081/userSystem/login")
                .accessTokenValiditySeconds(60)
                .autoApprove(true)
                .scopes("all");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints){
        endpoints.accessTokenConverter(jwtAccessTokenConverter())
                .tokenStore(jwtTokenStore());
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey("leemer");   // JWT key-value
        return jwtAccessTokenConverter;
    }

}
