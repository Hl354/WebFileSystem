package com.example.jupiter.controller;

import java.util.HashMap;
import java.util.Map;

public class BaseController {

    public Map returnTrueResult(Object data) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 0);
        result.put("data", data);
        return result;
    }

    public Map returnNoDataResult(String msg) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 0);
        result.put("msg", msg);
        return result;
    }

    public Map returnExceptionResult(String msg) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("status", 1);
        result.put("msg", msg);
        return result;
    }

}
