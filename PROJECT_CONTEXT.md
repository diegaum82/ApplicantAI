# ApplicantAI Resume Optimizer - Project Context

## 1. Project Overview
The ApplicantAI Resume Optimizer is a career transformation engine built on Spring Boot that uses advanced NLP and AI techniques to optimize resumes based on job descriptions. The system analyzes both resumes and job descriptions, provides matching scores, and suggests optimizations to improve candidacy.

Vision: To create a system that makes traditional resume builders obsolete through precise job matching, self-evolving architecture, and career path prediction.

## 2. Current State
- **Status**: Early development stage (Phase 1: Foundation)
- **Last Updated**: [Current Date]

### 2.1 Implemented Features
- Basic project structure with Spring Boot
- Domain models for Resume, Skill, and JobDescription
- Maven dependencies configuration
- Basic authentication (Spring Security)
- Project architecture documentation
- PDF processing service with OCR fallback
- Resume upload controller and UI
- Caffeine caching for improved performance
- NLP Analysis Module for text processing and skill extraction
- Job description analysis service
- Resume-job matching service with optimization suggestions
- Skill and industry dictionaries for entity recognition
- Semantic similarity for enhanced skill matching
- Word embedding service for improved matching accuracy
- Enhanced UI for match results with visualization
- Categorized optimization suggestions with accordion display

### 2.2 In Progress
- Resume section detection refinement
- Error handling improvements
- File validation
- NLP model integration and training
- Job description upload UI
- Advanced word embedding implementation

### 2.3 Next Priorities
- UI development for results display
- Resume optimization suggestions UI
- Job description upload and analysis UI
- Advanced matching algorithm with semantic understanding
- Career trajectory prediction
- Integration of actual word embeddings for improved matching

## 3. Technical Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Database**: H2 (development), PostgreSQL (planned for production)
- **Frontend**: Thymeleaf (implemented), React (planned)
- **Key Libraries**:
  - Apache PDFBox (PDF processing)
  - Tesseract OCR (image text extraction)
  - OpenNLP (natural language processing)
  - DeepLearning4J (machine learning)
  - Caffeine (caching)

## 4. Architecture Overview
The application follows a layered architecture with:

### 4.1 Core Components
- **PDF Alchemy Engine**: Processes and extracts text from resumes (Implemented)
- **NLP Cortex**: Analyzes job descriptions and resumes (Implemented)
- **Optimization Matrix**: Generates match scores and suggestions (Implemented)
- **Reality Fabrication Layer**: Renders and creates optimized documents (Planned)

### 4.2 Key Service Interfaces
```java
// Primary service interfaces that define the system boundaries
public interface ResumeProcessingService {
    ResumeContent processResume(MultipartFile file) throws IOException;
    Set<Skill> identifySkills(ResumeContent content);
    ResumeContent detectSections(String content);
    int evaluateExtractionQuality(ResumeContent content);
}

public interface JobDescriptionAnalysisService {
    Set<Skill> extractRequiredSkills(String jobDescription);
    Set<Skill> extractRequiredSkills(JobDescription jobDescription);
    Map<String, Double> extractKeyPhrases(String jobDescription);
    String determineSeniorityLevel(String jobDescription);
    String extractIndustry(String jobDescription);
    Map<String, String> analyzeJobAttributes(String jobDescription);
}

public interface MatchingService {
    MatchResult matchResumeWithJob(Resume resume, JobDescription jobDescription);
    MatchResult matchResumeWithJobText(String resumeContent, String jobDescriptionContent);
    OptimizedResume generateOptimizedResume(Resume resume, JobDescription jobDescription);
    OptimizedResume generateOptimizationSuggestions(MatchResult matchResult);
}

public interface WordEmbeddingService {
    double calculateSimilarity(String word1, String word2);
    Map<String, Double> findSimilarWords(String word, List<String> candidates, double threshold);
    double calculateSkillSimilarity(String skill, String requirement);
    boolean isReady();
}
```

