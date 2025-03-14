package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.JobDescription;
import com.applicantai.resumeoptimizer.model.MatchResult;
import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.OptimizedResume;
import com.applicantai.resumeoptimizer.model.Resume;
import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.JobDescriptionAnalysisService;
import com.applicantai.resumeoptimizer.service.MatchingService;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import com.applicantai.resumeoptimizer.service.WordEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
 * This service matches resumes with job descriptions and generates optimization suggestions.
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
    
    @Autowired
    private WordEmbeddingService wordEmbeddingService;
    
    @Value("${app.matching.similarity-threshold:0.7}")
    private double similarityThreshold;
    
    @Value("${app.matching.skill-weight:0.6}")
    private double skillWeight;
    
    @Value("${app.matching.experience-weight:0.3}")
    private double experienceWeight;
    
    @Value("${app.matching.education-weight:0.1}")
    private double educationWeight;
    
    /**
     * Matches a resume with a job description and calculates match scores.
     *
     * @param resume The resume to match
     * @param jobDescription The job description to match against
     * @return MatchResult containing match scores and details
     */
    @Override
    public MatchResult matchResumeWithJob(Resume resume, JobDescription jobDescription) {
        logger.info("Matching resume ID {} with job description ID {}", resume.getId(), jobDescription.getId());
        
        // Extract skills from resume and job description
        Set<Skill> resumeSkills = resume.getSkills();
        Set<Skill> jobSkills = jobDescription.getRequiredSkills();
        
        // Calculate match scores
        return calculateMatchScores(resumeSkills, jobSkills, resume, jobDescription);
    }
    
    /**
     * Matches a resume with a job description using the raw text content.
     *
     * @param resumeContent The raw text content of the resume
     * @param jobDescriptionContent The raw text content of the job description
     * @return MatchResult containing match scores and details
     */
    @Override
    public MatchResult matchResumeWithJobText(String resumeContent, String jobDescriptionContent) {
        logger.info("Matching resume text with job description text");
        
        // Analyze resume content
        NlpAnalysisResult resumeAnalysis = nlpAnalysisService.analyzeText(resumeContent);
        Set<Skill> resumeSkills = resumeAnalysis.getIdentifiedSkills();
        
        // Analyze job description content
        NlpAnalysisResult jobAnalysis = nlpAnalysisService.analyzeText(jobDescriptionContent);
        Set<Skill> jobSkills = jobAnalysis.getIdentifiedSkills();
        
        // Create temporary Resume and JobDescription objects
        Resume tempResume = new Resume();
        tempResume.setSkills(resumeSkills);
        tempResume.setRawContent(resumeContent);
        tempResume.setContent(resumeContent);
        
        JobDescription tempJobDescription = new JobDescription();
        tempJobDescription.setRequiredSkills(jobSkills);
        tempJobDescription.setContent(jobDescriptionContent);
        
        // Calculate match scores
        return calculateMatchScores(resumeSkills, jobSkills, tempResume, tempJobDescription);
    }
    
    /**
     * Generates an optimized version of the resume based on the job description.
     *
     * @param resume The resume to optimize
     * @param jobDescription The job description to optimize for
     * @return OptimizedResume containing the optimized content and suggestions
     */
    @Override
    public OptimizedResume generateOptimizedResume(Resume resume, JobDescription jobDescription) {
        logger.info("Generating optimized resume for resume ID {} and job description ID {}", 
                resume.getId(), jobDescription.getId());
        
        // First, match the resume with the job description
        MatchResult matchResult = matchResumeWithJob(resume, jobDescription);
        
        // Generate optimization suggestions based on the match result
        return generateOptimizationSuggestions(matchResult);
    }
    
    /**
     * Generates optimization suggestions for a resume based on a match result.
     *
     * @param matchResult The match result containing match details
     * @return OptimizedResume containing suggestions for improvement
     */
    @Override
    public OptimizedResume generateOptimizationSuggestions(MatchResult matchResult) {
        logger.info("Generating optimization suggestions for match result");
        
        OptimizedResume optimizedResume = new OptimizedResume();
        optimizedResume.setOriginalResume(matchResult.getResume());
        optimizedResume.setTargetJobDescription(matchResult.getJobDescription());
        optimizedResume.setMatchScore(matchResult.getOverallMatchScore());
        
        List<String> suggestions = new ArrayList<>();
        
        // Add missing skills suggestions
        if (!matchResult.getMissingSkills().isEmpty()) {
            suggestions.add("Add the following missing skills to your resume if you have them: " + 
                    matchResult.getMissingSkills().stream()
                            .map(Skill::getName)
                            .collect(Collectors.joining(", ")));
        }
        
        // Add skill emphasis suggestions
        if (!matchResult.getMatchedSkills().isEmpty()) {
            suggestions.add("Emphasize these skills more prominently in your resume: " + 
                    matchResult.getMatchedSkills().stream()
                            .filter(skill -> skill.getRelevanceScore() > 80)
                            .map(Skill::getName)
                            .collect(Collectors.joining(", ")));
        }
        
        // Add keyword suggestions based on job description
        JobDescription jobDescription = matchResult.getJobDescription();
        if (jobDescription != null && jobDescription.getContent() != null) {
            NlpAnalysisResult jobAnalysis = nlpAnalysisService.analyzeText(jobDescription.getContent());
            
            // Add key phrases from job description
            if (!jobAnalysis.getKeyPhrases().isEmpty()) {
                List<String> topKeyPhrases = jobAnalysis.getKeyPhrases().entrySet().stream()
                        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                        .limit(5)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());
                
                suggestions.add("Consider adding these key phrases from the job description: " + 
                        String.join(", ", topKeyPhrases));
            }
        }
        
        // Add section-specific suggestions
        addSectionSpecificSuggestions(matchResult, suggestions);
        
        // Add general suggestions
        suggestions.add("Quantify your achievements with specific metrics where possible");
        suggestions.add("Tailor your resume summary to highlight relevant experience for this position");
        suggestions.add("Ensure your resume is ATS-friendly with standard section headings");
        
        optimizedResume.setSuggestions(suggestions);
        
        // Generate optimized content (simplified version)
        // In a real implementation, this would be more sophisticated
        optimizedResume.setOptimizedContent("Optimized version of the resume would be generated here");
        
        return optimizedResume;
    }
    
    /**
     * Adds section-specific suggestions based on the match result.
     * 
     * @param matchResult The match result
     * @param suggestions The list of suggestions to add to
     */
    private void addSectionSpecificSuggestions(MatchResult matchResult, List<String> suggestions) {
        Resume resume = matchResult.getResume();
        JobDescription jobDescription = matchResult.getJobDescription();
        
        // Add experience-related suggestions
        if (matchResult.getExperienceMatchScore() < 70) {
            suggestions.add("Highlight experience that is most relevant to the job requirements");
            suggestions.add("Use action verbs that align with the job description language");
        }
        
        // Add education-related suggestions
        if (matchResult.getEducationMatchScore() < 70) {
            suggestions.add("Ensure your education section includes relevant coursework or certifications");
        }
        
        // Add format-related suggestions
        suggestions.add("Use bullet points to make your achievements more scannable");
        suggestions.add("Keep your resume to 1-2 pages for optimal readability");
    }
    
    /**
     * Calculates match scores between resume skills and job description skills.
     *
     * @param resumeSkills Skills from the resume
     * @param jobSkills Skills from the job description
     * @param resume The resume
     * @param jobDescription The job description
     * @return MatchResult containing match scores and details
     */
    private MatchResult calculateMatchScores(Set<Skill> resumeSkills, Set<Skill> jobSkills, 
                                           Resume resume, JobDescription jobDescription) {
        // Initialize match result
        MatchResult matchResult = new MatchResult();
        matchResult.setResume(resume);
        matchResult.setJobDescription(jobDescription);
        
        // Find matched skills using semantic similarity
        Set<Skill> matchedSkills = new HashSet<>();
        Map<String, Integer> matchedSkillScores = new HashMap<>();
        
        // For each job skill, find the best matching resume skill
        for (Skill jobSkill : jobSkills) {
            Skill bestMatch = null;
            double bestSimilarity = 0.0;
            
            for (Skill resumeSkill : resumeSkills) {
                // Calculate similarity using word embeddings
                double similarity = wordEmbeddingService.calculateSkillSimilarity(
                        resumeSkill.getName(), jobSkill.getName());
                
                if (similarity >= similarityThreshold && similarity > bestSimilarity) {
                    bestSimilarity = similarity;
                    bestMatch = resumeSkill;
                }
            }
            
            if (bestMatch != null) {
                matchedSkills.add(bestMatch);
                // Convert similarity to a score out of 100
                int score = (int) (bestSimilarity * 100);
                matchedSkillScores.put(bestMatch.getName(), score);
            }
        }
        
        // Find missing skills
        Set<Skill> missingSkills = new HashSet<>(jobSkills);
        missingSkills.removeIf(jobSkill -> 
                matchedSkills.stream().anyMatch(resumeSkill -> 
                        wordEmbeddingService.calculateSkillSimilarity(
                                resumeSkill.getName(), jobSkill.getName()) >= similarityThreshold));
        
        // Calculate overall match score
        int skillMatchScore = calculateSkillMatchScore(matchedSkills, jobSkills);
        int experienceMatchScore = calculateExperienceMatchScore(resume, jobDescription);
        int educationMatchScore = calculateEducationMatchScore(resume, jobDescription);
        
        // Overall score is a weighted average
        int overallMatchScore = (int) (skillMatchScore * skillWeight + 
                                      experienceMatchScore * experienceWeight + 
                                      educationMatchScore * educationWeight);
        
        // Set match result details
        matchResult.setOverallMatchScore(overallMatchScore);
        matchResult.setSkillMatchScore(skillMatchScore);
        matchResult.setExperienceMatchScore(experienceMatchScore);
        matchResult.setEducationMatchScore(educationMatchScore);
        matchResult.setMatchedSkills(matchedSkills);
        matchResult.setMissingSkills(missingSkills);
        matchResult.setMatchedSkillScores(matchedSkillScores);
        
        logger.info("Match result: overall score {}, skill score {}, experience score {}, education score {}",
                overallMatchScore, skillMatchScore, experienceMatchScore, educationMatchScore);
        
        return matchResult;
    }
    
    /**
     * Calculates the overall skill match score.
     *
     * @param matchedSkills Skills that matched
     * @param jobSkills Skills from the job description
     * @return Match score (0-100)
     */
    private int calculateSkillMatchScore(Set<Skill> matchedSkills, Set<Skill> jobSkills) {
        if (jobSkills.isEmpty()) {
            return 100; // No skills required, perfect match
        }
        
        // Calculate percentage of matched skills
        double matchPercentage = (double) matchedSkills.size() / jobSkills.size() * 100;
        
        // Apply weighting based on importance of skills
        // This is a simplified version; a real implementation would be more sophisticated
        
        return (int) matchPercentage;
    }
    
    /**
     * Calculates the experience match score.
     *
     * @param resume The resume
     * @param jobDescription The job description
     * @return Match score (0-100)
     */
    private int calculateExperienceMatchScore(Resume resume, JobDescription jobDescription) {
        // This is a placeholder implementation
        // A real implementation would compare experience levels, years of experience, etc.
        
        // For now, return a default score
        return 70;
    }
    
    /**
     * Calculates the education match score.
     *
     * @param resume The resume
     * @param jobDescription The job description
     * @return Match score (0-100)
     */
    private int calculateEducationMatchScore(Resume resume, JobDescription jobDescription) {
        // This is a placeholder implementation
        // A real implementation would compare education levels, degrees, etc.
        
        // For now, return a default score
        return 80;
    }
} 