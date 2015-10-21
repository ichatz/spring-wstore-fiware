/**
 * ****************************************************************************
 * Cloud Foundry
 * Copyright (c) [2009-2014] Pivotal Software, Inc. All Rights Reserved.
 * <p>
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 * <p>
 * This product includes a number of subcomponents with
 * separate copyright notices and license terms. Your use of these
 * subcomponents is subject to the terms and conditions of the
 * subcomponent's license, as noted in the LICENSE file.
 * *****************************************************************************
 */
package com.ichatz.wstore.fiware.security;

import com.ichatz.wstore.fiware.util.MyAccessTokenConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.util.Map;

/**
 * Copied from Spring Security OAuth2 to support the custom format for a Google Token which is different from what Spring supports
 */
public class TokenServices extends RemoteTokenServices {

    private static Logger LOGGER = LoggerFactory.getLogger(TokenServices.class);

    private RestOperations restTemplate;

    @Value("${checkTokenEndpointUrl}")
    private String checkTokenEndpointUrl;

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    private AccessTokenConverter tokenConverter = new MyAccessTokenConverter();

    public TokenServices() {
        restTemplate = new RestTemplate();
        ((RestTemplate) restTemplate).setErrorHandler(new DefaultResponseErrorHandler() {
            @Override
            // Ignore 400
            public void handleError(ClientHttpResponse response) throws IOException {
                if (response.getRawStatusCode() != 400) {
                    super.handleError(response);
                }
            }
        });
    }

    public void setRestTemplate(RestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setCheckTokenEndpointUrl(String checkTokenEndpointUrl) {
        this.checkTokenEndpointUrl = checkTokenEndpointUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setAccessTokenConverter(org.springframework.security.oauth2.provider.token.AccessTokenConverter accessTokenConverter) {
        this.tokenConverter = accessTokenConverter;
    }

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {
        Map<String, Object> checkTokenResponse = checkToken(accessToken);

        if (checkTokenResponse.containsKey("error")) {
            LOGGER.debug("check_token returned error: " + checkTokenResponse.get("error"));
            throw new InvalidTokenException(accessToken);
        }

        transformNonStandardValuesToStandardValues(checkTokenResponse);

        Assert.state(checkTokenResponse.containsKey("client_id"), "Client id must be present in response from auth server");
        return tokenConverter.extractAuthentication(checkTokenResponse);
    }

    private Map<String, Object> checkToken(String accessToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
        String accessTokenUrl = new StringBuilder(checkTokenEndpointUrl).append("?access_token=").append(accessToken).toString();
        LOGGER.info(accessTokenUrl);
        return getForMap(accessTokenUrl, formData, headers);
    }

    private void transformNonStandardValuesToStandardValues(Map<String, Object> map) {
        LOGGER.debug("Original map = " + map);
        map.put("client_id", map.get("app_id")); // Google sends 'client_id' as 'issued_to'
        map.put("user_name", map.get("id")); // Google sends 'user_name' as 'user_id'
        LOGGER.debug("Transformed = " + map);
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {
        String authorizationString = clientId + ":" + clientSecret;
        final String encodedBytes = new BASE64Encoder().encode(authorizationString.getBytes());
        final String creds = encodedBytes.replaceAll("\n", "");
        LOGGER.info(authorizationString + " = " + creds);
        return "Basic " + creds;
    }

    private Map<String, Object> getForMap(String path, MultiValueMap<String, String> formData, HttpHeaders headers) {
        if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        LOGGER.info(headers.getContentType().toString());
        ParameterizedTypeReference<Map<String, Object>> map = new ParameterizedTypeReference<Map<String, Object>>() {
        };
        return restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<>(formData, headers), map).getBody();
    }
}