## 5. Database Schema
- Users
- Resumes
- JobDescriptions
- Skills
- ResumeSkills (junction)
- JobSkills (junction)
- OptimizedResumes
- MatchResults

## 6. API Endpoints (Implemented & Planned)
- `GET /resume/upload` - Display resume upload form (Implemented)
- `POST /resume/upload` - Upload and process a resume (Implemented)
- `POST /api/v1/resume/upload` - API endpoint for resume upload (Implemented)
- `POST /api/v1/match` - Compare resume with job description (Implemented)
- `GET /match` - Display match form (Implemented)
- `POST /api/v1/jobs` - Submit a job description (Planned)
- `GET /api/v1/optimize/{id}` - Get optimized resume (Planned)

## 7. Development Workflow
- Repository: GitHub (private)
- CI/CD: To be implemented
- Development Environment: Local with H2 database
- Testing: JUnit and Mockito

## 8. Known Challenges
- Accurate extraction of skills from unstructured text
- Semantic understanding of job requirements
- Optimizing resume wording without changing factual content
- Balancing keyword optimization with human readability
- Handling various PDF formats and layouts consistently
- Managing multiple service implementations with Spring dependency injection
- Improving NLP model accuracy without extensive training data
- Implementing sophisticated word embeddings for semantic matching

