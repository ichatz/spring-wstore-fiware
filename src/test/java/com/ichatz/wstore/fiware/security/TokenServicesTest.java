package com.ichatz.wstore.fiware.security;

import com.ichatz.wstore.fiware.util.MyAccessTokenConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: saket.kumar
 * Date: 26/09/2014
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
@RunWith(MockitoJUnitRunner.class)
public class TokenServicesTest {

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private ResponseEntity<Map> response;
    @Mock
    private org.springframework.security.oauth2.provider.token.AccessTokenConverter accessTokenConverter;
    @Mock
    private OAuth2Authentication mockAuthentication;
    @Mock
    private AuthorityGranter authorityGranter;
    @Captor
    private ArgumentCaptor<Map<String, String>> mapCapture;

    private TokenServices tokenServices;

    @Before
    public void setUp() throws Exception {
        tokenServices = new TokenServices();
    }

    @Test
    public void shouldLoadAuthenticationAndTransformValuesToStandardValues() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("app_id", "blh");
        body.put("id", "user@domain.google.com");
        body.put("email", "user@domain.google.com");
        given(response.getBody()).willReturn(body);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class))).willReturn(response);
        given(accessTokenConverter.extractAuthentication(body)).willReturn(mockAuthentication);
        tokenServices.setRestTemplate(restTemplate);
        tokenServices.setCheckTokenEndpointUrl("//");
        tokenServices.setAccessTokenConverter(accessTokenConverter);

        OAuth2Authentication authentication = tokenServices.loadAuthentication(null);

        assertThat(authentication, is(mockAuthentication));
        verify(accessTokenConverter).extractAuthentication(mapCapture.capture());
        Map<String, String> value = mapCapture.getValue();
        System.out.println(mapCapture.getValue());
        assertThat(value.get("user_name"), notNullValue());
        assertThat(value.get("user_name"), is("user@domain.google.com"));
        assertThat(value.get("id"), notNullValue());
        assertThat(value.get("id"), is("user@domain.google.com"));
        assertThat(value.get("client_id"), notNullValue());
        assertThat(value.get("client_id"), is("blh"));
        assertThat(value.get("app_id"), notNullValue());
        assertThat(value.get("app_id"), is("blh"));
    }

    @Test
    public void shouldLoadAuthenticationAndTransformValuesToStandardValuesAndAddDomainRole() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("issued_to", "blh");
        body.put("user_id", "user@domain.google.com");
        body.put("email", "user@domain.google.com");
        given(response.getBody()).willReturn(body);
        given(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(ParameterizedTypeReference.class))).willReturn(response);
        tokenServices.setRestTemplate(restTemplate);
        tokenServices.setCheckTokenEndpointUrl("//");
        DefaultUserAuthenticationConverter defaultUserAuthenticationConverter = new DefaultUserAuthenticationConverter();
        defaultUserAuthenticationConverter.setAuthorityGranter(authorityGranter);
        MyAccessTokenConverter realAccessTokenConverter = new MyAccessTokenConverter();
        realAccessTokenConverter.setUserTokenConverter(defaultUserAuthenticationConverter);
        tokenServices.setAccessTokenConverter(realAccessTokenConverter);

        OAuth2Authentication authentication = tokenServices.loadAuthentication(null);

        assertThat(authentication, notNullValue());
        verify(authorityGranter).getAuthorities(anyMap());
    }
}
