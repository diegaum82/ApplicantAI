# ApplicantAI Resume Optimizer - System Architecture

This document details the architectural design of the ApplicantAI Resume Optimizer system, following the vision outlined in the SYSTEM_MISSION.md file.

## System Overview

ApplicantAI is designed as a microservices-based architecture that processes, analyzes, and optimizes resumes based on job descriptions using advanced NLP and machine learning techniques.

```
┌───────────────────────────────────────────────────────────────────────┐
│                                                                       │
│                    ApplicantAI Resume Optimizer                       │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
                 ▲                    ▲                    ▲
                 │                    │                    │
┌────────────────┼────────────────────┼────────────────────┼────────────┐
│                │                    │                    │            │
│            ┌───┴────┐          ┌────┴───┐           ┌───┴────┐       │
│            │  Web   │          │  API   │           │ Admin  │       │
│            │  UI    │          │Gateway │           │  UI    │       │
│            └───┬────┘          └────┬───┘           └───┬────┘       │
│                │                    │                    │            │
└────────────────┼────────────────────┼────────────────────┼────────────┘
                 │                    │                    │
┌────────────────┼────────────────────┼────────────────────┼────────────┐
│                │                    │                    │            │
│                ▼                    ▼                    ▼            │
│         ┌─────────────┐      ┌─────────────┐      ┌─────────────┐    │
│         │ User Auth   │      │ Resume Proc.│      │  Job Desc.  │    │
│         │  Service    │      │  Service    │      │  Service    │    │
│         └─────────────┘      └──────┬──────┘      └──────┬──────┘    │
│                                     │                     │           │
│                                     ▼                     ▼           │
│                               ┌─────────────────────────────────┐    │
│                               │                                 │    │
│                               │     Matching & Optimization     │    │
│                               │           Service               │    │
│                               │                                 │    │
│                               └───────────────┬─────────────────┘    │
│                                               │                      │
│                                               ▼                      │
│                               ┌─────────────────────────────────┐    │
│                               │                                 │    │
│                               │    Career Trajectory Service    │    │
│                               │                                 │    │
│                               └─────────────────────────────────┘    │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
                                    ▲
┌───────────────────────────────────┼──────────────────────────────────┐
│                                   │                                   │
│               ┌───────────────────┴────────────────────┐             │
│               │                                        │             │
│               │       Data Persistence Layer           │             │
│               │                                        │             │
│               └─┬──────────────┬───────────┬─────────┬─┘             │
│                 │              │           │         │               │
│              ┌──┴───┐      ┌───┴───┐   ┌───┴───┐ ┌───┴───┐           │
│              │ User │      │Resume │   │ Job   │ │Match  │           │
│              │  DB  │      │  DB   │   │  DB   │ │  DB   │           │
│              └──────┘      └───────┘   └───────┘ └───────┘           │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘
```

## Triple-Core Neural Nexus

The heart of the system is the Triple-Core Neural Nexus, which consists of three primary layers:

### 1. Quantum Analysis Layer

```
┌─────────────────────────────────────────────────────────────┐
│                   Quantum Analysis Layer                     │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ ┌─────────────────┐  ┌────────────────┐  ┌───────────────┐  │
│ │                 │  │                │  │               │  │
│ │   NLP Cortex    │  │ Compliance     │  │ Optimization  │  │
│ │   - BERT Models │  │ Grid           │  │ Matrix        │  │
│ │   - Named Entity│  │ - GDPR Rules   │  │ - ATS Sim     │  │
│ │     Recognition │  │ - Auto-Redact  │  │ - Cultural Fit│  │
│ │   - Skill Extrac│  │   Engine       │  │   Predictor   │  │
│ │                 │  │                │  │               │  │
│ └────────┬────────┘  └────────┬───────┘  └───────┬───────┘  │
│          │                    │                  │          │
│          └────────────────────┼──────────────────┘          │
│                               │                              │
└───────────────────────────────┼──────────────────────────────┘
                                ▼
```

This layer processes incoming resumes and job descriptions using:
- **NLP Cortex**: Analyzes text content using BERT models and NER to extract skills, experiences, and keywords
- **Compliance Grid**: Ensures all processing follows data protection rules and handles sensitive information appropriately
- **Optimization Matrix**: Simulates ATS systems and generates cultural fit predictions

### 2. Reality Fabrication Layer

```
┌─────────────────────────────────────────────────────────────┐
│                 Reality Fabrication Layer                    │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│ ┌─────────────────┐  ┌────────────────┐  ┌───────────────┐  │
│ │                 │  │                │  │               │  │
│ │ PDF Alchemy     │  │ Semantic       │  │ Holographic   │  │
│ │ Engine          │  │ Forge          │  │ Renderer      │  │
│ │ - PDF Extraction│  │ - Skill Mapper │  │ - Dynamic UI  │  │
│ │ - OCR Processing│  │ - Job Decoder  │  │ - Resume      │  │
│ │ - Format        │  │ - Context      │  │   Templates   │  │
│ │   Normalization │  │   Analysis     │  │ - Export Engine│ │
│ │                 │  │                │  │               │  │
│ └────────┬────────┘  └────────┬───────┘  └───────┬───────┘  │
│          │                    │                  │          │
│          └────────────────────┼──────────────────┘          │
│                               │                              │
└───────────────────────────────┼──────────────────────────────┘
                                ▼
```