## 9. References
- [SYSTEM_MISSION.md](SYSTEM_MISSION.md) - Project vision and architecture
- [Implementation Roadmap](SYSTEM_MISSION.md#project-implementation-roadmap) - Development phases
- [API Documentation](docs/api.md) - When available

## 10. Progress Log
```
YYYY-MM-DD: Project initialization, created basic models and configuration
[Previous Date]: Implemented PDF processing service, resume upload controller, and UI templates
[Previous Date]: Implemented NLP Analysis Module, job description analysis, and resume-job matching
[Current Date]: Enhanced matching algorithm with semantic similarity and improved UI for optimization suggestions
```

## PDF Alchemy Engine

The PDF Alchemy Engine is the core text extraction component of the ApplicantAI system. It's responsible for extracting content from resume PDF files with high accuracy and reliability. Key features include:

- **Direct Text Extraction**: Uses Apache PDFBox to extract text directly from PDF files
- **OCR Fallback**: Automatically falls back to OCR (Optical Character Recognition) for image-based PDFs
- **Section Recognition**: Intelligently identifies resume sections like Education, Experience, Skills
- **Bullet Point Extraction**: Identifies and extracts bullet points commonly found in resumes
- **Performance Optimization**: Implements caching with Caffeine for improved performance on repeated uploads
- **Quality Metrics**: Provides quality scores and detailed warnings about the extraction process

The engine is designed to handle a wide variety of PDF formats and structures, gracefully degrading from direct extraction to OCR when needed. It provides structured content that can be analyzed by downstream components.

### Configuration

The PDF Alchemy Engine can be configured through the `application.properties` file:

```properties
# Directory for temporary files
app.resume.storage.location=./resume-uploads/

# Enable/disable OCR fallback
app.ocr.use-fallback=true

# Tesseract OCR path and language
app.ocr.tesseract-data-path=/usr/share/tesseract-ocr/4.0/tessdata/
app.ocr.language=eng
```

### Caching

Resume processing is an expensive operation, so the system uses Caffeine caching to improve performance for repeated uploads of the same file. The `@Cacheable` annotation is used on the `processResume` method with a cache key based on the file name and size:

```java
@Cacheable(value = "resumeCache", key = "#file.originalFilename + '-' + #file.size")
public ResumeContent processResume(MultipartFile file) throws IOException {
    // Processing logic
}
```

This significantly improves response time for duplicate uploads during testing or development.

## NLP Cortex

The NLP Cortex is the natural language processing component of the ApplicantAI system. It analyzes text content from both resumes and job descriptions to extract meaningful information. Key features include:

- **Skill Extraction**: Identifies technical and soft skills using pattern matching and dictionaries
- **Key Phrase Extraction**: Extracts important phrases with their relevance scores
- **Named Entity Recognition**: Identifies organizations, locations, and other entities
- **Attribute Extraction**: Determines attributes like experience level, seniority, and industry
- **Configurable Analysis**: Supports different levels of analysis depth based on configuration

The NLP Cortex uses a combination of pattern matching, dictionaries, and OpenNLP models to achieve accurate results without requiring extensive training data.

### Configuration

The NLP Cortex can be configured through the `application.properties` file:

```properties
# NLP Configuration
app.nlp.models.path=classpath:nlp/models/
app.nlp.min-confidence-score=0.7
app.nlp.enable-advanced-analysis=true
app.nlp.skill-dictionary.path=classpath:nlp/dictionaries/skills.json
app.nlp.industry-dictionary.path=classpath:nlp/dictionaries/industries.json
```

### Dictionaries

The NLP Cortex uses JSON dictionaries to improve entity recognition:

- **Skills Dictionary**: Contains programming languages, frameworks, databases, cloud technologies, and soft skills
- **Industries Dictionary**: Contains industries, job levels, and employment types

These dictionaries help the system recognize domain-specific terms and their relationships.

## Optimization Matrix

The Optimization Matrix is the component responsible for comparing resumes with job descriptions and generating optimization suggestions. Key features include:

- **Match Score Calculation**: Computes an overall match score between a resume and job description
- **Skill Matching**: Identifies matching skills and their relevance using semantic similarity
- **Missing Skills Detection**: Identifies skills required by the job but missing from the resume
- **Optimization Suggestions**: Generates actionable suggestions to improve the resume
- **Phrase Recommendations**: Suggests key phrases from the job description to include in the resume
- **Semantic Matching**: Uses word embeddings to find semantically similar skills and phrases

The Optimization Matrix uses the results from the NLP Cortex and Word Embedding Service to provide meaningful insights and suggestions.

### Configuration

The Optimization Matrix can be configured through the `application.properties` file:

```properties
# Matching Configuration
app.matching.similarity-threshold=0.7
app.matching.skill-weight=0.6
app.matching.experience-weight=0.3
app.matching.education-weight=0.1
app.matching.enable-semantic-matching=true
```

## Word Embedding Service

The Word Embedding Service provides semantic similarity capabilities to the system, enabling more accurate matching between resume skills and job requirements. Key features include:

- **Semantic Similarity**: Calculates similarity between words and phrases
- **Synonym Recognition**: Identifies synonyms and related terms
- **Levenshtein Distance**: Uses edit distance to find similar terms
- **Abbreviation Detection**: Recognizes common abbreviations and their full forms

The current implementation uses a simple approach with predefined synonyms and string similarity algorithms, but it will be replaced with a more sophisticated implementation using actual word embeddings in the future.

## Resume Upload UI

The system provides a modern, responsive UI for resume uploads with the following features:
- Drag-and-drop file upload interface
- Client-side validation for file types
- Progress indication during upload
- Clear error messaging
- Responsive design for mobile and desktop

The UI is implemented using Thymeleaf templates with Bootstrap for styling and JavaScript for enhanced interactivity.

## Match Results UI

The system provides a comprehensive UI for displaying match results with the following features:
- Visual representation of match scores using circular progress indicators
- Detailed breakdown of skill, experience, and education scores
- Categorized display of matching and missing skills
- Accordion-based organization of optimization suggestions by category
- Color-coded indicators for different match quality levels

The UI is designed to be intuitive and actionable, helping users understand how to improve their resumes for specific job descriptions.

---

*Note to AI assistants: This document serves as a continuity guide. When helping with this project, please:
1. Read this document and SYSTEM_MISSION.md to understand context
2. Update this document with significant changes you implement
3. Maintain the architectural vision while addressing immediate needs
4. Document any new challenges or design decisions* 