package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.JobDescriptionAnalysisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implementation of the JobDescriptionAnalysisService interface.
 * Uses NlpAnalysisService for text analysis.
 */
@Service
public class JobDescriptionAnalysisServiceImpl implements JobDescriptionAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(JobDescriptionAnalysisServiceImpl.class);
    
    // Patterns for extracting specific job description information
    private static final Pattern REQUIRED_SKILLS_PATTERN = Pattern.compile(
            "(?i)\\b(required|must have|essential|necessary|needed|mandatory)\\s+(?:skills|qualifications|requirements)\\b",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern PREFERRED_SKILLS_PATTERN = Pattern.compile(
            "(?i)\\b(preferred|desired|nice to have|plus|bonus|advantageous)\\s+(?:skills|qualifications|requirements)\\b",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern EXPERIENCE_PATTERN = Pattern.compile(
            "(?i)\\b(\\d+)\\+?\\s+(?:years?|yrs?)\\s+(?:of\\s+)?(?:experience|work)\\b",
            Pattern.CASE_INSENSITIVE);
    
    @Autowired
    private NlpAnalysisService nlpAnalysisService;
    
    @Override
    public Set<Skill> extractRequiredSkills(String jobDescription) {
        logger.debug("Extracting required skills from job description text");
        
        // Use NLP analysis to extract skills
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(jobDescription);
        
        // Get all identified skills
        Set<Skill> allSkills = analysisResult.getIdentifiedSkills();
        
        // For now, we'll return all identified skills
        // In a more sophisticated implementation, we would filter based on required vs. preferred
        return allSkills;
    }
    
    @Override
    public Set<Skill> extractRequiredSkills(JobDescription jobDescription) {
        if (jobDescription == null || jobDescription.getContent() == null) {
            logger.warn("Job description or content is null");
            return new HashSet<>();
        }
        
        return extractRequiredSkills(jobDescription.getContent());
    }
    
    @Override
    public Map<String, Double> extractKeyPhrases(String jobDescription) {
        logger.debug("Extracting key phrases from job description text");
        
        // Use NLP analysis to extract key phrases
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(jobDescription);
        
        // Get all key phrases with their importance scores
        return analysisResult.getKeyPhrases();
    }
    
    @Override
    public String determineSeniorityLevel(String jobDescription) {
        logger.debug("Determining seniority level from job description text");
        
        // Use NLP analysis to extract attributes
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(jobDescription);
        
        // Check if seniority level was identified
        Map<String, String> attributes = analysisResult.getAttributes();
        if (attributes.containsKey("seniority_level")) {
            return attributes.get("seniority_level");
        }
        
        // If not found in attributes, try to determine from experience requirements
        Matcher matcher = EXPERIENCE_PATTERN.matcher(jobDescription);
        if (matcher.find()) {
            String yearsStr = matcher.group(1);
            try {
                int years = Integer.parseInt(yearsStr);
                
                // Simple mapping of years to seniority level
                if (years <= 2) {
                    return "Entry";
                } else if (years <= 5) {
                    return "Mid";
                } else if (years <= 8) {
                    return "Senior";
                } else {
                    return "Principal/Lead";
                }
            } catch (NumberFormatException e) {
                logger.warn("Failed to parse years of experience: {}", yearsStr);
            }
        }
        
        // Default to "Not Specified" if we couldn't determine
        return "Not Specified";
    }
    
    @Override
    public String extractIndustry(String jobDescription) {
        logger.debug("Extracting industry from job description text");
        
        // Use NLP analysis to extract attributes
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(jobDescription);
        
        // Check if industry was identified
        Map<String, String> attributes = analysisResult.getAttributes();
        if (attributes.containsKey("industry")) {
            return attributes.get("industry");
        }
        
        // Default to "Not Specified" if we couldn't determine
        return "Not Specified";
    }
    
    @Override
    public Map<String, String> analyzeJobAttributes(String jobDescription) {
        logger.debug("Analyzing job attributes from job description text");
        
        // Use NLP analysis to extract attributes
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(jobDescription);
        
        // Start with the attributes from NLP analysis
        Map<String, String> jobAttributes = new HashMap<>(analysisResult.getAttributes());
        
        // Add seniority level if not already present
        if (!jobAttributes.containsKey("seniority_level")) {
            jobAttributes.put("seniority_level", determineSeniorityLevel(jobDescription));
        }
        
        // Add industry if not already present
        if (!jobAttributes.containsKey("industry")) {
            jobAttributes.put("industry", extractIndustry(jobDescription));
        }
        
        // Add required skills count
        jobAttributes.put("required_skills_count", String.valueOf(analysisResult.getIdentifiedSkills().size()));
        
        // Add key phrases count
        jobAttributes.put("key_phrases_count", String.valueOf(analysisResult.getKeyPhrases().size()));
        
        return jobAttributes;
    }
} 