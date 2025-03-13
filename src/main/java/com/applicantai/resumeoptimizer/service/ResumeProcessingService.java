package com.applicantai.resumeoptimizer.service;

import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * Service interface for processing and extracting content from resume files.
 */
public interface ResumeProcessingService {

    /**
     * Processes a resume file and extracts content from it.
     *
     * @param file the resume file to process
     * @return ResumeContent containing the extracted data
     * @throws IOException if there is an error processing the file
     */
    ResumeContent processResume(MultipartFile file) throws IOException;
    
    /**
     * Identifies skills from resume content
     * 
     * @param content The extracted resume content
     * @return Set of skills identified in the resume
     */
    Set<Skill> identifySkills(ResumeContent content);
    
    /**
     * Detects sections in the resume (e.g., experience, education, skills)
     * 
     * @param content The raw text content from the resume
     * @return ResumeContent with populated sections
     */
    ResumeContent detectSections(String content);
    
    /**
     * Evaluates the quality of text extraction
     * 
     * @param content The extracted content
     * @return Quality score (0-100)
     */
    int evaluateExtractionQuality(ResumeContent content);
} 