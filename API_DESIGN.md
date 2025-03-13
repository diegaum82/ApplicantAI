# ApplicantAI Resume Optimizer - API Design

This document outlines the API design for the ApplicantAI Resume Optimizer system. The API provides a comprehensive interface for resume analysis, job matching, and optimization.

## API Overview

The API follows REST principles and is versioned to allow for future changes. All endpoints return JSON responses unless otherwise specified.

### Base URL
```
/api/v1
```

### Authentication
All API endpoints require authentication. The API uses JWT-based authentication with tokens provided in the Authorization header:

```
Authorization: Bearer <token>
```

## Endpoints

### Resume Processing (Implemented)

#### Upload Resume
```
POST /resume/upload
```
Request:
- Content-Type: multipart/form-data
- Body: 
  - file: PDF file (required)

Response:
```json
{
  "success": true,
  "resumeId": "550e8400-e29b-41d4-a716-446655440000",
  "fileName": "resume.pdf",
  "fileSize": 125678,
  "extractedContent": {
    "rawText": "Text content of the resume...",
    "sections": {
      "experience": "Work experience content...",
      "education": "Education content...",
      "skills": "Skills content..."
    },
    "bulletPoints": [
      "Accomplished X resulting in Y",
      "Developed Z system that improved efficiency by 30%"
    ],
    "metadata": {
      "extractionMethod": "DIRECT",
      "extractionQuality": "85",
      "warnings": "Low quality text extraction detected"
    }
  }
}
```

Error Response:
```json
{
  "success": false,
  "error": "Failed to process file",
  "message": "The file is not a valid PDF",
  "timestamp": "2023-03-15T10:30:15Z"
}
```

#### Analyze Resume (Implemented)
```
POST /api/v1/resume/analyze
```
Request:
- Content-Type: multipart/form-data
- Body: 
  - file: PDF file (required)
  - analyzeSkills: boolean (optional, default: true)
  - extractSections: boolean (optional, default: true)

Response:
```json
{
  "success": true,
  "resumeId": "550e8400-e29b-41d4-a716-446655440000",
  "fileName": "resume.pdf",
  "fileSize": 125678,
  "extractionQuality": 85,
  "skills": [
    { "name": "Java", "category": "Programming Language", "relevanceScore": 90 },
    { "name": "Spring Boot", "category": "Framework", "relevanceScore": 85 },
    { "name": "AWS", "category": "Cloud", "relevanceScore": 75 }
  ],
  "sections": {
    "experience": "Work experience content...",
    "education": "Education content...",
    "skills": "Skills content..."
  },
  "keyPhrases": [
    { "phrase": "software development", "score": 0.92 },
    { "phrase": "cloud infrastructure", "score": 0.85 },
    { "phrase": "team leadership", "score": 0.78 }
  ],
  "namedEntities": [
    "Google",
    "Amazon",
    "San Francisco",
    "Stanford University"
  ],
  "attributes": {
    "experience_level": "senior",
    "education_level": "Bachelor",
    "industry": "technology"
  }
}
```

### Job Description Analysis (Implemented)

#### Analyze Job Description
```
POST /api/v1/job/analyze
```
Request:
```json
{
  "title": "Senior Software Engineer",
  "company": "Tech Innovations Inc.",
  "content": "We are looking for a Senior Software Engineer with 5+ years of experience in Java development. The ideal candidate will have experience with Spring Boot, AWS, and microservices architecture..."
}
```
Response:
```json
{
  "success": true,
  "jobId": "job123",
  "requiredSkills": [
    { "name": "Java", "category": "Programming Language", "importanceScore": 95 },
    { "name": "Spring Boot", "category": "Framework", "importanceScore": 90 },
    { "name": "AWS", "category": "Cloud", "importanceScore": 85 }
  ],
  "keyPhrases": [
    { "phrase": "microservices architecture", "score": 0.92 },
    { "phrase": "software engineer", "score": 0.88 },
    { "phrase": "java development", "score": 0.85 }
  ],
  "seniorityLevel": "Senior",
  "industry": "Technology",
  "attributes": {
    "experience_level": "5+ years",
    "education_level": "Bachelor",
    "employment_type": "Full-Time"
  }
}
```

### Matching and Optimization (Implemented)

