package com.ichatz.wstore.fiware.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

/**
 * Implements the functionality of the offerings.
 */
@Controller
public class OffersController {

    private static Logger LOGGER = LoggerFactory.getLogger(OffersController.class);

    private static ObjectMapper OM = new ObjectMapper();

    @Autowired
    private OAuth2RestOperations oauth2RestTemplate;

    @ResponseBody
    @RequestMapping(value = "/offers/list", method = RequestMethod.GET)
    public String getOfferings() throws JsonProcessingException {
        final String url = "http://fff.sparkworks.net/api/offering/offerings?filter=published&action=count&sort=date&state=deleted&start=1&limit=5";

        final HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + oauth2RestTemplate.getAccessToken());

        LOGGER.info("" + oauth2RestTemplate.getAccessToken());

        HttpEntity entity = new HttpEntity(headers);

        final RestTemplate restTemplate = new RestTemplate();

        final HttpEntity<String> response = restTemplate.exchange(
                url, HttpMethod.GET, entity, String.class);

        return response.getBody();
    }


}
