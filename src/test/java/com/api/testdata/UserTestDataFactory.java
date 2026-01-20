package com.api.testdata;

import com.api.pojo.User;

public class UserTestDataFactory {

    public static User createUser(){
        User user = User.builder()
                .name("Test")
                .email("Test" + System.currentTimeMillis()+"@gmail.com")
                .gender("male")
                .status("active").build();
        return user;
    }

}
