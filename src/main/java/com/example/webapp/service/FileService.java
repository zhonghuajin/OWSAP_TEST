package com.example.webapp.service;

import org.springframework.stereotype.Service;
import java.io.*;
import java.nio.file.*;

@Service
public class FileService {
    
    private String baseDir = "/tmp/uploads/";
    
    public String readFile(String filename) throws IOException {
        Path path = Paths.get(baseDir + filename);
        return new String(Files.readAllBytes(path));
    }
    
    public void writeFile(String filename, String content) throws IOException {
        Path path = Paths.get(baseDir + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, content.getBytes());
    }
    
    public String loadResource(String resource) {
        try {
            FileInputStream fis = new FileInputStream(resource);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            return new String(data);
        } catch (Exception e) {
            return "Error loading resource";
        }
    }
    
    public Object deserializeData(byte[] data) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}