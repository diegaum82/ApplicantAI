package com.applicantai.resumeoptimizer.controller;

import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for handling resume uploads and processing.
 */
@Controller
@RequestMapping("/resumes")
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);
    
    private final ResumeProcessingService resumeProcessingService;
    
    @Autowired
    public ResumeController(ResumeProcessingService resumeProcessingService) {
        this.resumeProcessingService = resumeProcessingService;
    }
    
    /**
     * Shows the upload form.
     * 
     * @return the view name for the upload form
     */
    @GetMapping("/upload")
    public String showUploadForm() {
        return "resume/upload";
    }
    
    /**
     * Handles file upload from the web form.
     * 
     * @param file the uploaded resume file
     * @param redirectAttributes for flash attributes
     * @return redirect to the result page
     */
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a file to upload");
            return "redirect:/resumes/upload";
        }
        
        try {
            ResumeContent resumeContent = resumeProcessingService.processResume(file);
            
            // Store the resume content in session or temporary storage
            // For simplicity, we'll use flash attributes for now
            redirectAttributes.addFlashAttribute("resumeContent", resumeContent);
            
            // Extract extraction quality and method from metadata
            int extractionQuality = 100;
            String extractionMethod = "DIRECT";
            
            if (resumeContent.getMetadata() != null) {
                if (resumeContent.getMetadata().containsKey("extractionQuality")) {
                    extractionQuality = Integer.parseInt(resumeContent.getMetadata().get("extractionQuality"));
                }
                
                if (resumeContent.getMetadata().containsKey("extractionMethod")) {
                    extractionMethod = resumeContent.getMetadata().get("extractionMethod");
                }
            }
            
            redirectAttributes.addFlashAttribute("extractionQuality", extractionQuality);
            redirectAttributes.addFlashAttribute("extractionMethod", extractionMethod);
            redirectAttributes.addFlashAttribute("sections", resumeContent.getSections());
            redirectAttributes.addFlashAttribute("bulletPoints", resumeContent.getBulletPoints());
            
            // Add warnings if any
            if (resumeContent.getMetadata() != null && resumeContent.getMetadata().containsKey("warnings")) {
                redirectAttributes.addFlashAttribute("warnings", resumeContent.getMetadata().get("warnings"));
            }
            
            logger.info("Successfully processed resume: {}", resumeContent.getOriginalFileName());
            return "redirect:/resumes/result";
            
        } catch (IOException e) {
            logger.error("Error processing uploaded file", e);
            redirectAttributes.addFlashAttribute("error", "Error processing file: " + e.getMessage());
            return "redirect:/resumes/upload";
        }
    }
    
    /**
     * Shows the analysis result page.
     * 
     * @param model Spring model for view
     * @return the view name for the result page
     */
    @GetMapping("/result")
    public String showResultPage(Model model) {
        // Check if we have the resume content in flash attributes
        if (!model.containsAttribute("resumeContent")) {
            return "redirect:/resumes/upload";
        }
        
        return "resume/result";
    }
    
    /**
     * API endpoint for file uploads.
     * 
     * @param file the uploaded resume file
     * @return JSON response with processed resume data
     */
    @PostMapping("/api/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiUpload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please select a file to upload");
        }
        
        try {
            ResumeContent resumeContent = resumeProcessingService.processResume(file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("filename", resumeContent.getOriginalFileName());
            response.put("fileSize", resumeContent.getFileSize());
            response.put("sections", resumeContent.getSections());
            response.put("bulletPoints", resumeContent.getBulletPoints());
            
            // Add metadata
            if (resumeContent.getMetadata() != null) {
                response.put("metadata", resumeContent.getMetadata());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("Error processing uploaded file via API", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Error processing file: " + e.getMessage());
        }
    }
} 