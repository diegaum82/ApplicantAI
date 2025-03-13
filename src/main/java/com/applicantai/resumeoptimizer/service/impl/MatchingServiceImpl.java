package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.Resume;
import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.JobDescriptionAnalysisService;
import com.applicantai.resumeoptimizer.service.MatchingService;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the MatchingService interface.
 * Compares resumes with job descriptions and generates optimization suggestions.
 */
@Service
public class MatchingServiceImpl implements MatchingService {
    
    private static final Logger logger = LoggerFactory.getLogger(MatchingServiceImpl.class);
    
    @Autowired
    private ResumeProcessingService resumeProcessingService;
    
    @Autowired
    private JobDescriptionAnalysisService jobDescriptionAnalysisService;
    
    @Autowired
    private NlpAnalysisService nlpAnalysisService;
    
    @Override
    public int calculateMatchScore(Resume resume, JobDescription jobDescription) {
        logger.debug("Calculating match score between resume {} and job description {}", 
                resume.getId(), jobDescription.getId());
        
        // Extract skills from resume
        Set<Skill> resumeSkills = resume.getSkills();
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            logger.warn("Resume has no skills. Using content analysis instead.");
            // If no skills are associated with the resume, try to extract them from content
            if (resume.getContent() != null) {
                ResumeContent resumeContent = new ResumeContent();
                resumeContent.setRawText(resume.getContent());
                resumeSkills = resumeProcessingService.identifySkills(resumeContent);
            } else {
                resumeSkills = new HashSet<>();
            }
        }
        
        // Extract required skills from job description
        Set<Skill> jobSkills = jobDescription.getRequiredSkills();
        if (jobSkills == null || jobSkills.isEmpty()) {
            logger.warn("Job description has no skills. Using content analysis instead.");
            // If no skills are associated with the job description, try to extract them from content
            if (jobDescription.getContent() != null) {
                jobSkills = jobDescriptionAnalysisService.extractRequiredSkills(jobDescription.getContent());
            } else {
                jobSkills = new HashSet<>();
            }
        }
        
