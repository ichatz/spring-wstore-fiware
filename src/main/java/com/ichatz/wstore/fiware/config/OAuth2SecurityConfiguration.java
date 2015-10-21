package com.ichatz.wstore.fiware.config;


import com.ichatz.wstore.fiware.util.MyOAuth2RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableOAuth2Client
class OAuth2SecurityConfiguration {
    private static Logger LOGGER = LoggerFactory.getLogger(OAuth2SecurityConfiguration.class);
    @Autowired
    private Environment env;

    @Resource
    @Qualifier("accessTokenRequest")
    private AccessTokenRequest accessTokenRequest;

    @Bean
    @Scope("session")
    public OAuth2ProtectedResourceDetails googleResource() {
        LOGGER.debug("googleResource");
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setId("fiware-oauth-client");
        details.setClientId(env.getProperty("client.id"));
        details.setClientSecret(env.getProperty("client.secret"));
        details.setAccessTokenUri("https://account.lab.fiware.org/oauth2/token");
        details.setUserAuthorizationUri("https://account.lab.fiware.org/oauth2/authorize");
        details.setGrantType(env.getProperty("authorization.code"));
        String commaSeparatedScopes = env.getProperty("auth.scope");
        details.setScope(parseScopes(commaSeparatedScopes));
        details.setPreEstablishedRedirectUri(env.getProperty("preestablished.redirect.url"));
        details.setUseCurrentUri(false);
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);
        return details;
    }

    private List<String> parseScopes(String commaSeparatedScopes) {
        LOGGER.debug("parseScopes:" + commaSeparatedScopes);
        List<String> scopes = new ArrayList<>();
        Collections.addAll(scopes, commaSeparatedScopes.split(","));
        return scopes;
    }

    @Bean
    @Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
    public OAuth2RestTemplate googleRestTemplate() {
        LOGGER.debug("googleRestTemplate");
        return new MyOAuth2RestTemplate(googleResource(), new DefaultOAuth2ClientContext(accessTokenRequest));
    }
}
