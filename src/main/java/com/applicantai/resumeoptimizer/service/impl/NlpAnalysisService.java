package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.NlpAnalysisResult;
import com.applicantai.resumeoptimizer.model.Skill;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service for performing NLP analysis on text content.
 * This is a core service used by both resume processing and job description analysis.
 */
@Service
public class NlpAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(NlpAnalysisService.class);
    
    // Common technical skills patterns
    private static final Pattern PROGRAMMING_LANGUAGES_PATTERN = Pattern.compile(
            "\\b(Java|Python|C\\+\\+|JavaScript|TypeScript|Ruby|PHP|Swift|Kotlin|Go|Rust|C#|Scala|Perl|R|MATLAB|Groovy|Dart|Objective-C|Shell|Bash|PowerShell)\\b",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern FRAMEWORKS_PATTERN = Pattern.compile(
            "\\b(Spring|Spring Boot|React|Angular|Vue|Django|Flask|Express|Laravel|Ruby on Rails|ASP\\.NET|Hibernate|JPA|Struts|TensorFlow|PyTorch|Keras|Scikit-learn|Bootstrap|jQuery|Node\\.js)\\b",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern DATABASES_PATTERN = Pattern.compile(
            "\\b(SQL|MySQL|PostgreSQL|Oracle|SQL Server|MongoDB|Cassandra|Redis|DynamoDB|Elasticsearch|Neo4j|MariaDB|SQLite|Firebase|Couchbase)\\b",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern CLOUD_PATTERN = Pattern.compile(
            "\\b(AWS|Amazon Web Services|EC2|S3|Lambda|Azure|Microsoft Azure|Google Cloud|GCP|Kubernetes|Docker|Terraform|CloudFormation|Heroku|Digital Ocean)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Common soft skills patterns
    private static final Pattern SOFT_SKILLS_PATTERN = Pattern.compile(
            "\\b(leadership|communication|teamwork|problem.?solving|critical.?thinking|time.?management|adaptability|flexibility|creativity|work.?ethic|attention.?to.?detail|collaboration|interpersonal|analytical|organizational|decision.?making)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Education patterns
    private static final Pattern EDUCATION_PATTERN = Pattern.compile(
            "\\b(Bachelor|Master|PhD|Doctorate|BSc|MSc|BA|MA|MBA|BBA|B\\.S\\.|M\\.S\\.|B\\.A\\.|M\\.A\\.|Ph\\.D\\.)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Experience level patterns
    private static final Pattern EXPERIENCE_LEVEL_PATTERN = Pattern.compile(
            "\\b(\\d+)\\+?\\s+(year|yr)s?\\b|\\b(junior|senior|mid-level|lead|principal|entry.?level|experienced)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Seniority level patterns
    private static final Pattern SENIORITY_PATTERN = Pattern.compile(
            "\\b(junior|senior|mid-level|lead|principal|chief|head|director|manager|executive|entry.?level|intern|associate|staff)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Industry patterns
    private static final Pattern INDUSTRY_PATTERN = Pattern.compile(
            "\\b(technology|finance|healthcare|education|retail|manufacturing|consulting|media|entertainment|government|non-profit|telecommunications|automotive|aerospace|energy|pharmaceutical|insurance|real.?estate|hospitality|transportation)\\b",
            Pattern.CASE_INSENSITIVE);
    
    // Common skill categories
    private static final Map<String, String> SKILL_CATEGORIES = new HashMap<>();
    
    static {
        // Initialize skill categories
        SKILL_CATEGORIES.put("Java", "Programming Language");
        SKILL_CATEGORIES.put("Python", "Programming Language");
        SKILL_CATEGORIES.put("JavaScript", "Programming Language");
        SKILL_CATEGORIES.put("TypeScript", "Programming Language");
        SKILL_CATEGORIES.put("C++", "Programming Language");
        SKILL_CATEGORIES.put("C#", "Programming Language");
        SKILL_CATEGORIES.put("Ruby", "Programming Language");
        SKILL_CATEGORIES.put("PHP", "Programming Language");
        SKILL_CATEGORIES.put("Swift", "Programming Language");
        SKILL_CATEGORIES.put("Kotlin", "Programming Language");
        SKILL_CATEGORIES.put("Go", "Programming Language");
        SKILL_CATEGORIES.put("Rust", "Programming Language");
        
        SKILL_CATEGORIES.put("Spring", "Framework");
        SKILL_CATEGORIES.put("Spring Boot", "Framework");
        SKILL_CATEGORIES.put("React", "Framework");
        SKILL_CATEGORIES.put("Angular", "Framework");
        SKILL_CATEGORIES.put("Vue", "Framework");
        SKILL_CATEGORIES.put("Django", "Framework");
        SKILL_CATEGORIES.put("Flask", "Framework");
        SKILL_CATEGORIES.put("Express", "Framework");
        SKILL_CATEGORIES.put("Laravel", "Framework");
        SKILL_CATEGORIES.put("ASP.NET", "Framework");
        
        SKILL_CATEGORIES.put("MySQL", "Database");
        SKILL_CATEGORIES.put("PostgreSQL", "Database");
        SKILL_CATEGORIES.put("MongoDB", "Database");
        SKILL_CATEGORIES.put("Oracle", "Database");
        SKILL_CATEGORIES.put("SQL Server", "Database");
        SKILL_CATEGORIES.put("Redis", "Database");
        SKILL_CATEGORIES.put("Elasticsearch", "Database");
        
        SKILL_CATEGORIES.put("AWS", "Cloud");
        SKILL_CATEGORIES.put("Azure", "Cloud");
        SKILL_CATEGORIES.put("Google Cloud", "Cloud");
        SKILL_CATEGORIES.put("Kubernetes", "DevOps");
        SKILL_CATEGORIES.put("Docker", "DevOps");
        SKILL_CATEGORIES.put("Jenkins", "DevOps");
        SKILL_CATEGORIES.put("Git", "DevOps");
        
        SKILL_CATEGORIES.put("Leadership", "Soft Skill");
        SKILL_CATEGORIES.put("Communication", "Soft Skill");
        SKILL_CATEGORIES.put("Teamwork", "Soft Skill");
        SKILL_CATEGORIES.put("Problem Solving", "Soft Skill");
        SKILL_CATEGORIES.put("Critical Thinking", "Soft Skill");
        SKILL_CATEGORIES.put("Time Management", "Soft Skill");
    }
    
    @Autowired
    private ResourceLoader resourceLoader;
    
    @Value("${app.nlp.models.path:classpath:nlp/models/}")
    private String nlpModelsPath;
    
    private SentenceDetectorME sentenceDetector;
    private TokenizerME tokenizer;
    private POSTaggerME posTagger;
    private NameFinderME personNameFinder;
    private NameFinderME organizationNameFinder;
    private NameFinderME locationNameFinder;
    
    /**
     * Initializes the NLP models.
     * This method is called after the bean is constructed.
     */
    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing NLP models from path: {}", nlpModelsPath);
            
            // Try to load models
            try {
                sentenceDetector = new SentenceDetectorME(loadModel("en-sent.bin", SentenceModel.class));
                logger.info("Sentence detector model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load sentence detector model: {}", e.getMessage());
                logger.info("Will use pattern-based sentence detection as fallback");
            }
            
            try {
                tokenizer = new TokenizerME(loadModel("en-token.bin", TokenizerModel.class));
                logger.info("Tokenizer model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load tokenizer model: {}", e.getMessage());
                logger.info("Will use simple whitespace tokenization as fallback");
            }
            
            try {
                posTagger = new POSTaggerME(loadModel("en-pos-maxent.bin", POSModel.class));
                logger.info("POS tagger model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load POS tagger model: {}", e.getMessage());
                logger.info("POS tagging will be skipped");
            }
            
            try {
                personNameFinder = new NameFinderME(loadModel("en-ner-person.bin", TokenNameFinderModel.class));
                logger.info("Person name finder model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load person name finder model: {}", e.getMessage());
                logger.info("Will use pattern-based person name detection as fallback");
            }
            
            try {
                organizationNameFinder = new NameFinderME(loadModel("en-ner-organization.bin", TokenNameFinderModel.class));
                logger.info("Organization name finder model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load organization name finder model: {}", e.getMessage());
                logger.info("Will use pattern-based organization detection as fallback");
            }
            
            try {
                locationNameFinder = new NameFinderME(loadModel("en-ner-location.bin", TokenNameFinderModel.class));
                logger.info("Location name finder model loaded successfully");
            } catch (Exception e) {
                logger.warn("Failed to load location name finder model: {}", e.getMessage());
                logger.info("Will use pattern-based location detection as fallback");
            }
            
            logger.info("NLP models initialization completed");
        } catch (Exception e) {
            logger.error("Error initializing NLP models: {}", e.getMessage());
            logger.info("Will use pattern-based analysis as fallback");
        }
    }
    
    /**
     * Analyzes text content and extracts skills, key phrases, and other information.
     *
     * @param text The text to analyze
     * @return NlpAnalysisResult containing the analysis results
     */
    public NlpAnalysisResult analyzeText(String text) {
        logger.debug("Analyzing text with NLP: {} characters", text.length());
        
        NlpAnalysisResult result = new NlpAnalysisResult();
        
        // Extract skills using pattern matching
        extractSkills(text, result);
        
        // Extract key phrases
        extractKeyPhrases(text, result);
        
        // Extract named entities
        extractNamedEntities(text, result);
        
        // Extract attributes
        extractAttributes(text, result);
        
        logger.debug("NLP analysis completed: {} skills, {} key phrases, {} named entities",
                result.getIdentifiedSkills().size(),
                result.getKeyPhrases().size(),
                result.getNamedEntities().size());
        
        return result;
    }
    
    /**
     * Extracts skills from text using pattern matching.
     *
     * @param text The text to analyze
     * @param result The result object to populate
     */
    private void extractSkills(String text, NlpAnalysisResult result) {
        logger.debug("Extracting skills from text");
        
        // Extract programming languages
        extractSkillsByPattern(text, PROGRAMMING_LANGUAGES_PATTERN, "Programming Language", result);
        
        // Extract frameworks
        extractSkillsByPattern(text, FRAMEWORKS_PATTERN, "Framework", result);
        
        // Extract databases
        extractSkillsByPattern(text, DATABASES_PATTERN, "Database", result);
        
        // Extract cloud technologies
        extractSkillsByPattern(text, CLOUD_PATTERN, "Cloud Technology", result);
        
        // Extract soft skills
        extractSkillsByPattern(text, SOFT_SKILLS_PATTERN, "Soft Skill", result);
        
        // Special case for "Spring Boot" which might be missed by the regex
        if (text.toLowerCase().contains("spring boot")) {
            Skill springBootSkill = new Skill();
            springBootSkill.setName("Spring Boot");
            springBootSkill.setCategory("Framework");
            springBootSkill.setRelevanceScore(100);
            result.addSkill(springBootSkill);
        }
    }
    
    /**
     * Extracts skills using a regex pattern and adds them to the result.
     *
     * @param text The text to analyze
     * @param pattern The pattern to match
     * @param category The skill category
     * @param result The result object to populate
     */
    private void extractSkillsByPattern(String text, Pattern pattern, String category, NlpAnalysisResult result) {
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            String skillName = matcher.group().trim();
            
            // Create a new skill
            Skill skill = new Skill();
            skill.setName(skillName);
            skill.setCategory(category);
            
            // Add the skill to the result
            result.addSkill(skill);
            
            // Add as a key phrase with high importance
            result.addKeyPhrase(skillName, 0.9);
        }
    }
    
    /**
     * Extracts key phrases from text.
     *
     * @param text The text to analyze
     * @param result The result object to populate
     */
    private void extractKeyPhrases(String text, NlpAnalysisResult result) {
        // For now, we'll use a simple approach based on sentence importance
        // In a real implementation, you would use TF-IDF or other algorithms
        
        // Split text into sentences
        String[] sentences = text.split("[.!?]");
        
        for (String sentence : sentences) {
            sentence = sentence.trim();
            if (sentence.isEmpty()) {
                continue;
            }
            
            // Check if sentence contains important keywords
            double importance = calculateSentenceImportance(sentence);
            
            // If sentence is important, extract key phrases
            if (importance > 0.5) {
                // For simplicity, we'll just use the first few words as a key phrase
                String[] words = sentence.split("\\s+");
                if (words.length >= 3) {
                    String keyPhrase = String.join(" ", Arrays.copyOfRange(words, 0, Math.min(5, words.length)));
                    result.addKeyPhrase(keyPhrase, importance);
                }
            }
        }
    }
    
    /**
     * Calculates the importance of a sentence based on keywords.
     *
     * @param sentence The sentence to analyze
     * @return Importance score (0.0-1.0)
     */
    private double calculateSentenceImportance(String sentence) {
        // Simple heuristic: check for important keywords
        String lowerSentence = sentence.toLowerCase();
        
        double score = 0.0;
        
        // Check for skill-related terms
        if (PROGRAMMING_LANGUAGES_PATTERN.matcher(lowerSentence).find()) score += 0.3;
        if (FRAMEWORKS_PATTERN.matcher(lowerSentence).find()) score += 0.3;
        if (DATABASES_PATTERN.matcher(lowerSentence).find()) score += 0.3;
        if (CLOUD_PATTERN.matcher(lowerSentence).find()) score += 0.3;
        
        // Check for experience-related terms
        if (lowerSentence.contains("experience")) score += 0.2;
        if (lowerSentence.contains("year")) score += 0.2;
        if (EXPERIENCE_LEVEL_PATTERN.matcher(lowerSentence).find()) score += 0.3;
        
        // Check for education-related terms
        if (EDUCATION_PATTERN.matcher(lowerSentence).find()) score += 0.2;
        if (lowerSentence.contains("degree")) score += 0.2;
        if (lowerSentence.contains("university")) score += 0.2;
        
        // Check for job-related terms
        if (lowerSentence.contains("job")) score += 0.1;
        if (lowerSentence.contains("position")) score += 0.1;
        if (lowerSentence.contains("role")) score += 0.1;
        if (lowerSentence.contains("responsibility")) score += 0.2;
        
        // Cap at 1.0
        return Math.min(1.0, score);
    }
    
    /**
     * Extracts named entities from text.
     *
     * @param text The text to analyze
     * @param result The result object to populate
     */
    private void extractNamedEntities(String text, NlpAnalysisResult result) {
        // For now, we'll use a simple approach based on pattern matching
        // In a real implementation, you would use the OpenNLP models
        
        // Extract organization names (simplified)
        Pattern orgPattern = Pattern.compile("\\b([A-Z][a-z]+ )+(?:Inc|LLC|Ltd|Corporation|Corp|Company|Co)\\b");
        Matcher orgMatcher = orgPattern.matcher(text);
        while (orgMatcher.find()) {
            result.addNamedEntity(orgMatcher.group().trim());
        }
        
        // Extract locations (simplified)
        Pattern locPattern = Pattern.compile("\\b(?:San Francisco|New York|Seattle|Boston|Chicago|Austin|Los Angeles|London|Berlin|Tokyo|Remote)\\b");
        Matcher locMatcher = locPattern.matcher(text);
        while (locMatcher.find()) {
            result.addNamedEntity(locMatcher.group().trim());
        }
    }
    
    /**
     * Extracts attributes from text.
     *
     * @param text The text to analyze
     * @param result The result object to populate
     */
    private void extractAttributes(String text, NlpAnalysisResult result) {
        // Extract experience level
        Matcher expMatcher = EXPERIENCE_LEVEL_PATTERN.matcher(text);
        if (expMatcher.find()) {
            result.addAttribute("experience_level", expMatcher.group().trim());
        }
        
        // Extract seniority level
        Matcher seniorityMatcher = SENIORITY_PATTERN.matcher(text);
        if (seniorityMatcher.find()) {
            result.addAttribute("seniority_level", seniorityMatcher.group().trim());
        }
        
        // Extract industry
        Matcher industryMatcher = INDUSTRY_PATTERN.matcher(text);
        if (industryMatcher.find()) {
            result.addAttribute("industry", industryMatcher.group().trim());
        }
        
        // Extract education level
        Matcher eduMatcher = EDUCATION_PATTERN.matcher(text);
        if (eduMatcher.find()) {
            result.addAttribute("education_level", eduMatcher.group().trim());
        }
    }
    
    /**
     * Helper method to load OpenNLP models.
     *
     * @param modelName The name of the model file
     * @param modelClass The class of the model
     * @return The loaded model
     * @throws IOException If the model cannot be loaded
     */
    private <T> T loadModel(String modelName, Class<T> modelClass) throws IOException {
        try {
            Resource resource = resourceLoader.getResource(nlpModelsPath + modelName);
            try (InputStream modelIn = resource.getInputStream()) {
                if (modelClass == SentenceModel.class) {
                    return modelClass.cast(new SentenceModel(modelIn));
                } else if (modelClass == TokenizerModel.class) {
                    return modelClass.cast(new TokenizerModel(modelIn));
                } else if (modelClass == POSModel.class) {
                    return modelClass.cast(new POSModel(modelIn));
                } else if (modelClass == TokenNameFinderModel.class) {
                    return modelClass.cast(new TokenNameFinderModel(modelIn));
                } else {
                    throw new IllegalArgumentException("Unsupported model class: " + modelClass.getName());
                }
            }
        } catch (IOException e) {
            logger.error("Failed to load model {}: {}", modelName, e.getMessage());
            throw e;
        }
    }

    // Add fallback methods for when models are not available
    private String[] detectSentencesFallback(String text) {
        // Simple sentence detection using periods, question marks, and exclamation points
        return text.split("[.!?]\\s+");
    }

    private String[] tokenizeFallback(String text) {
        // Simple tokenization by whitespace
        return text.trim().split("\\s+");
    }
} 