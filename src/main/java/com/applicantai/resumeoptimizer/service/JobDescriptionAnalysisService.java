package com.applicantai.resumeoptimizer.service;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.Skill;

import java.util.Map;
import java.util.Set;

/**
 * Service interface for analyzing job descriptions and extracting relevant information.
 */
public interface JobDescriptionAnalysisService {
    
    /**
     * Extracts required skills from a job description text.
     *
     * @param jobDescription The job description text to analyze
     * @return Set of skills identified in the job description
     */
    Set<Skill> extractRequiredSkills(String jobDescription);
    
    /**
     * Extracts required skills from a JobDescription entity.
     *
     * @param jobDescription The JobDescription entity to analyze
     * @return Set of skills identified in the job description
     */
    Set<Skill> extractRequiredSkills(JobDescription jobDescription);
    
    /**
     * Extracts key phrases from a job description with their importance scores.
     *
     * @param jobDescription The job description text to analyze
     * @return Map of key phrases to their importance scores (0.0-1.0)
     */
    Map<String, Double> extractKeyPhrases(String jobDescription);
    
    /**
     * Determines the seniority level required by the job description.
     *
     * @param jobDescription The job description text to analyze
     * @return The seniority level (e.g., "Entry", "Mid", "Senior", "Executive")
     */
    String determineSeniorityLevel(String jobDescription);
    
    /**
     * Extracts the primary industry or domain from the job description.
     *
     * @param jobDescription The job description text to analyze
     * @return The identified industry or domain
     */
    String extractIndustry(String jobDescription);
    
    /**
     * Analyzes the job description and returns a map of various attributes.
     *
     * @param jobDescription The job description text to analyze
     * @return Map of attributes (e.g., "required_experience", "education_level", etc.)
     */
    Map<String, String> analyzeJobAttributes(String jobDescription);
} 