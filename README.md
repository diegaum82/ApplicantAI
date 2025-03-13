# ApplicantAI Resume Optimizer

![ApplicantAI Logo](docs/images/applicantai-logo.png)

## Overview

ApplicantAI Resume Optimizer is an advanced application that helps job seekers optimize their resumes for specific job descriptions using Natural Language Processing (NLP) and AI techniques. The system analyzes resumes, extracts key information, and provides tailored suggestions to improve match rates with Applicant Tracking Systems (ATS) and human recruiters.

## Features

- **PDF Resume Processing**: Extract text and structure from PDF resumes
- **Skill Identification**: Automatically identify technical and soft skills from resume content
- **Job Description Analysis**: Extract key requirements and preferences from job descriptions
- **Resume-Job Matching**: Calculate match scores between resumes and job descriptions
- **Optimization Suggestions**: Provide actionable recommendations to improve resume effectiveness
- **ATS Compatibility Check**: Ensure resumes are compatible with Applicant Tracking Systems
- **Industry-Specific Analysis**: Tailor analysis based on industry standards and expectations

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.x
- **PDF Processing**: Apache PDFBox, PDF Alchemy Engine
- **NLP & AI**: Custom NLP Cortex, OpenNLP, Stanford NLP
- **Database**: H2 (Development), PostgreSQL (Production)
- **Caching**: Caffeine
- **Frontend**: Thymeleaf, Bootstrap, JavaScript (React planned for future)
- **Build Tool**: Maven

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/com/applicantai/resumeoptimizer/
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── model/            # Domain models
│   │   │   ├── repository/       # Data access layer
│   │   │   ├── service/          # Business logic
│   │   │   │   ├── impl/         # Service implementations
│   │   │   ├── config/           # Configuration classes
│   │   │   ├── util/             # Utility classes
│   │   │   └── ResumeOptimizerApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── templates/        # Thymeleaf templates
│   │   │   ├── static/           # Static resources
│   │   │   └── nlp/              # NLP resources and dictionaries
│   └── test/                     # Test classes
├── docs/                         # Documentation
├── pom.xml                       # Maven configuration
└── README.md                     # This file
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher
- Git

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-organization/applicantai-resume-optimizer.git
   cd applicantai-resume-optimizer
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the application:
   ```
   http://localhost:8080
   ```

### Configuration

The application can be configured through the `application.properties` file:

```properties
# Server configuration
server.port=8080

# Database configuration
spring.datasource.url=jdbc:h2:mem:applicantaidb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=password
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true

# NLP configuration
nlp.dictionaries.path=classpath:nlp/dictionaries
nlp.models.path=classpath:nlp/models
nlp.skill-extraction.confidence-threshold=0.7
```

## API Documentation

The API documentation is available at:
```
http://localhost:8080/swagger-ui.html
```

For detailed API design, see [API_DESIGN.md](API_DESIGN.md).

## Architecture

The system follows a microservices-based architecture with the following key components:

1. **PDF Alchemy Engine**: Handles PDF processing and text extraction
2. **NLP Cortex**: Processes and analyzes text using NLP techniques
3. **Optimization Matrix**: Matches resumes with job descriptions and generates suggestions
4. **User Interface**: Provides web interface for uploading and viewing results

For detailed architecture information, see [ARCHITECTURE.md](ARCHITECTURE.md).

## Development

### Development Workflow

1. Create a feature branch from `develop`
2. Implement your changes
3. Write tests
4. Submit a pull request to `develop`

### Coding Standards

- Follow Google Java Style Guide
- Write unit tests for all new features
- Document public APIs

## Contributing

We welcome contributions! Please see [CONTRIBUTING.md](CONTRIBUTING.md) for details.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Apache PDFBox team for the PDF processing library
- Spring Boot team for the excellent framework
- OpenNLP and Stanford NLP for NLP tools and models

---

© 2023 ApplicantAI. All rights reserved.