This layer creates and transforms documents:
- **PDF Alchemy Engine**: Extracts text from various document formats
- **Semantic Forge**: Maps skills and job requirements, decodes job descriptions
- **Holographic Renderer**: Creates optimized resumes with dynamic templates

### 3. Infinity Feedback Loop

```
┌─────────────────────────────────────────────────────────────┐
│                  Infinity Feedback Loop                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│     ┌─────────────────────┐      ┌─────────────────────┐    │
│     │                     │      │                     │    │
│     │  Career Path Oracle │      │  Salary Alchemist   │    │
│     │  - Trajectory       │      │  - Market Analysis  │    │
│     │    Prediction       │      │  - Salary Range     │    │
│     │  - Career Gap       │      │    Optimization     │    │
│     │    Analysis         │      │  - Negotiation      │    │
│     │  - Skill Growth     │      │    Strategy         │    │
│     │    Recommender      │      │                     │    │
│     │                     │      │                     │    │
│     └──────────┬──────────┘      └──────────┬──────────┘    │
│                │                            │               │
│                └────────────────┬───────────┘               │
│                                 │                           │
└─────────────────────────────────┼───────────────────────────┘
                                  ▼
                          User Feedback & 
                          Continuous Learning
```

This layer provides ongoing value through:
- **Career Path Oracle**: Predicts career trajectories and suggests skill development
- **Salary Alchemist**: Optimizes salary expectations and provides negotiation insights

## Service Components

### User Authentication Service
- Handles user registration, login, and authentication
- Manages JWT tokens and authorization
- Controls access to protected resources

### Resume Processing Service
- Accepts file uploads (PDF, DOCX, TXT)
- Extracts text and structure from documents
- Performs initial analysis of content
- Identifies skills, experience, and education

### Job Description Service
- Accepts job postings and descriptions
- Analyzes requirements and qualifications
- Extracts key skills and experience levels
- Determines job context and industry

### Word Embedding Service
- Calculates semantic similarity between words and phrases
- Identifies synonyms and related terms
- Enhances skill matching accuracy
- Supports the Matching & Optimization Service

### Matching & Optimization Service
- Compares resumes against job descriptions using semantic similarity
- Calculates match scores for different criteria
- Generates recommendations for improvements
- Creates optimized versions of resumes

### Career Trajectory Service
- Analyzes career paths based on resume content
- Predicts potential career progression
- Recommends skill development opportunities
- Provides salary insights and market analysis

## Data Flow

### Resume Upload & Analysis Flow

```
┌─────────┐     ┌───────────┐     ┌───────────────┐     ┌─────────────┐
│         │     │           │     │               │     │             │
│  User   │────▶│  Upload   │────▶│ PDF Extraction│────▶│ Text Analysis│
│         │     │  Service  │     │               │     │             │
└─────────┘     └───────────┘     └───────────────┘     └──────┬──────┘
                                                               │
┌──────────────┐     ┌─────────────┐     ┌────────────┐       │
│              │     │             │     │            │       │
│ Resume Store │◀────│ Skill Mapping│◀────│ NER Process│◀──────┘
│              │     │             │     │            │
└──────────────┘     └─────────────┘     └────────────┘
```

### Job Description Processing Flow

```
┌─────────┐     ┌───────────┐     ┌─────────────────┐     ┌───────────────┐
│         │     │           │     │                 │     │               │
│  User   │────▶│  Job API  │────▶│ Content Analysis│────▶│ Requirement   │
│         │     │           │     │                 │     │ Extraction    │
└─────────┘     └───────────┘     └─────────────────┘     └───────┬───────┘
                                                                  │
┌──────────────┐     ┌─────────────────┐     ┌────────────────┐   │
│              │     │                 │     │                │   │
│ Job Store    │◀────│ Skill Categorize│◀────│ Context Analysis│◀──┘
│              │     │                 │     │                │
└──────────────┘     └─────────────────┘     └────────────────┘
```

### Optimization Process Flow

```
┌──────────┐    ┌───────────┐    ┌─────────────┐    ┌────────────────┐
│          │    │           │    │             │    │                │
│ Resume   │───▶│ Job Desc. │───▶│ Matching    │───▶│ Gap Analysis   │
│ Data     │    │ Data      │    │ Algorithm   │    │                │
└──────────┘    └───────────┘    └─────────────┘    └────────┬───────┘
                                                             │
┌────────────┐    ┌───────────────┐    ┌─────────────────┐   │
│            │    │               │    │                 │   │
│ Optimized  │◀───│ Document      │◀───│ Content         │◀──┘
│ Resume     │    │ Generation    │    │ Optimization    │
└────────────┘    └───────────────┘    └─────────────────┘
```

### Semantic Matching Flow

