package com.api.manager;

import com.api.manager.APIResponseWrapper;

public final class APITestContext {

    private static final ThreadLocal<RequestDetails> REQUEST = new ThreadLocal<>();
    private static final ThreadLocal<ResponseDetails> RESPONSE = new ThreadLocal<>();

    private APITestContext() {}

    public static void setRequest(RequestDetails requestDetails){
        REQUEST.set(requestDetails);
    }

    public static void setResponse(ResponseDetails responseDetails){
        RESPONSE.set(responseDetails);
    }

    public static RequestDetails getRequest(){
        return REQUEST.get();
    }

    public static ResponseDetails getResponse(){
        return RESPONSE.get();
    }

    public static void clear() {
        REQUEST.remove();
        RESPONSE.remove();
    }
}
