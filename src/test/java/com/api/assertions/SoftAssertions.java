package com.api.assertions;

import com.api.manager.APIResponseWrapper;
import org.testng.asserts.SoftAssert;

public class SoftAssertions {

    private final SoftAssert softAssert = new SoftAssert();

    public void notNull(Object actual, String message) {
        softAssert.assertNotNull(actual, message);
    }

    public void equals(Object actual, Object expected, String message) {
        softAssert.assertEquals(actual, expected, message);
    }

    public void isTrue(boolean condition, String message) {
        softAssert.assertTrue(condition, message);
    }

    public void isFalse(boolean condition, String message) {
        softAssert.assertFalse(condition, message);
    }

    public void assertAll() {
        softAssert.assertAll();
    }

}
