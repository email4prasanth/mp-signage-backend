package com.module.idw_signage.Utils;
/*
    Created At 05/09/2024
    Author @Hubino
 */
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.core.env.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class Https {
    private static final Logger logger = LoggerFactory.getLogger(Https.class);
    @Autowired
    private Environment env;

  private static String endPoint;
    @PostConstruct
    public void init() {
        endPoint =  env.getProperty("auth.endpoint.name");
    }

    public static JsonNode doPost(String auth) {
        logger.info("Https :: doPost :: doPost method Initialized");
        HttpEntity finalEntity = null;
        JsonNode jsonResponse;
        try {
            CloseableHttpClient httpClientPostRequestForCreateLink = HttpClients.createDefault();

            HttpPost httpPostForCreateLink = new HttpPost(endPoint);
            httpPostForCreateLink.setHeader("Content-Type", "application/json");
            httpPostForCreateLink.setHeader("Authorization", auth);
            org.apache.http.HttpResponse postResponse = httpClientPostRequestForCreateLink.execute(httpPostForCreateLink);
            finalEntity = postResponse.getEntity();

            String finalResponseBody = EntityUtils.toString(finalEntity);
            ObjectMapper objectMapper = new ObjectMapper();
            jsonResponse = objectMapper.readTree(finalResponseBody);
        } catch (ClientProtocolException e) {
            logger.error("httpException " + e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("httpException " + e);
            throw new RuntimeException(e);
        } finally {
            logger.info("Https :: doPost :: doPost finally Block executed...");
        }
        return jsonResponse;
    }

}
