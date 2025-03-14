package com.applicantai.resumeoptimizer.controller;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.MatchResult;
import com.applicantai.resumeoptimizer.model.OptimizedResume;
import com.applicantai.resumeoptimizer.model.Resume;
import com.applicantai.resumeoptimizer.service.JobDescriptionAnalysisService;
import com.applicantai.resumeoptimizer.service.MatchingService;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for matching resumes with job descriptions and generating optimization suggestions.
 */
@RestController
@RequestMapping("/api/v1/match")
public class MatchingController {

    private static final Logger logger = LoggerFactory.getLogger(MatchingController.class);

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private ResumeProcessingService resumeProcessingService;

    @Autowired
    private JobDescriptionAnalysisService jobDescriptionAnalysisService;

    /**
     * Matches a resume with a job description using their raw text content.
     *
     * @param request The request containing resume and job description text
     * @return Match result with scores and suggestions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> matchResumeWithJob(@RequestBody MatchRequest request) {
        logger.info("Received match request");

        try {
            // Match resume with job description
            MatchResult matchResult = matchingService.matchResumeWithJobText(
                    request.getResumeContent(), 
                    request.getJobDescription());

            // Generate optimization suggestions
            OptimizedResume optimizedResume = matchingService.generateOptimizationSuggestions(matchResult);

            // Create response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            
            // Add overall match score
            response.put("matchScore", matchResult.getOverallMatchScore());
            
            // Add detailed scores
            response.put("skillMatchScore", matchResult.getSkillMatchScore());
            response.put("experienceMatchScore", matchResult.getExperienceMatchScore());
            response.put("educationMatchScore", matchResult.getEducationMatchScore());
            
            // Convert matched skills to a map of skill names to scores
            Map<String, Integer> matchedSkills = new HashMap<>();
            matchResult.getMatchedSkills().forEach(skill -> 
                    matchedSkills.put(skill.getName(), 
                            matchResult.getMatchedSkillScores().getOrDefault(skill.getName(), 0)));
            
            response.put("matchingSkills", matchedSkills);
            
            // Convert missing skills to a map of skill names to importance scores
            Map<String, Integer> missingSkills = new HashMap<>();
            matchResult.getMissingSkills().forEach(skill -> 
                    missingSkills.put(skill.getName(), skill.getRelevanceScore() != null ? skill.getRelevanceScore() : 0));
            
            response.put("missingSkills", missingSkills);
            
            // Add optimization suggestions
            response.put("suggestions", optimizedResume.getSuggestions());
            
            // Add additional metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("resumeLength", request.getResumeContent().length());
            metadata.put("jobDescriptionLength", request.getJobDescription().length());
            metadata.put("matchTimestamp", System.currentTimeMillis());
            response.put("metadata", metadata);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error matching resume with job description", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Failed to match resume with job description");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Request object for matching a resume with a job description.
     */
    public static class MatchRequest {
        private String resumeContent;
        private String jobDescription;

        public String getResumeContent() {
            return resumeContent;
        }

        public void setResumeContent(String resumeContent) {
            this.resumeContent = resumeContent;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }
    }
} 