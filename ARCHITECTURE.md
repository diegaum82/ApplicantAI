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

### Matching & Optimization Service
- Compares resumes against job descriptions
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

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17
- **Build Tool**: Maven
- **API Documentation**: SpringDoc OpenAPI

### Data Processing
- **PDF Processing**: Apache PDFBox
- **OCR**: Tesseract
- **NLP**: OpenNLP, DeepLearning4J
- **Text Analysis**: SpaCy, BERT models

### Data Storage
- **Primary Database**: PostgreSQL
- **Caching**: Redis
- **Document Storage**: MinIO (S3-compatible)

### Frontend
- **Web Framework**: React
- **UI Components**: Material-UI
- **State Management**: Redux
- **Visualization**: D3.js

### Infrastructure
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **CI/CD**: GitHub Actions
- **Monitoring**: Prometheus & Grafana

## Security Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Security Architecture                        │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐   │
│  │                │   │                │   │                │   │
│  │ Authentication │   │ Authorization  │   │ Data Protection│   │
│  │ - JWT Tokens   │   │ - Role-Based   │   │ - Encryption   │   │
│  │ - OAuth 2.0    │   │   Access       │   │ - PII Handling │   │
│  │ - MFA Support  │   │ - API Security │   │ - GDPR Controls│   │
│  │                │   │                │   │                │   │
│  └────────────────┘   └────────────────┘   └────────────────┘   │
│                                                                 │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐   │
│  │                │   │                │   │                │   │
│  │ Input Validation│  │ Audit Logging  │   │ Rate Limiting  │   │
│  │ - Sanitization │   │ - User Actions │   │ - API Throttling│  │
│  │ - Validation   │   │ - System Events│   │ - DoS Protection│  │
│  │ - XSS Prevention│  │ - Alerts       │   │                │   │
│  │                │   │                │   │                │   │
│  └────────────────┘   └────────────────┘   └────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

### Key Security Features
- JWT-based authentication with secure token handling
- Role-based access control for all API endpoints
- Data encryption at rest and in transit
- PII detection and automatic redaction
- Comprehensive audit logging
- Rate limiting and API protection

## Scalability Architecture

The system is designed to scale horizontally with increasing load:

- **Stateless Services**: All services are designed to be stateless for easy scaling
- **Database Sharding**: Data partitioning for high-volume data
- **Caching Strategy**: Multi-level caching to reduce database load
- **Asynchronous Processing**: Long-running tasks handled via message queues
- **Microservices Isolation**: Services can scale independently based on demand

## Monitoring and Observability

