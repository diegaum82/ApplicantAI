package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Enhanced implementation of the ResumeProcessingService that uses NLP analysis for skill extraction.
 * This service delegates PDF processing to the PdfProcessingService but enhances it with NLP capabilities.
 */
@Service
public class EnhancedResumeProcessingService implements ResumeProcessingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EnhancedResumeProcessingService.class);
    
    @Autowired
    private PdfProcessingService pdfProcessingService;
    
    @Autowired
    private NlpAnalysisService nlpAnalysisService;
    
    @Value("${app.nlp.min-confidence-score:0.7}")
    private double minConfidenceScore;
    
    @Override
    @Cacheable(value = "resumeCache", key = "#file.originalFilename + '-' + #file.size")
    public ResumeContent processResume(MultipartFile file) throws IOException {
        logger.info("Enhanced processing of resume file: {}", file.getOriginalFilename());
        
        // Delegate to PDF processing service for initial extraction
        ResumeContent resumeContent = pdfProcessingService.processResume(file);
        
        // Enhance with NLP analysis
        enhanceWithNlpAnalysis(resumeContent);
        
        return resumeContent;
    }
    
    /**
     * Enhances the resume content with NLP analysis.
     *
     * @param resumeContent The resume content to enhance
     */
    private void enhanceWithNlpAnalysis(ResumeContent resumeContent) {
        if (resumeContent == null || resumeContent.getRawText() == null) {
            logger.warn("Cannot enhance null resume content");
            return;
        }
        
        logger.debug("Enhancing resume content with NLP analysis");
        
        // Perform NLP analysis on the raw text
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(resumeContent.getRawText());
        
        // Add NLP analysis metadata
        resumeContent.getMetadata().put("nlpEnhanced", "true");
        resumeContent.getMetadata().put("identifiedSkillsCount", String.valueOf(analysisResult.getIdentifiedSkills().size()));
        resumeContent.getMetadata().put("keyPhrasesCount", String.valueOf(analysisResult.getKeyPhrases().size()));
        resumeContent.getMetadata().put("namedEntitiesCount", String.valueOf(analysisResult.getNamedEntities().size()));
        
        // Store named entities in metadata
        if (!analysisResult.getNamedEntities().isEmpty()) {
            resumeContent.getMetadata().put("namedEntities", String.join("|", analysisResult.getNamedEntities()));
        }
    }
    
    @Override
    public Set<Skill> identifySkills(ResumeContent content) {
        if (content == null || content.getRawText() == null) {
            logger.warn("Cannot identify skills in null resume content");
            return new HashSet<>();
        }
        
        logger.info("Identifying skills in resume content");
        
        // Perform NLP analysis on the raw text
        NlpAnalysisResult analysisResult = nlpAnalysisService.analyzeText(content.getRawText());
        
        // Get identified skills
        return analysisResult.getIdentifiedSkills();
    }
    
    @Override
    public ResumeContent detectSections(String content) {
        // Delegate to PDF processing service for section detection
        return pdfProcessingService.detectSections(content);
    }
    
    @Override
    public int evaluateExtractionQuality(ResumeContent content) {
        // Start with the base quality score from PDF processing
        int baseQualityScore = pdfProcessingService.evaluateExtractionQuality(content);
        
        // If NLP enhancement was performed, adjust the score
        if (content.getMetadata().containsKey("nlpEnhanced") && "true".equals(content.getMetadata().get("nlpEnhanced"))) {
            // Bonus for identified skills
            int skillsCount = Integer.parseInt(content.getMetadata().getOrDefault("identifiedSkillsCount", "0"));
            int skillsBonus = Math.min(10, skillsCount * 2); // Up to 10 points bonus for skills
            
            // Bonus for named entities
            int entitiesCount = Integer.parseInt(content.getMetadata().getOrDefault("namedEntitiesCount", "0"));
            int entitiesBonus = Math.min(5, entitiesCount); // Up to 5 points bonus for entities
            
            // Apply bonuses, but cap at 100
            return Math.min(100, baseQualityScore + skillsBonus + entitiesBonus);
        }
        
        return baseQualityScore;
    }
} 