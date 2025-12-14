package com.example.webapp.service;

import org.springframework.stereotype.Service;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class DataProcessor {
    
    private ExecutorService executor = Executors.newFixedThreadPool(5);
    
    public String processUserData(String input) {
        DataStore.setUserContext(input);
        
        executor.submit(() -> {
            String data = DataStore.getUserContext();
            DataStore.setSessionData("lastProcessed", data);
            performOperation(data);
        });
        
        return "Processing: " + input;
    }
    
    private void performOperation(String data) {
        try {
            Thread.sleep(100);
            String result = executeCommand(data);
            DataStore.setSessionData("result", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String executeCommand(String cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            return "Executed: " + cmd;
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}