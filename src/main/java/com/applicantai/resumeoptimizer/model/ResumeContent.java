package com.applicantai.resumeoptimizer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing the content extracted from a resume.
 * Contains raw text, structured sections, metadata, and additional extracted information.
 */
public class ResumeContent {
    
    private String originalFileName;
    private long fileSize;
    private String rawText;
    private Map<String, String> sections;
    private List<String> bulletPoints;
    private Map<String, String> metadata;
    
    public ResumeContent() {
        this.sections = new HashMap<>();
        this.bulletPoints = new ArrayList<>();
        this.metadata = new HashMap<>();
    }
    
    // Getters and Setters
    
    public String getOriginalFileName() {
        return originalFileName;
    }
    
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }
    
    public long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getRawText() {
        return rawText;
    }
    
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }
    
    public Map<String, String> getSections() {
        return sections;
    }
    
    public void setSections(Map<String, String> sections) {
        this.sections = sections;
    }
    
    public List<String> getBulletPoints() {
        return bulletPoints;
    }
    
    public void setBulletPoints(List<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }
    
    public Map<String, String> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
    
    @Override
    public String toString() {
        return "ResumeContent{" +
                "originalFileName='" + originalFileName + '\'' +
                ", fileSize=" + fileSize +
                ", rawTextLength=" + (rawText != null ? rawText.length() : 0) +
                ", sectionsCount=" + sections.size() +
                ", bulletPointsCount=" + bulletPoints.size() +
                '}';
    }
} 