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
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing an optimized version of a resume for a specific job description.
 */
@Entity
@Table(name = "optimized_resumes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptimizedResume {
    
    @Id
    @GeneratedValue
    private UUID id;
    
    @ManyToOne
    @JoinColumn(name = "resume_id")
    private Resume originalResume;
    
    @ManyToOne
    @JoinColumn(name = "job_description_id")
    private JobDescription targetJobDescription;
    
    @OneToOne
    @JoinColumn(name = "match_result_id")
    private MatchResult matchResult;
    
    @Column(name = "optimized_content", columnDefinition = "TEXT")
    private String optimizedContent;
    
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> changesApplied = new ArrayList<>();
    
    @Column(name = "before_score")
    private int beforeScore;
    
    @Column(name = "after_score")
    private int afterScore;
    
    @Column(name = "download_url")
    private String downloadUrl;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Adds a change that was applied during optimization.
     *
     * @param change Description of the change
     */
    public void addChange(String change) {
        this.changesApplied.add(change);
    }
} 