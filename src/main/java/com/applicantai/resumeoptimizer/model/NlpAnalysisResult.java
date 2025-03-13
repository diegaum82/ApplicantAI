package com.applicantai.resumeoptimizer.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Model class representing the results of NLP analysis on text content.
 */
public class NlpAnalysisResult {
    
    private Set<Skill> identifiedSkills;
    private Map<String, Double> keyPhrases;
    private List<String> namedEntities;
    private Map<String, String> attributes;
    private List<String> suggestions;
    
    public NlpAnalysisResult() {
        this.identifiedSkills = new HashSet<>();
        this.keyPhrases = new HashMap<>();
        this.namedEntities = new ArrayList<>();
        this.attributes = new HashMap<>();
        this.suggestions = new ArrayList<>();
    }
    
    /**
     * Adds a skill to the identified skills set.
     *
     * @param skill The skill to add
     */
    public void addSkill(Skill skill) {
        this.identifiedSkills.add(skill);
    }
    
    /**
     * Adds a key phrase with its importance score.
     *
     * @param phrase The key phrase
     * @param score The importance score (0.0-1.0)
     */
    public void addKeyPhrase(String phrase, double score) {
        this.keyPhrases.put(phrase, score);
    }
    
    /**
     * Adds a named entity.
     *
     * @param entity The named entity
     */
    public void addNamedEntity(String entity) {
        this.namedEntities.add(entity);
    }
    
    /**
     * Adds an attribute.
     *
     * @param key The attribute key
     * @param value The attribute value
     */
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
    
    /**
     * Adds a suggestion.
     *
     * @param suggestion The suggestion
     */
    public void addSuggestion(String suggestion) {
        this.suggestions.add(suggestion);
    }
    
    // Getters and Setters
    
    public Set<Skill> getIdentifiedSkills() {
        return identifiedSkills;
    }
    
    public void setIdentifiedSkills(Set<Skill> identifiedSkills) {
        this.identifiedSkills = identifiedSkills;
    }
    
    public Map<String, Double> getKeyPhrases() {
        return keyPhrases;
    }
    
    public void setKeyPhrases(Map<String, Double> keyPhrases) {
        this.keyPhrases = keyPhrases;
    }
    
    public List<String> getNamedEntities() {
        return namedEntities;
    }
    
    public void setNamedEntities(List<String> namedEntities) {
        this.namedEntities = namedEntities;
    }
    
    public Map<String, String> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
    
    public List<String> getSuggestions() {
        return suggestions;
    }
    
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
    
    @Override
    public String toString() {
        return "NlpAnalysisResult{" +
                "identifiedSkills=" + identifiedSkills.size() +
                ", keyPhrases=" + keyPhrases.size() +
                ", namedEntities=" + namedEntities.size() +
                ", attributes=" + attributes.size() +
                ", suggestions=" + suggestions.size() +
                '}';
    }
} 