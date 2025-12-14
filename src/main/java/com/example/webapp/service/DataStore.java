package com.example.webapp.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DataStore {
    private static Map<String, String> sessionData = new ConcurrentHashMap<>();
    private static ThreadLocal<String> userContext = new ThreadLocal<>();
    
    public static void setSessionData(String key, String value) {
        sessionData.put(key, value);
    }
    
    public static String getSessionData(String key) {
        return sessionData.get(key);
    }
    
    public static void setUserContext(String username) {
        userContext.set(username);
    }
    
    public static String getUserContext() {
        return userContext.get();
    }
    
    public static void clearUserContext() {
        userContext.remove();
    }
}