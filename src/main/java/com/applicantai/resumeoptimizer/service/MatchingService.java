package com.applicantai.resumeoptimizer.service;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.MatchResult;
import com.applicantai.resumeoptimizer.model.OptimizedResume;
import com.applicantai.resumeoptimizer.model.Resume;

/**
 * Service interface for matching resumes with job descriptions and generating optimization suggestions.
 */
public interface MatchingService {
    
    /**
     * Matches a resume with a job description and calculates match scores.
     *
     * @param resume The resume to match
     * @param jobDescription The job description to match against
     * @return MatchResult containing match scores and details
     */
    MatchResult matchResumeWithJob(Resume resume, JobDescription jobDescription);
    
    /**
     * Matches a resume with a job description using the raw text content.
     *
     * @param resumeContent The raw text content of the resume
     * @param jobDescriptionContent The raw text content of the job description
     * @return MatchResult containing match scores and details
     */
    MatchResult matchResumeWithJobText(String resumeContent, String jobDescriptionContent);
    
    /**
     * Generates an optimized version of the resume based on the job description.
     *
     * @param resume The resume to optimize
     * @param jobDescription The job description to optimize for
     * @return OptimizedResume containing the optimized content and suggestions
     */
    OptimizedResume generateOptimizedResume(Resume resume, JobDescription jobDescription);
    
    /**
     * Generates optimization suggestions for a resume based on a match result.
     *
     * @param matchResult The match result containing match details
     * @return OptimizedResume containing suggestions for improvement
     */
    OptimizedResume generateOptimizationSuggestions(MatchResult matchResult);
} 