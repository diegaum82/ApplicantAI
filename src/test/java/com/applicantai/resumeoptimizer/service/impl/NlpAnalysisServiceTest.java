package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ResourceLoader;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NlpAnalysisServiceTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private NlpAnalysisService nlpAnalysisService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void analyzeText_WithProgrammingLanguages_ShouldIdentifySkills() {
        // Given
        String text = "I am proficient in Java, Python, and JavaScript. I have 5 years of experience with Spring Boot.";
        
        // When
        NlpAnalysisResult result = nlpAnalysisService.analyzeText(text);
        
        // Then
        assertNotNull(result);
        Set<Skill> skills = result.getIdentifiedSkills();
        assertNotNull(skills);
        assertFalse(skills.isEmpty());
        
        // Verify that Java, Python, and JavaScript are identified
        boolean hasJava = skills.stream().anyMatch(skill -> "Java".equalsIgnoreCase(skill.getName()));
        boolean hasPython = skills.stream().anyMatch(skill -> "Python".equalsIgnoreCase(skill.getName()));
        boolean hasJavaScript = skills.stream().anyMatch(skill -> "JavaScript".equalsIgnoreCase(skill.getName()));
        
        assertTrue(hasJava, "Java should be identified as a skill");
        assertTrue(hasPython, "Python should be identified as a skill");
        assertTrue(hasJavaScript, "JavaScript should be identified as a skill");
    }

    @Test
    void analyzeText_WithFrameworks_ShouldIdentifySkills() {
        // Given
        String text = "I have experience with Spring Boot, React, and Angular frameworks.";
        
        // When
        NlpAnalysisResult result = nlpAnalysisService.analyzeText(text);
        
        // Then
        assertNotNull(result);
        Set<Skill> skills = result.getIdentifiedSkills();
        assertNotNull(skills);
        assertFalse(skills.isEmpty());
        
        // Verify that Spring Boot, React, and Angular are identified
        boolean hasSpringBoot = skills.stream().anyMatch(skill -> "Spring Boot".equalsIgnoreCase(skill.getName()));
        boolean hasReact = skills.stream().anyMatch(skill -> "React".equalsIgnoreCase(skill.getName()));
        boolean hasAngular = skills.stream().anyMatch(skill -> "Angular".equalsIgnoreCase(skill.getName()));
        
        assertTrue(hasSpringBoot, "Spring Boot should be identified as a skill");
        assertTrue(hasReact, "React should be identified as a skill");
        assertTrue(hasAngular, "Angular should be identified as a skill");
    }

    @Test
    void analyzeText_WithKeyPhrases_ShouldExtractKeyPhrases() {
        // Given
        String text = "I am a software engineer with 5 years of experience in web development. " +
                "I specialize in building scalable applications using microservices architecture.";
        
        // When
        NlpAnalysisResult result = nlpAnalysisService.analyzeText(text);
        
        // Then
        assertNotNull(result);
        Map<String, Double> keyPhrases = result.getKeyPhrases();
        assertNotNull(keyPhrases);
        assertFalse(keyPhrases.isEmpty());
        
        // Key phrases like "software engineer", "web development", "microservices architecture" should be extracted
        // Note: The exact phrases and scores may vary depending on the implementation
        assertTrue(keyPhrases.size() > 0, "Should extract at least one key phrase");
    }
} 