```
┌─────────────────────────────────────────────────────────────────┐
│                  Monitoring & Observability                      │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐   │
│  │                │   │                │   │                │   │
│  │  Metrics       │   │  Logging       │   │  Tracing       │   │
│  │  - Prometheus  │   │  - ELK Stack   │   │  - Jaeger      │   │
│  │  - Grafana     │   │  - Log         │   │  - Distributed │   │
│  │  - Alerts      │   │    Aggregation │   │    Request     │   │
│  │                │   │                │   │    Tracking    │   │
│  └────────────────┘   └────────────────┘   └────────────────┘   │
│                                                                 │
│  ┌────────────────┐   ┌────────────────┐   ┌────────────────┐   │
│  │                │   │                │   │                │   │
│  │  Health Checks │   │  Error Tracking│   │  Performance   │   │
│  │  - Service     │   │  - Centralized │   │  Analysis      │   │
│  │    Status      │   │    Error       │   │  - Hotspot     │   │
│  │  - Dependency  │   │    Reporting   │   │    Detection   │   │
│  │    Monitoring  │   │  - Alerts      │   │  - Optimization│   │
│  └────────────────┘   └────────────────┘   └────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Deployment Architecture

The system will be deployed as a series of containerized services in a Kubernetes cluster:

```
┌──────────────────────────────────────────────────────┐
│                 Kubernetes Cluster                    │
├──────────────────────────────────────────────────────┤
│                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │ Frontend    │  │ API Gateway │  │ Backend     │   │
│  │ Pods        │  │ Pods        │  │ Service Pods│   │
│  └─────────────┘  └─────────────┘  └─────────────┘   │
│                                                      │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐   │
│  │ Database    │  │ Caching     │  │ Message     │   │
│  │ Instances   │  │ Layer       │  │ Queue       │   │
│  └─────────────┘  └─────────────┘  └─────────────┘   │
│                                                      │
└──────────────────────────────────────────────────────┘
```

## Implementation Status

### Implemented Components

#### PDF Alchemy Engine (Resume Processing Service)

The PDF Alchemy Engine has been implemented with the following features:

1. **Multiple Text Extraction Strategies**:
   - Direct text extraction using Apache PDFBox
   - OCR fallback using Tesseract for image-based PDFs
   - Quality evaluation metrics

2. **Content Structure Analysis**:
   - Section detection (Education, Experience, Skills, etc.)
   - Bullet point extraction
   - Raw text preservation

3. **Performance Optimization**:
   - Caffeine caching for repeated processing of the same file
   - Configurable OCR settings

4. **Error Handling**:
   - Graceful degradation between extraction methods
   - Detailed warnings and quality metrics

The implementation includes two service classes:
- `PdfProcessingService` (primary implementation)
- `PdfAlchemyEngineImpl` (alternative implementation)

Both implement the `ResumeProcessingService` interface, with `PdfProcessingService` marked as `@Primary` for Spring's dependency injection.

#### Resume Upload Controller

The Resume Upload Controller provides both web and API interfaces for resume uploads:

1. **Web Interface**:
   - GET `/resume/upload` - Displays the upload form
   - POST `/resume/upload` - Processes the uploaded file and displays results

2. **API Interface**:
   - POST `/api/v1/resume/upload` - REST endpoint for programmatic uploads

3. **Features**:
   - File validation
   - Error handling
   - Integration with PDF Alchemy Engine

#### Caching Mechanism

The system implements caching using Spring Boot's cache abstraction with Caffeine as the provider:

1. **Cache Configuration**:
   - Cache name: `resumeCache`
   - Cache key: Combination of filename and file size
   - Implementation: Caffeine in-memory cache

2. **Cached Operations**:
   - Resume processing (most computationally expensive operation)

3. **Benefits**:
   - Reduced processing time for duplicate uploads
   - Lower resource utilization
   - Improved user experience

#### NLP Cortex (NLP Analysis Module)

The NLP Cortex has been implemented with the following features:

1. **Text Analysis**:
   - Pattern-based skill extraction
   - Key phrase identification
   - Named entity recognition
   - Attribute extraction

2. **Dictionary Support**:
   - Comprehensive skill dictionary with categories and aliases
   - Industry and job level dictionaries
   - Configurable matching thresholds

3. **Service Components**:
   - `NlpAnalysisService` - Core NLP functionality
   - `JobDescriptionAnalysisService` - Job description analysis
   - `EnhancedResumeProcessingService` - NLP-enhanced resume processing

4. **Configuration**:
   - Configurable analysis depth
   - Adjustable confidence thresholds
   - Dictionary paths

#### Optimization Matrix (Matching Service)

The Optimization Matrix has been implemented with the following features:

1. **Matching Algorithms**:
   - Skill-based matching
   - Key phrase matching
   - Attribute comparison

2. **Optimization Suggestions**:
   - Missing skill identification
   - Key phrase recommendations
   - General resume improvement suggestions

3. **Service Components**:
   - `MatchingServiceImpl` - Core matching functionality
   - Support for both entity-based and content-based matching

4. **Scoring System**:
   - Overall match score calculation
   - Component-specific scores (skills, experience, etc.)
   - Weighted scoring based on importance

---

This architecture document provides a high-level overview of the ApplicantAI Resume Optimizer system. It will evolve as the project progresses, with more detailed component designs and implementation specifics added over time. 