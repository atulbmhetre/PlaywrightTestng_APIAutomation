package com.api.tests;

import com.api.assertions.SoftAssertions;
import com.api.manager.APIResponseWrapper;
import com.api.pojo.APIErrorMessage;
import com.api.reports.Log;
import com.api.services.UserServiceOperations;
import com.api.testdata.UserTestDataFactory;
import com.api.tests.tags.TestTags;
import com.api.utils.Endpoints;
import com.api.pojo.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

public class UserTests extends BaseTest{

    private UserServiceOperations userServiceOperations;

    @BeforeClass(alwaysRun = true)
    public void inituserServiceOperations(){
        userServiceOperations = new UserServiceOperations(apiClient);
    }



    @Test(groups = {"smoke", "positive", TestTags.MT_3457})
    public void getGoRestUsersTest() throws JsonProcessingException {
        Log.info("Get Users api to be called.");
        APIResponseWrapper apiResponse = userServiceOperations.getUsers();
        Log.info("Status and Header to be verified.");
        apiResponse.assertStatus(200,"OK");
        apiResponse.assertHeader("content-type","application/json; charset=utf-8");
        List<User> users = apiResponse.convertResponseToList(User.class);
        Log.info("Get Users response to be verified.");
        Assert.assertTrue(users.size() > 0);
        Assert.assertNotNull(users.get(0).getId());
    }

    @Test(groups = {"regression", TestTags.MT_3425})
    public void endToEndUserWorkflowTest() throws JsonProcessingException {

        Log.info("User data to be created");
        User user = UserTestDataFactory.createUser();
        Log.info("Create Users api to be called.");
        APIResponseWrapper apiResponse = userServiceOperations.createUser(user);
        apiResponse.assertStatus(201, "Created");
        User createdUser = apiResponse.convertResponseToPoJo(User.class);
        int userId = createdUser.getId();
        APIResponseWrapper GET_apiResponse = userServiceOperations.getUserById(userId);
        GET_apiResponse.assertStatus(200, "OK");
        User fetchedUser = GET_apiResponse.convertResponseToPoJo(User.class);
        SoftAssertions softAssert = new SoftAssertions();
        softAssert.equals(fetchedUser.getName(), "hjgjhgjggjuutu","Name of the user should not mismatch."); //user.getName()
        softAssert.equals(fetchedUser.getEmail(), "agjgjgjgj","Email of the user should not mismatch."); //user.getEmail()
        softAssert.assertAll();
    }

    @Test(groups = {"regression", TestTags.MT_2341})
    public void verifySchemaOfUserResponse() throws IOException {
        User user = UserTestDataFactory.createUser();
        APIResponse apiResponse = apiRequestContext.post(Endpoints.USERS, RequestOptions.create().setData(user));
        apiClient.validateResponseSchema((APIResponse) apiResponse,"user-schema.json");
    }

    @Test(groups = {"smoke", "negative", TestTags.MT_3456})
    public void createUserWithInvalidEmailAdress() throws JsonProcessingException {
        User user = UserTestDataFactory.createUser();
        user.setEmail("invalid@@gmail.com");
        APIResponseWrapper apiResponse = userServiceOperations.createUser(user);
        List<APIErrorMessage> apiErrorMessages = apiResponse.convertResponseToList(APIErrorMessage.class);
        apiResponse.assertStatus(422,"Unprocessable Entity");
        apiResponse.assertError("email","is invalid");
    }
}
