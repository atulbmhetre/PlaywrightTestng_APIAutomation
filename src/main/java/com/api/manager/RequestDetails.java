package com.api.manager;

import java.util.Map;

public class RequestDetails {
    public String method;
    public String endpoint;
    public Object body;
    public Map<String, String> headers;
}
