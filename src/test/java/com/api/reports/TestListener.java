package com.api.reports;

import com.api.manager.APIResponseWrapper;
import com.api.manager.APITestContext;
import com.api.manager.RequestDetails;
import com.api.manager.ResponseDetails;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {
    private static final ExtentReports extent = ExtentManager.getExtent();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(
                result.getMethod().getMethodName()
        );
        ExtentTestManager.setTest(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTestManager.getTest().pass("Test Passed.");
        ExtentTestManager.unload();
        APITestContext.clear();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTestManager.getTest().fail(result.getThrowable());
        RequestDetails req = APITestContext.getRequest();
        ResponseDetails res = APITestContext.getResponse();

        if (req != null) {
            Log.info("===== API REQUEST =====");
            Log.info("Method   : " + req.method);
            Log.info("Endpoint : " + req.endpoint);
            Log.info("Headers  : " + req.headers);
            Log.info("Body     : " + req.body);
        }

        if (res != null) {
            Log.info("===== API RESPONSE =====");
            Log.info("Status   : " + res.status + " " + res.statusText);
            Log.info("Headers  : " + res.headers);
            Log.info("Body     : " + res.body);
        }
        ExtentTestManager.unload();
        APITestContext.clear();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTestManager.getTest().skip("Test skipped.");
        ExtentTestManager.unload();
        APITestContext.clear();
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

}