```
┌──────────┐    ┌───────────┐    ┌─────────────────┐    ┌────────────────┐
│          │    │           │    │                 │    │                │
│ Resume   │───▶│ Job Desc. │───▶│ Word Embedding  │───▶│ Semantic       │
│ Skills   │    │ Skills    │    │ Service         │    │ Similarity     │
└──────────┘    └───────────┘    └─────────────────┘    └────────┬───────┘
                                                                 │
┌────────────┐    ┌───────────────┐    ┌─────────────────────┐   │
│            │    │               │    │                     │   │
│ Match      │◀───│ Score         │◀───│ Optimization        │◀──┘
│ Result     │    │ Calculation   │    │ Suggestions         │
└────────────┘    └───────────────┘    └─────────────────────┘
```

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI

### Data Processing
- **PDF Processing**: Apache PDFBox
- **OCR**: Tesseract
- **NLP**: OpenNLP, custom word embeddings
- **Machine Learning**: DeepLearning4J (planned)

### Frontend
- **View Templates**: Thymeleaf
- **CSS Framework**: Bootstrap 5
- **JavaScript**: Vanilla JS, Chart.js for visualizations
- **Future UI**: React (planned)

### Data Storage
- **Development Database**: H2
- **Production Database**: PostgreSQL (planned)
- **Caching**: Caffeine

### Security
- **Authentication**: Spring Security
- **Authorization**: Role-based access control
- **API Security**: JWT (planned)

## Component Details

### PDF Alchemy Engine (Implemented)

The PDF Alchemy Engine is responsible for extracting text from PDF resumes. It uses a multi-strategy approach:

1. **Direct Extraction**: Uses Apache PDFBox to extract text directly from PDF files
2. **OCR Fallback**: If direct extraction yields poor results, falls back to Tesseract OCR
3. **Quality Assessment**: Evaluates the quality of extracted text and provides feedback

Key classes:
- `PdfProcessingService`: Main service for PDF processing
- `ResumeContent`: Model for storing extracted content
- `OcrService`: Service for OCR processing

### NLP Cortex (Implemented)

The NLP Cortex analyzes text content to extract meaningful information:

1. **Skill Extraction**: Identifies technical and soft skills
2. **Named Entity Recognition**: Extracts organizations, locations, etc.
3. **Key Phrase Extraction**: Identifies important phrases
4. **Attribute Detection**: Determines experience level, education, etc.

Key classes:
- `NlpAnalysisService`: Main service for NLP analysis
- `JobDescriptionAnalysisService`: Service for analyzing job descriptions
- `NlpAnalysisResult`: Model for storing analysis results

### Word Embedding Service (Implemented)

The Word Embedding Service provides semantic similarity capabilities:

1. **Similarity Calculation**: Computes similarity between words and phrases
2. **Synonym Recognition**: Identifies synonyms and related terms
3. **Skill Matching**: Specialized matching for skills and requirements

Key classes:
- `WordEmbeddingService`: Interface for word embedding operations
- `SimpleWordEmbeddingServiceImpl`: Implementation using string similarity and synonyms

### Optimization Matrix (Implemented)

The Optimization Matrix matches resumes with job descriptions and generates suggestions:

1. **Skill Matching**: Matches resume skills with job requirements using semantic similarity
2. **Score Calculation**: Computes match scores for skills, experience, and education
3. **Gap Analysis**: Identifies missing skills and experience
4. **Suggestion Generation**: Creates actionable suggestions for improvement

Key classes:
- `MatchingService`: Interface for matching operations
- `MatchingServiceImpl`: Implementation of matching algorithms
- `MatchResult`: Model for storing match results
- `OptimizedResume`: Model for storing optimization results

## API Design

The system exposes RESTful APIs for various operations:

### Resume API
- `POST /api/v1/resume/upload`: Upload and process a resume
- `GET /api/v1/resume/{id}`: Get resume details
- `DELETE /api/v1/resume/{id}`: Delete a resume

### Job Description API
- `POST /api/v1/jobs`: Submit a job description
- `GET /api/v1/jobs/{id}`: Get job description details
- `DELETE /api/v1/jobs/{id}`: Delete a job description

### Matching API
- `POST /api/v1/match`: Match a resume with a job description
- `GET /api/v1/match/{id}`: Get match results
- `GET /api/v1/optimize/{id}`: Get optimized resume

## Security Architecture

The system implements a layered security approach:

1. **Authentication**: Spring Security with form-based authentication
2. **Authorization**: Role-based access control
3. **Data Protection**: Encryption of sensitive data
4. **API Security**: JWT for API authentication (planned)

## Deployment Architecture

The system is designed for cloud deployment:

1. **Development**: Local environment with H2 database
2. **Testing**: Containerized environment with PostgreSQL
3. **Production**: Kubernetes cluster with high availability (planned)

## Future Enhancements

1. **Advanced NLP**: Integration with more sophisticated NLP models
2. **Machine Learning**: Implementation of ML for career prediction
3. **Real-time Analysis**: Stream processing for real-time resume analysis
4. **Mobile Support**: Native mobile applications
5. **Integration**: APIs for third-party integration

---

*This architecture document will continue to evolve as the project progresses, with more detailed component designs and implementation specifics added over time.* 