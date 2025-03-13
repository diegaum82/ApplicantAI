package com.applicantai.resumeoptimizer.service;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.Resume;
import com.applicantai.resumeoptimizer.model.ResumeContent;

import java.util.List;
import java.util.Map;

/**
 * Service interface for matching resumes with job descriptions and generating optimization suggestions.
 */
public interface MatchingService {
    
    /**
     * Compares a resume with a job description and returns a match score.
     *
     * @param resume The resume to compare
     * @param jobDescription The job description to compare against
     * @return A score between 0 and 100 indicating the match quality
     */
    int calculateMatchScore(Resume resume, JobDescription jobDescription);
    
    /**
     * Compares resume content with a job description and returns a match score.
     *
     * @param resumeContent The resume content to compare
     * @param jobDescription The job description to compare against
     * @return A score between 0 and 100 indicating the match quality
     */
    int calculateMatchScore(ResumeContent resumeContent, String jobDescription);
    
    /**
     * Identifies matching skills between a resume and job description.
     *
     * @param resume The resume to analyze
     * @param jobDescription The job description to analyze
     * @return A map of skill names to relevance scores (0-100)
     */
    Map<String, Integer> identifyMatchingSkills(Resume resume, JobDescription jobDescription);
    
    /**
     * Identifies skills that are missing from the resume but required by the job description.
     *
     * @param resume The resume to analyze
     * @param jobDescription The job description to analyze
     * @return A map of skill names to importance scores (0-100)
     */
    Map<String, Integer> identifyMissingSkills(Resume resume, JobDescription jobDescription);
    
    /**
     * Generates optimization suggestions for a resume based on a job description.
     *
     * @param resume The resume to optimize
     * @param jobDescription The target job description
     * @return A list of optimization suggestions
     */
    List<String> generateOptimizationSuggestions(Resume resume, JobDescription jobDescription);
    
    /**
     * Generates optimization suggestions for resume content based on a job description.
     *
     * @param resumeContent The resume content to optimize
     * @param jobDescription The target job description
     * @return A list of optimization suggestions
     */
    List<String> generateOptimizationSuggestions(ResumeContent resumeContent, String jobDescription);
} 