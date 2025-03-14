package com.applicantai.resumeoptimizer.service;

import java.util.List;
import java.util.Map;

/**
 * Service interface for word embedding operations.
 * This service provides methods for calculating semantic similarity between words and phrases
 * using word embeddings, which helps in more accurate skill matching.
 */
public interface WordEmbeddingService {
    
    /**
     * Calculates the semantic similarity between two words or phrases.
     * 
     * @param word1 The first word or phrase
     * @param word2 The second word or phrase
     * @return A similarity score between 0.0 (not similar) and 1.0 (identical)
     */
    double calculateSimilarity(String word1, String word2);
    
    /**
     * Finds the most similar words to the input word from the given list.
     * 
     * @param word The input word or phrase
     * @param candidates List of candidate words or phrases to compare against
     * @param threshold Minimum similarity threshold (0.0 to 1.0)
     * @return Map of similar words with their similarity scores
     */
    Map<String, Double> findSimilarWords(String word, List<String> candidates, double threshold);
    
    /**
     * Calculates the semantic similarity between a skill and a job requirement.
     * This is specialized for skill matching in the resume optimization context.
     * 
     * @param skill The skill name from a resume
     * @param requirement The requirement from a job description
     * @return A similarity score between 0.0 (not similar) and 1.0 (identical)
     */
    double calculateSkillSimilarity(String skill, String requirement);
    
    /**
     * Checks if the service has embeddings loaded and is ready to use.
     * 
     * @return true if the service is ready, false otherwise
     */
    boolean isReady();
} 