#### Match Resume with Job Description
```
POST /api/v1/match
```
Request:
```json
{
  "resumeContent": "Full text of the resume...",
  "jobDescription": "Full text of the job description..."
}
```
Response:
```json
{
  "success": true,
  "matchScore": 78,
  "matchingSkills": {
    "Java": 95,
    "Spring Boot": 90,
    "REST API": 85
  },
  "missingSkills": {
    "Kubernetes": 90,
    "GraphQL": 75
  },
  "suggestions": [
    "Add Kubernetes to your skills section",
    "Highlight your experience with microservices architecture",
    "Consider adding the phrase 'cloud infrastructure' to your resume",
    "Quantify your achievements with specific metrics where possible"
  ]
}
```

### User Management

#### Register User
```
POST /users/register
```
Request:
```json
{
  "email": "user@example.com",
  "password": "securePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```
Response:
```json
{
  "id": "12345",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "createdAt": "2023-03-15T10:30:15Z"
}
```

#### Login
```
POST /users/login
```
Request:
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```
Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresAt": "2023-03-15T12:30:15Z",
  "user": {
    "id": "12345",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }
}
```

### Resume Management

#### Get Resume
```
GET /resumes/{resumeId}
```
Response:
```json
{
  "id": "resume123",
  "title": "Software Engineer Resume",
  "content": "... extracted text content ...",
  "contentType": "application/pdf",
  "fileSize": 125042,
  "uploadedAt": "2023-03-15T10:35:22Z",
  "skills": [
    { "id": "skill1", "name": "Java", "category": "Programming", "relevanceScore": 95 },
    { "id": "skill2", "name": "Spring Boot", "category": "Framework", "relevanceScore": 90 }
  ],
  "atsScore": 82,
  "lpsScore": 78,
  "feoScore": 85,
  "status": "PROCESSED"
}
```

#### List Resumes
```
GET /resumes
```
Response:
```json
{
  "items": [
    {
      "id": "resume123",
      "title": "Software Engineer Resume",
      "uploadedAt": "2023-03-15T10:35:22Z",
      "status": "PROCESSED"
    },
    {
      "id": "resume124",
      "title": "Data Scientist Resume",
      "uploadedAt": "2023-03-14T14:22:10Z",
      "status": "PROCESSED"
    }
  ],
  "totalItems": 2,
  "page": 1,
  "pageSize": 10,
  "totalPages": 1
}
```

### Job Description Management

#### Submit Job Description
```
POST /jobs
```
Request:
```json
{
  "title": "Senior Software Engineer",
  "company": "Tech Innovations Inc.",
  "location": "San Francisco, CA",
  "content": "... job description content ...",
  "employmentType": "FULL_TIME",
  "experienceLevel": "SENIOR"
}
```
Response:
```json
{
  "id": "job456",
  "title": "Senior Software Engineer",
  "company": "Tech Innovations Inc.",
  "location": "San Francisco, CA",
  "createdAt": "2023-03-15T11:00:15Z",
  "requiredSkills": [
    { "id": "skill1", "name": "Java", "category": "Programming" },
    { "id": "skill3", "name": "Microservices", "category": "Architecture" }
  ],
  "status": "PROCESSED"
}
```

#### Get Job Description
```
GET /jobs/{jobId}
```
Response:
```json
{
  "id": "job456",
  "title": "Senior Software Engineer",
  "company": "Tech Innovations Inc.",
  "location": "San Francisco, CA",
  "content": "... job description content ...",
  "employmentType": "FULL_TIME",
  "experienceLevel": "SENIOR",
  "createdAt": "2023-03-15T11:00:15Z",
  "requiredSkills": [
    { "id": "skill1", "name": "Java", "category": "Programming" },
    { "id": "skill3", "name": "Microservices", "category": "Architecture" }
  ],
  "keyPhrases": [
    { "phrase": "cloud infrastructure", "importance": 0.85 },
    { "phrase": "agile development", "importance": 0.75 }
  ],
  "status": "PROCESSED"
}
```

## Error Handling

All endpoints use standard HTTP status codes:
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

Error responses follow this format:
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The request contains invalid parameters",
    "details": [
      { "field": "email", "message": "Must be a valid email address" }
    ]
  },
  "requestId": "req-123456",
  "timestamp": "2023-03-15T11:30:22Z"
}
```

## Rate Limiting

API requests are rate-limited to protect the service. Limits are specified in the response headers:
```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 99
X-RateLimit-Reset: 1678883422
```

## Versioning

API changes will be managed through versioning. The current version is v1, specified in the base URL.

---

*Note: This API design is subject to revision as the project evolves. Always refer to the latest documentation for current endpoints and functionality.* 