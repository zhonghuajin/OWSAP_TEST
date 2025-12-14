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
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataProcessor dataProcessor;

    @Autowired
    private FileService fileService;

    // 注入 EntityManager 以便执行原生动态 SQL
    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/")
    public String home() {

        return "index";
    }

    @PostMapping("/login")
    @ResponseBody
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session) {

        // ---------------------------------------------------------
        // [VULNERABLE CODE] 故意引入 SQL 注入漏洞的代码
        // ---------------------------------------------------------
        // 直接使用字符串拼接，这是导致 SQL 注入的根源
        String sql = "SELECT * FROM users WHERE username = '" + username + "' AND password = '" + password + "'";
        try {
            // 执行原生 SQL 查询
            Query query = entityManager.createNativeQuery(sql, User.class);
            // 获取结果列表
            List<User> users = query.getResultList();
            // 如果列表不为空，说明登录成功（或者注入成功绕过了验证）
            if (!users.isEmpty()) {

                // 获取第一个匹配的用户
                User user = users.get(0);
                session.setAttribute("user", user.getUsername());
                session.setAttribute("role", user.getRole());
                return "Login successful for " + user.getUsername();
            }
        } catch (Exception e) {

            // 发生异常（如 SQL 语法错误）时返回失败
            e.printStackTrace();
            return "Login failed (Error)";
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