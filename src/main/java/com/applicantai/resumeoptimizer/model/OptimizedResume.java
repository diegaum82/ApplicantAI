package com.applicantai.resumeoptimizer.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an optimized version of a resume.
 */
@Entity
@Table(name = "optimized_resumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedResume {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "original_resume_id")
    private Resume originalResume;
    
    @ManyToOne
    @JoinColumn(name = "target_job_description_id")
    private JobDescription targetJobDescription;
    
    @OneToOne
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;
    
    @Lob
    @Column(name = "optimized_content", columnDefinition = "TEXT")
    private String optimizedContent;
    
    @Column(name = "match_score")
    private Integer matchScore;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> suggestions = new ArrayList<>();
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Adds a suggestion to the optimized resume.
     *
     * @param suggestion The suggestion to add
     */
    public void addSuggestion(String suggestion) {
        if (suggestions == null) {
            suggestions = new ArrayList<>();
        }
        suggestions.add(suggestion);
    }
} 