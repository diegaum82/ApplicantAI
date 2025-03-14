package com.applicantai.resumeoptimizer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for serving the match form view.
 */
@Controller
@RequestMapping("/match")
public class MatchViewController {

    /**
     * Displays the form for matching resumes with job descriptions.
     *
     * @return The view name
     */
    @GetMapping
    public String showMatchForm() {
        return "match/form";
    }
} 