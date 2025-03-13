# Contributing to ApplicantAI Resume Optimizer

Thank you for your interest in contributing to the ApplicantAI Resume Optimizer! This document provides guidelines and instructions for contributing to this project.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before contributing.

## How Can I Contribute?

### Reporting Bugs

This section guides you through submitting a bug report. Following these guidelines helps maintainers understand your report, reproduce the issue, and find related reports.

* **Use the GitHub issue search** — check if the issue has already been reported.
* **Check if the issue has been fixed** — try to reproduce it using the latest `main` or `develop` branch.
* **Isolate the problem** — create a reduced test case and a live example.
* **Use the bug report template** — when you create a new issue, you will see a template that guides you through collecting and providing the required information.

### Suggesting Enhancements

This section guides you through submitting an enhancement suggestion, including completely new features and minor improvements to existing functionality.

* **Use the GitHub issue search** — check if the enhancement has already been suggested.
* **Determine which repository the enhancement should be suggested in**.
* **Use the feature request template** — when you create a new issue, you will see a template that guides you through collecting and providing the required information.

### Pull Requests

* Fill in the required template
* Do not include issue numbers in the PR title
* Include screenshots and animated GIFs in your pull request whenever possible
* Follow the Java style guide
* Include adequate tests
* Document new code based on the Documentation Styleguide
* End all files with a newline

## Development Process

### Branching Strategy

We follow the GitFlow branching model:

* `main` - production-ready code
* `develop` - latest delivered development changes
* `feature/*` - feature branches
* `release/*` - release branches
* `hotfix/*` - hotfix branches

### Setting Up Development Environment

1. Fork the repository
2. Clone your fork
   ```bash
   git clone https://github.com/your-username/applicantai-resume-optimizer.git
   cd applicantai-resume-optimizer
   ```
3. Add the original repository as a remote
   ```bash
   git remote add upstream https://github.com/original-owner/applicantai-resume-optimizer.git
   ```
4. Create a new branch from `develop`
   ```bash
   git checkout develop
   git pull upstream develop
   git checkout -b feature/your-feature-name
   ```

### Coding Standards

* Follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* Write clear, readable, and maintainable code
* Include JavaDoc comments for all public methods and classes
* Keep methods small and focused on a single responsibility
* Use meaningful variable and method names

### Testing

* Write unit tests for all new code
* Ensure all tests pass before submitting a pull request
* Aim for at least 80% code coverage for new code
* Include integration tests for new features

### Documentation

* Update the README.md with details of changes to the interface
* Update the JavaDoc comments
* Update the API documentation if applicable
* Add or update examples if applicable

## Submitting Changes

1. Push your changes to your fork
   ```bash
   git push origin feature/your-feature-name
   ```
2. Submit a pull request to the `develop` branch of the original repository
3. The core team will review your pull request and provide feedback
4. Once approved, your changes will be merged

## Additional Resources

* [General GitHub documentation](https://help.github.com/)
* [GitHub pull request documentation](https://help.github.com/articles/about-pull-requests/)
* [Java Style Guide](https://google.github.io/styleguide/javaguide.html)
* [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)

## Thank You!

Your contributions to open source, large or small, make projects like this possible. Thank you for taking the time to contribute. 