        // Calculate match score
        return calculateSkillMatchScore(resumeSkills, jobSkills);
    }
    
    @Override
    public int calculateMatchScore(ResumeContent resumeContent, String jobDescription) {
        logger.debug("Calculating match score between resume content and job description text");
        
        // Extract skills from resume content
        Set<Skill> resumeSkills = resumeProcessingService.identifySkills(resumeContent);
        
        // Extract required skills from job description
        Set<Skill> jobSkills = jobDescriptionAnalysisService.extractRequiredSkills(jobDescription);
        
        // Calculate match score
        return calculateSkillMatchScore(resumeSkills, jobSkills);
    }
    
    /**
     * Calculates a match score based on skills.
     *
     * @param resumeSkills Skills from the resume
     * @param jobSkills Skills from the job description
     * @return A score between 0 and 100
     */
    private int calculateSkillMatchScore(Set<Skill> resumeSkills, Set<Skill> jobSkills) {
        if (jobSkills.isEmpty()) {
            logger.warn("Job skills set is empty, cannot calculate meaningful match score");
            return 0;
        }
        
        // Count matching skills
        int matchingSkillsCount = 0;
        for (Skill jobSkill : jobSkills) {
            for (Skill resumeSkill : resumeSkills) {
                if (resumeSkill.getName().equalsIgnoreCase(jobSkill.getName())) {
                    matchingSkillsCount++;
                    break;
                }
            }
        }
        
        // Calculate percentage match
        double matchPercentage = (double) matchingSkillsCount / jobSkills.size() * 100;
        
        // Apply a bonus for having more skills than required
        if (resumeSkills.size() > jobSkills.size()) {
            matchPercentage += 5; // Small bonus for having additional skills
        }
        
        // Cap at 100
        return (int) Math.min(100, matchPercentage);
    }
    
    @Override
    public Map<String, Integer> identifyMatchingSkills(Resume resume, JobDescription jobDescription) {
        logger.debug("Identifying matching skills between resume {} and job description {}", 
                resume.getId(), jobDescription.getId());
        
        Map<String, Integer> matchingSkills = new HashMap<>();
        
        // Extract skills from resume
        Set<Skill> resumeSkills = resume.getSkills();
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            // If no skills are associated with the resume, try to extract them from content
            if (resume.getContent() != null) {
                ResumeContent resumeContent = new ResumeContent();
                resumeContent.setRawText(resume.getContent());
                resumeSkills = resumeProcessingService.identifySkills(resumeContent);
            } else {
                resumeSkills = new HashSet<>();
            }
        }
        
        // Extract required skills from job description
        Set<Skill> jobSkills = jobDescription.getRequiredSkills();
        if (jobSkills == null || jobSkills.isEmpty()) {
            // If no skills are associated with the job description, try to extract them from content
            if (jobDescription.getContent() != null) {
                jobSkills = jobDescriptionAnalysisService.extractRequiredSkills(jobDescription.getContent());
            } else {
                jobSkills = new HashSet<>();
            }
        }
        
        // Identify matching skills
        for (Skill jobSkill : jobSkills) {
            for (Skill resumeSkill : resumeSkills) {
                if (resumeSkill.getName().equalsIgnoreCase(jobSkill.getName())) {
                    // Use relevance score if available, otherwise default to 80
                    int relevanceScore = resumeSkill.getRelevanceScore() != null ? 
                            resumeSkill.getRelevanceScore() : 80;
                    matchingSkills.put(resumeSkill.getName(), relevanceScore);
                    break;
                }
            }
        }
        
        return matchingSkills;
    }
    
    @Override
    public Map<String, Integer> identifyMissingSkills(Resume resume, JobDescription jobDescription) {
        logger.debug("Identifying missing skills between resume {} and job description {}", 
                resume.getId(), jobDescription.getId());
        
        Map<String, Integer> missingSkills = new HashMap<>();
        
        // Extract skills from resume
        Set<Skill> resumeSkills = resume.getSkills();
        if (resumeSkills == null || resumeSkills.isEmpty()) {
            // If no skills are associated with the resume, try to extract them from content
            if (resume.getContent() != null) {
                ResumeContent resumeContent = new ResumeContent();
                resumeContent.setRawText(resume.getContent());
                resumeSkills = resumeProcessingService.identifySkills(resumeContent);
            } else {
                resumeSkills = new HashSet<>();
            }
        }
        
        // Extract required skills from job description
        Set<Skill> jobSkills = jobDescription.getRequiredSkills();
        if (jobSkills == null || jobSkills.isEmpty()) {
            // If no skills are associated with the job description, try to extract them from content
            if (jobDescription.getContent() != null) {
                jobSkills = jobDescriptionAnalysisService.extractRequiredSkills(jobDescription.getContent());
            } else {
                jobSkills = new HashSet<>();
            }
        }
        
        // Convert resume skills to lowercase names for easier comparison
        Set<String> resumeSkillNames = resumeSkills.stream()
                .map(skill -> skill.getName().toLowerCase())
                .collect(Collectors.toSet());
        
        // Identify missing skills
        for (Skill jobSkill : jobSkills) {
            if (!resumeSkillNames.contains(jobSkill.getName().toLowerCase())) {
                // Default importance score is 80
                missingSkills.put(jobSkill.getName(), 80);
            }
        }
        
        return missingSkills;
    }
    
    @Override
    public List<String> generateOptimizationSuggestions(Resume resume, JobDescription jobDescription) {
        logger.debug("Generating optimization suggestions for resume {} and job description {}", 
                resume.getId(), jobDescription.getId());
        
        List<String> suggestions = new ArrayList<>();
        
        // Identify missing skills
        Map<String, Integer> missingSkills = identifyMissingSkills(resume, jobDescription);
        
        // Generate suggestions for missing skills
        if (!missingSkills.isEmpty()) {
            suggestions.add("Add the following skills to your resume: " + 
                    String.join(", ", missingSkills.keySet()));
        }
        
        // Analyze job description for key phrases
        Map<String, Double> keyPhrases = jobDescriptionAnalysisService.extractKeyPhrases(jobDescription.getContent());
        
        // Check if key phrases are present in resume
        if (resume.getContent() != null) {
            String resumeContent = resume.getContent().toLowerCase();
            
            for (Map.Entry<String, Double> entry : keyPhrases.entrySet()) {
                String phrase = entry.getKey();
                Double importance = entry.getValue();
                
                // Only suggest important phrases (importance > 0.7)
                if (importance > 0.7 && !resumeContent.contains(phrase.toLowerCase())) {
                    suggestions.add("Consider adding the phrase '" + phrase + "' to your resume");
                }
            }
        }
        
        // Add general suggestions
        suggestions.add("Tailor your resume summary to highlight experience relevant to this job");
        suggestions.add("Quantify your achievements with specific metrics where possible");
        
        return suggestions;
    }
    
    @Override
    public List<String> generateOptimizationSuggestions(ResumeContent resumeContent, String jobDescription) {
        logger.debug("Generating optimization suggestions for resume content and job description text");
        
        List<String> suggestions = new ArrayList<>();
        
        // Analyze resume content
        NlpAnalysisResult resumeAnalysis = nlpAnalysisService.analyzeText(resumeContent.getRawText());
        
        // Analyze job description
        NlpAnalysisResult jobAnalysis = nlpAnalysisService.analyzeText(jobDescription);
        
        // Extract skills from both
        Set<Skill> resumeSkills = resumeAnalysis.getIdentifiedSkills();
        Set<Skill> jobSkills = jobAnalysis.getIdentifiedSkills();
        
        // Convert to lowercase names for easier comparison
        Set<String> resumeSkillNames = resumeSkills.stream()
                .map(skill -> skill.getName().toLowerCase())
                .collect(Collectors.toSet());
        
        // Identify missing skills
        List<String> missingSkillNames = new ArrayList<>();
        for (Skill jobSkill : jobSkills) {
            if (!resumeSkillNames.contains(jobSkill.getName().toLowerCase())) {
                missingSkillNames.add(jobSkill.getName());
            }
        }
        
        // Generate suggestions for missing skills
        if (!missingSkillNames.isEmpty()) {
            suggestions.add("Add the following skills to your resume: " + 
                    String.join(", ", missingSkillNames));
        }
        
        // Check if key phrases from job description are present in resume
        Map<String, Double> jobKeyPhrases = jobAnalysis.getKeyPhrases();
        String resumeText = resumeContent.getRawText().toLowerCase();
        
        for (Map.Entry<String, Double> entry : jobKeyPhrases.entrySet()) {
            String phrase = entry.getKey();
            Double importance = entry.getValue();
            
            // Only suggest important phrases (importance > 0.7)
            if (importance > 0.7 && !resumeText.contains(phrase.toLowerCase())) {
                suggestions.add("Consider adding the phrase '" + phrase + "' to your resume");
            }
        }
        
        // Add general suggestions
        suggestions.add("Tailor your resume summary to highlight experience relevant to this job");
        suggestions.add("Quantify your achievements with specific metrics where possible");
        suggestions.add("Ensure your resume uses industry-standard terminology for your field");
        
        return suggestions;
    }
} 