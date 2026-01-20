package com.api.services;

import com.api.manager.APIClient;
import com.api.manager.APIResponseWrapper;
import com.api.pojo.User;
import com.api.utils.Endpoints;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.APIResponse;

public class UserServiceOperations {

    private APIClient apiClient;

    public UserServiceOperations(APIClient apiClient){
        this.apiClient = apiClient;
    }

    public APIResponseWrapper createUser(User user) throws JsonProcessingException {
        return apiClient.post(Endpoints.USERS, user);
    }

    public APIResponseWrapper getUserById(int userId) throws JsonProcessingException {
        return apiClient.get(Endpoints.USER_BY_ID + "/" + userId);
    }

    public APIResponseWrapper getUsers() throws JsonProcessingException {
        return apiClient.get(Endpoints.USERS);
    }


}
