package com.applicantai.resumeoptimizer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing the result of matching a resume with a job description.
 */
@Entity
@Table(name = "match_results")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;
    
    @ManyToOne
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;
    
    @Column(name = "overall_match_score")
    private int overallMatchScore;
    
    @Column(name = "skill_match_score")
    private int skillMatchScore;
    
    @Column(name = "experience_match_score")
    private int experienceMatchScore;
    
    @Column(name = "education_match_score")
    private int educationMatchScore;
    
    @Column(name = "cultural_fit_score")
    private int culturalFitScore;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> matchedSkills = new HashMap<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    private Map<String, Integer> missingSkills = new HashMap<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> recommendations = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Adds a matched skill with its relevance score.
     *
     * @param skillName The skill name
     * @param relevanceScore The relevance score (0-100)
     */
    public void addMatchedSkill(String skillName, int relevanceScore) {
        this.matchedSkills.put(skillName, relevanceScore);
    }
    
    /**
     * Adds a missing skill with its importance score.
     *
     * @param skillName The skill name
     * @param importanceScore The importance score (0-100)
     */
    public void addMissingSkill(String skillName, int importanceScore) {
        this.missingSkills.put(skillName, importanceScore);
    }
    
    /**
     * Adds a recommendation.
     *
     * @param recommendation The recommendation
     */
    public void addRecommendation(String recommendation) {
        this.recommendations.add(recommendation);
    }
} 