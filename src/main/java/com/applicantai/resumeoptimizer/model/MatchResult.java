package com.applicantai.resumeoptimizer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume resume;
    
    @ManyToOne
    @JoinColumn(name = "job_description_id")
    private JobDescription jobDescription;
    
    @Column(name = "overall_match_score")
    private Integer overallMatchScore;
    
    @Column(name = "skill_match_score")
    private Integer skillMatchScore;
    
    @Column(name = "experience_match_score")
    private Integer experienceMatchScore;
    
    @Column(name = "education_match_score")
    private Integer educationMatchScore;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Transient
    private Set<Skill> matchedSkills = new HashSet<>();
    
    @Transient
    private Set<Skill> missingSkills = new HashSet<>();
    
    @Transient
    private Map<String, Integer> matchedSkillScores = new HashMap<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> recommendations = new ArrayList<>();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Adds a matched skill to the result.
     *
     * @param skill The skill to add
     * @param score The match score for the skill
     */
    public void addMatchedSkill(Skill skill, int score) {
        matchedSkills.add(skill);
        matchedSkillScores.put(skill.getName(), score);
    }
    
    /**
     * Adds a missing skill to the result.
     *
     * @param skill The missing skill
     */
    public void addMissingSkill(Skill skill) {
        missingSkills.add(skill);
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