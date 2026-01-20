package com.api.tests;

import com.api.utils.ConfigReader;
import com.api.manager.APIClient;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.HashMap;
import java.util.Map;

public class BaseTest {
    protected Playwright playwright;
    protected APIRequestContext apiRequestContext;
    APIClient apiClient;

    @BeforeClass(alwaysRun = true)
    public void setup(){
        System.setProperty("PLAYWRIGHT_NODEJS_PATH", "C:\\Program Files\\nodejs\\node.exe");
        validateRequiredProperties("base.url","api.token");

        String url = ConfigReader.getProperties("base.url");
        System.out.println("Base URL = " + url);
        String token = ConfigReader.getProperties("api.token");

        playwright= Playwright.create();
        APIRequest apiRequest = playwright.request();
        Map<String, String> inputHeaders = new HashMap<>();
        inputHeaders.put("Content-Type", "application/json");
        inputHeaders.put("Accept", "application/json");
        inputHeaders.put("Authorization", "Bearer " + token);

        apiRequestContext = apiRequest.newContext(new APIRequest.NewContextOptions()
                .setBaseURL(url)
                .setExtraHTTPHeaders(inputHeaders));
        apiClient = new APIClient(apiRequestContext,inputHeaders);
    }

    @AfterClass
    public void tearDown(){
        if(apiRequestContext != null)
            apiRequestContext.dispose();
        if(playwright != null)
            playwright.close();

    }

    public void validateRequiredProperties(String...keys){
        for(String key : keys){
            String value = ConfigReader.getProperties(key);
            if(value == null || value.trim().isEmpty() || value.trim().isBlank()){
                throw new RuntimeException("Property is missing or empty, Property : " + key);
            }
        }
    }
}
