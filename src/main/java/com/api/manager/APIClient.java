package com.api.manager;

import com.api.utils.ConfigReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class APIClient {

    private APIRequestContext apiRequestContext;
    ObjectMapper objectMapper = new ObjectMapper();
    private Map<String, String> headers;
    private int maxRetires;

    public APIClient(APIRequestContext apiRequestContext, Map<String, String> headers){
        this.apiRequestContext = apiRequestContext;
        this.headers = headers;
        maxRetires = Integer.parseInt(ConfigReader.getProperties("api.maxRetries"));
    }

    public APIResponseWrapper get(String endpoint) throws JsonProcessingException {
        int retry = 0;
        APIResponse apiResponse = null;
        logRequest("GET", endpoint, null);
        String responseBody = null;
        int status = 0;
        String statusText = null;
        RequestDetails request = new RequestDetails();
        request.method = "GET";
        request.endpoint = endpoint;
        request.headers = headers;
        APITestContext.setRequest(request);
        while (retry < maxRetires){
            apiResponse = apiRequestContext.get(endpoint);
            responseBody = apiResponse.text();
            status = apiResponse.status();
            statusText = apiResponse.statusText();
            if (status > 500){
                System.out.println("Response from retry " + retry + " is = \n"+
                        "POST Failed\n" +
                        "Endpoint : "+ endpoint + "\n" +
                        "Status : " + status +"\n" +
                        "StatusText : " + statusText +"\n" +
                        "Response : " + responseBody
                );
            }
            else
                break;
            retry++;
        }
        ResponseDetails response = new ResponseDetails();
        response.status = apiResponse.status();
        response.statusText = apiResponse.statusText();
        response.body = apiResponse.text();
        response.headers = apiResponse.headers();
        APITestContext.setResponse(response);

        return new APIResponseWrapper(apiResponse);
    }
    public APIResponseWrapper post(String endpoint, Object body) throws JsonProcessingException {
        int retry = 0;
        APIResponse apiResponse = null;
        logRequest("POST",endpoint,body);
        String requestBody = objectMapper.writeValueAsString(body);
        String responseBody = null;
        int status = 0;
        String statusText = null;
        RequestDetails request = new RequestDetails();
        request.method = "POST";
        request.endpoint = endpoint;
        request.body = body;
        request.headers = headers;
        APITestContext.setRequest(request);
        while(retry < maxRetires){
            apiResponse = apiRequestContext.post(endpoint, RequestOptions.create().setData(body));
            responseBody = apiResponse.text();
            status = apiResponse.status();
            statusText = apiResponse.statusText();
            if(status > 500){
                System.out.println("Response from retry " + retry + " is = \n"+
                        "POST Failed\n" +
                        "Endpoint : "+ endpoint + "\n" +
                        "Status : " + status +"\n" +
                        "StatusText : " + statusText +"\n" +
                        "RequestBody : " + requestBody +"\n" +
                        "Response : " + responseBody
                        );
            }
            else
                break;
            retry++;
        }
//        if(status < 200 || status >= 300 ){
//            throw new RuntimeException("POST Failed\n" +
//                    "Endpoint : "+ endpoint + "\n" +
//                    "Status : " + status +"\n" +
//                    "StatusText : " + statusText +"\n" +
//                    "RequestBody : " + requestBody +"\n" +
//                    "Response : " + responseBody);
//        }
        ResponseDetails response = new ResponseDetails();
        response.status = apiResponse.status();
        response.statusText = apiResponse.statusText();
        response.body = apiResponse.text();
        response.headers = apiResponse.headers();
        APITestContext.setResponse(response);

        return new APIResponseWrapper(apiResponse);
    }
    public APIResponseWrapper put(String endpoint, String body) throws JsonProcessingException {
        int retry = 0;
        APIResponse apiResponse = null;
        logRequest("PUT",endpoint,body);
        String requestBody = objectMapper.writeValueAsString(body);
        String responseBody = null;
        int status = 0;
        String statusText = null;
        RequestDetails request = new RequestDetails();
        request.method = "PUT";
        request.endpoint = endpoint;
        request.body = body;
        request.headers = headers;
        APITestContext.setRequest(request);
        while(retry < maxRetires){
            apiResponse = apiRequestContext.put(endpoint, RequestOptions.create().setData(body));
            responseBody = apiResponse.text();
            status = apiResponse.status();
            statusText = apiResponse.statusText();
            if (status > 500){
                System.out.println("Response from retry " + retry + " is = \n"+
                        "POST Failed\n" +
                        "Endpoint : "+ endpoint + "\n" +
                        "Status : " + status +"\n" +
                        "StatusText : " + statusText +"\n" +
                        "RequestBody : " + requestBody +"\n" +
                        "Response : " + responseBody
                );
            }
            else
                break;
            retry++;
        }
        if(status < 200 || status >= 300 ){
            throw new RuntimeException("POST Failed\n" +
                    "Endpoint : "+ endpoint + "\n" +
                    "Status : " + status +"\n" +
                    "Response : " + responseBody);
        }
        ResponseDetails response = new ResponseDetails();
        response.status = apiResponse.status();
        response.statusText = apiResponse.statusText();
        response.body = apiResponse.text();
        response.headers = apiResponse.headers();
        APITestContext.setResponse(response);

        return new APIResponseWrapper(apiResponse);
    }
//    public APIResponse delete(String endpoint) {
//        return apiRequestContext.delete(endpoint);
//    }

    public void validateResponseSchema(APIResponse apiResponse, String schemaFileName) throws IOException {
        try {
            String responseBody = apiResponse.text();
            int status = apiResponse.status();
            if(status < 200 || status >= 300 ){
                throw new RuntimeException("POST Failed\n" +
                        "Status : " + status +"\n" +
                        "Response : " + responseBody);
            }

            JsonNode responseJson = objectMapper.readTree(apiResponse.text());
            String schemaPath = "./src/test/resources/" + schemaFileName;
            String schemaContent = Files.readString(Path.of(schemaPath));

            JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

            JsonSchema schema = factory.getSchema(schemaContent);
            Set<ValidationMessage> errors = schema.validate(responseJson);

            if (!errors.isEmpty()) {
                StringBuilder errorMsg = new StringBuilder("❌ Schema validation failed for " + schemaFileName + ":");
                for (ValidationMessage msg : errors) {
                    errorMsg.append("\n - ").append(msg.getMessage());
                }
                throw new RuntimeException(errorMsg.toString());
            }
            System.out.println("✅ Schema validation passed: " + schemaFileName);

        } catch (Exception e) {
            throw new RuntimeException("Schema Validation Error: " + e.getMessage());
        }
    }

    private void logRequest(String method, String endpoint, Object body) {
        System.out.println("===== API REQUEST =====");
        System.out.println("Method   : " + method);
        System.out.println("Endpoint : " + endpoint);

        System.out.println("Headers  :");
        headers.forEach((k, v) ->
                System.out.println("  " + k + " : " + v));

        if (body != null) {
            System.out.println("Body     : " + body);
        }

        System.out.println("=======================");
    }

}
