package com.api.manager;

import com.api.pojo.APIErrorMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

import java.util.List;
import java.util.Map;

public class APIResponseWrapper {

    private final APIResponse apiResponse;
    private final String apiResponseBody;
    ObjectMapper objectMapper;
    public APIResponseWrapper(APIResponse apiResponse){
        this.apiResponse = apiResponse;
        this.apiResponseBody = apiResponse.text();
        objectMapper = new ObjectMapper();
        logResponse();
    }

    private void logResponse() {
        System.out.println("===== API RESPONSE =====");
        System.out.println("Status  : " + apiResponse.status());
        System.out.println("Status Text : " + apiResponse.statusText());

        System.out.println("Headers :");
        for (Map.Entry<String, String> entry : apiResponse.headers().entrySet()) {
            System.out.println("  " + entry.getKey() + " : " + entry.getValue());
        }

        System.out.println("Body    :");
        System.out.println(apiResponseBody);
        System.out.println("========================");
    }

    public int getResponseStatus(){
        return apiResponse.status();
    }
    public String getResponseStatusText(){
        return apiResponse.statusText();
    }
    public String getResponseBody(){
        return apiResponseBody;
    }
    public Map<String, String> getResponseHeaders() {
        return apiResponse.headers();
    }

    public void assertStatus(int expectedStatus, String expectedStatusText){
        if(apiResponse.status() != expectedStatus || (!apiResponse.statusText().equals(expectedStatusText))){
            throw  new RuntimeException("Status is not as Expected.\nExpected Status : "+expectedStatus
                    +"\nActual Status : " + apiResponse.status()
                    + "\nExpected Status Text : " + expectedStatusText
                    + "\nActual Status Text : " + apiResponse.statusText());
        }
    }

    public void assertHeader(String headerkey, String headerValue){
        String value = apiResponse.headers().get(headerkey);
        if(!value.equals(headerValue)){
            throw new RuntimeException("Header is not as expected.\n" +
                    "Expected " + headerkey + " : " + headerValue +
                    "\nActual " + headerkey + " : " + value);
        }
    }

    public void assertError(String field, String errorMessage) throws JsonProcessingException {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, APIErrorMessage.class);
        List<APIErrorMessage> errorMessages = objectMapper.readValue(apiResponse.text(),type);
        boolean matchFound = false;
        for(APIErrorMessage apiErrorMessage : errorMessages){
            if(field.equals(apiErrorMessage.getField()) && errorMessage.equals(apiErrorMessage.getMessage())){
                matchFound = true;
            }
        }
        if(!matchFound){
            throw new RuntimeException(
                    "Expected error not found.\n" +
                            "Expected field   : " + field + "\n" +
                            "Expected message : " + errorMessage + "\n" +
                            "Actual errors    : " + apiResponse.text()
            );
        }
    }

    public <T> T convertResponseToPoJo(Class<T> objTypeName) throws JsonProcessingException {
        T pojo = objectMapper.readValue(apiResponse.text(),objTypeName);
        return pojo;
    }

    public <T> List<T> convertResponseToList(Class<T> objTypeName) throws JsonProcessingException {
        JavaType type = objectMapper.getTypeFactory().constructCollectionType(List.class, objTypeName);
        return objectMapper.readValue(apiResponse.text(), type);
    }


}
