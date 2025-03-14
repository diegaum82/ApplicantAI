package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.service.WordEmbeddingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Simple implementation of WordEmbeddingService that uses basic string similarity
 * techniques as a fallback when actual word embeddings are not available.
 * 
 * This implementation will be replaced with a more sophisticated one using
 * actual word embeddings in the future.
 */
@Service
public class SimpleWordEmbeddingServiceImpl implements WordEmbeddingService {
    
    private static final Logger logger = LoggerFactory.getLogger(SimpleWordEmbeddingServiceImpl.class);
    
    @Value("${app.nlp.enable-advanced-analysis:false}")
    private boolean enableAdvancedAnalysis;
    
    // Common technology synonyms and related terms
    private final Map<String, Set<String>> synonymMap = new HashMap<>();
    
    @PostConstruct
    public void init() {
        logger.info("Initializing simple word embedding service");
        
        // Initialize synonym map with common technology terms
        // This is a simplified approach until we implement actual word embeddings
        
        // Java ecosystem
        addSynonyms("java", "jvm", "j2ee", "java ee", "java se", "core java");
        addSynonyms("spring", "spring framework", "spring boot", "spring mvc", "spring cloud");
        addSynonyms("hibernate", "jpa", "orm", "object-relational mapping");
        
        // JavaScript ecosystem
        addSynonyms("javascript", "js", "ecmascript", "typescript", "ts");
        addSynonyms("react", "reactjs", "react.js", "react native");
        addSynonyms("angular", "angularjs", "angular.js", "angular 2+");
        addSynonyms("node", "nodejs", "node.js");
        
        // Cloud and DevOps
        addSynonyms("aws", "amazon web services", "ec2", "s3", "lambda");
        addSynonyms("azure", "microsoft azure", "azure cloud");
        addSynonyms("gcp", "google cloud", "google cloud platform");
        addSynonyms("kubernetes", "k8s", "container orchestration");
        addSynonyms("docker", "containerization", "containers");
        
        // Databases
        addSynonyms("sql", "relational database", "rdbms");
        addSynonyms("nosql", "non-relational database", "document database");
        addSynonyms("mongodb", "mongo", "document database");
        addSynonyms("postgresql", "postgres");
        
        // AI/ML
        addSynonyms("machine learning", "ml", "artificial intelligence", "ai");
        addSynonyms("deep learning", "neural networks", "neural nets");
        addSynonyms("nlp", "natural language processing");
        
        // Soft skills
        addSynonyms("leadership", "team lead", "team leadership", "leading");
        addSynonyms("communication", "interpersonal skills", "verbal communication", "written communication");
        addSynonyms("problem solving", "problem-solving", "analytical thinking", "critical thinking");
        
        logger.info("Initialized synonym map with {} entries", synonymMap.size());
    }
    
    private void addSynonyms(String mainTerm, String... synonyms) {
        Set<String> synonymSet = new HashSet<>(Arrays.asList(synonyms));
        synonymMap.put(mainTerm.toLowerCase(), synonymSet);
        
        // Also add reverse mappings for better lookup
        for (String synonym : synonyms) {
            Set<String> set = synonymMap.getOrDefault(synonym.toLowerCase(), new HashSet<>());
            set.add(mainTerm.toLowerCase());
            synonymMap.put(synonym.toLowerCase(), set);
        }
    }
    
    @Override
    public double calculateSimilarity(String word1, String word2) {
        if (word1 == null || word2 == null) {
            return 0.0;
        }
        
        // Convert to lowercase for case-insensitive comparison
        String w1 = word1.toLowerCase();
        String w2 = word2.toLowerCase();
        
        // Exact match
        if (w1.equals(w2)) {
            return 1.0;
        }
        
        // Check if one contains the other
        if (w1.contains(w2) || w2.contains(w1)) {
            return 0.8;
        }
        
        // Check synonyms
        if (areSynonyms(w1, w2)) {
            return 0.9;
        }
        
        // Calculate Levenshtein distance-based similarity
        return calculateLevenshteinSimilarity(w1, w2);
    }
    
    @Override
    public Map<String, Double> findSimilarWords(String word, List<String> candidates, double threshold) {
        if (word == null || candidates == null || candidates.isEmpty()) {
            return new HashMap<>();
        }
        
        Map<String, Double> result = new HashMap<>();
        String w = word.toLowerCase();
        
        for (String candidate : candidates) {
            double similarity = calculateSimilarity(w, candidate);
            if (similarity >= threshold) {
                result.put(candidate, similarity);
            }
        }
        
        return result;
    }
    
    @Override
    public double calculateSkillSimilarity(String skill, String requirement) {
        // For skills, we want to be a bit more lenient with matching
        double similarity = calculateSimilarity(skill, requirement);
        
        // Boost similarity for skills that are often abbreviated
        if (isAbbreviation(skill, requirement) || isAbbreviation(requirement, skill)) {
            similarity = Math.max(similarity, 0.85);
        }
        
        return similarity;
    }
    
    @Override
    public boolean isReady() {
        // This simple implementation is always ready
        return true;
    }
    
    /**
     * Checks if two words are synonyms according to our synonym map.
     */
    private boolean areSynonyms(String word1, String word2) {
        // Check if word2 is in the synonym set of word1
        Set<String> synonyms = synonymMap.getOrDefault(word1, new HashSet<>());
        if (synonyms.contains(word2)) {
            return true;
        }
        
        // Check if word1 is in the synonym set of word2
        synonyms = synonymMap.getOrDefault(word2, new HashSet<>());
        return synonyms.contains(word1);
    }
    
    /**
     * Calculates similarity based on Levenshtein distance.
     */
    private double calculateLevenshteinSimilarity(String s1, String s2) {
        int distance = levenshteinDistance(s1, s2);
        int maxLength = Math.max(s1.length(), s2.length());
        
        if (maxLength == 0) {
            return 1.0; // Both strings are empty
        }
        
        return 1.0 - ((double) distance / maxLength);
    }
    
    /**
     * Calculates the Levenshtein distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];
        
        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                    Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                    dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[s1.length()][s2.length()];
    }
    
    /**
     * Checks if one string might be an abbreviation of the other.
     */
    private boolean isAbbreviation(String abbr, String full) {
        if (abbr.length() >= full.length()) {
            return false;
        }
        
        // Check if abbr consists of capital letters from full
        if (abbr.toUpperCase().equals(abbr)) {
            // Split full by spaces and check if first letters match abbr
            String[] words = full.split("\\s+");
            if (words.length >= abbr.length()) {
                StringBuilder firstLetters = new StringBuilder();
                for (String word : words) {
                    if (!word.isEmpty()) {
                        firstLetters.append(Character.toUpperCase(word.charAt(0)));
                    }
                }
                return firstLetters.toString().contains(abbr);
            }
        }
        
        return false;
    }
} 