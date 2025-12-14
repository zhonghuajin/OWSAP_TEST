package com.example.webapp.controller;

import com.example.webapp.model.User;
import com.example.webapp.repository.UserRepository;
import com.example.webapp.service.DataProcessor;
import com.example.webapp.service.DataStore;
import com.example.webapp.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Base64;

@Controller
public class WebController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DataProcessor dataProcessor;
    
    @Autowired
    private FileService fileService;
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = userRepository.findByCredentials(username, password);
        if (user != null) {
            session.setAttribute("user", username);
            session.setAttribute("role", user.getRole());
            return "Login successful for " + username;
        }
        return "Login failed";
    }
    
    @GetMapping("/profile")
    public String profile(@RequestParam String name, Model model) {
        model.addAttribute("username", name);
        return "profile";
    }
    
    @PostMapping("/update")
    @ResponseBody
    public String updateProfile(@RequestParam String data) {
        return dataProcessor.processUserData(data);
    }
    
    @GetMapping("/file")
    @ResponseBody
    public String getFile(@RequestParam String path) {
        try {
            return fileService.readFile(path);
        } catch (Exception e) {
            return "File not found";
        }
    }
    
    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam String filename, @RequestParam String content) {
        try {
            fileService.writeFile(filename, content);
            return "File uploaded: " + filename;
        } catch (Exception e) {
            return "Upload failed";
        }
    }
    
    @GetMapping("/resource")
    @ResponseBody
    public String loadResource(@RequestParam String name) {
        return fileService.loadResource(name);
    }
    
    @PostMapping("/deserialize")
    @ResponseBody
    public String deserialize(@RequestParam String data) {
        try {
            byte[] bytes = Base64.getDecoder().decode(data);
            Object obj = fileService.deserializeData(bytes);
            return "Deserialized: " + obj;
        } catch (Exception e) {
            return "Deserialization failed";
        }
    }
    
    @GetMapping("/admin")
    @ResponseBody
    public String adminPanel(HttpSession session) {
        String role = (String) session.getAttribute("role");
        if ("admin".equals(role)) {
            return "Welcome to admin panel";
        }
        return "Access denied";
    }
    
    @PostMapping("/search")
    @ResponseBody
    public String search(@RequestParam String query) {
        String result = DataStore.getSessionData(query);
        return "Search results for: " + query + " - " + result;
    }
}