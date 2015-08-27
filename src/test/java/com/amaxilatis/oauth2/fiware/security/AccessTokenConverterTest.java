package com.amaxilatis.oauth2.fiware.security;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.amaxilatis.oauth2.fiware.util.MyAccessTokenConverter;
import org.junit.Test;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;

import java.util.Map;

public class AccessTokenConverterTest {
    private DefaultAccessTokenConverter accessTokenConverter = new MyAccessTokenConverter();
//    private DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();

    @Test
    public void shouldExtractAuthenticationAndScopesWhenScopeIsString() throws Exception {
        Map<String, Object> map = newHashMap();
        map.put(org.springframework.security.oauth2.provider.token.AccessTokenConverter.SCOPE, "a b");
        OAuth2Authentication authentication = accessTokenConverter.extractAuthentication(map);

        assertThat(authentication.getOAuth2Request().getScope(), containsInAnyOrder("a", "b"));
    }

    @Test
    public void shouldExtractAuthenticationAndScopesWhenScopeIsCollection() throws Exception {
        Map<String, Object> map = newHashMap();
        map.put(org.springframework.security.oauth2.provider.token.AccessTokenConverter.SCOPE, newArrayList("a", "b"));
        OAuth2Authentication authentication = accessTokenConverter.extractAuthentication(map);

        assertThat(authentication.getOAuth2Request().getScope(), containsInAnyOrder("a", "b"));
    }
}
