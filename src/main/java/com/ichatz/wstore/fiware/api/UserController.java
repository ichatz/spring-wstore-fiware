package com.ichatz.wstore.fiware.api;

import com.ichatz.wstore.fiware.model.FiwareProfile;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    private static Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static ObjectMapper OM = new ObjectMapper();
    @Autowired
    private OAuth2RestOperations oauth2RestTemplate;

    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public FiwareProfile findUsersStartingWithPrefix() throws JsonProcessingException {
        FiwareProfile profile = getProfile();
        LOGGER.info("Google Profile Data {}", OM.writerWithDefaultPrettyPrinter().writeValueAsString(profile));
        return profile;
    }

    private FiwareProfile getProfile() {
        LOGGER.debug("get profile");
        String url = "https://account.lab.fiware.org/user?access_token=" + oauth2RestTemplate.getAccessToken();
        ResponseEntity<FiwareProfile> forEntity = oauth2RestTemplate.getForEntity(url, FiwareProfile.class);
        return forEntity.getBody();
    }
}
