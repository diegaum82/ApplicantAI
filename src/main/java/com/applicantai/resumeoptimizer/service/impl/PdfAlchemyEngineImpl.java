package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PDF Alchemy Engine - The core service for extracting and processing text from PDF resumes.
 * This implementation uses a direct PDF text extraction method with fallback to OCR for image-based PDFs.
 */
@Service
public class PdfAlchemyEngineImpl implements ResumeProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(PdfAlchemyEngineImpl.class);
    private static final int MIN_TEXT_LENGTH = 100; // Minimum text length to consider a successful extraction
    
    private static final Pattern SECTION_HEADER_PATTERN = Pattern.compile("^\\s*(?:[A-Z][A-Z\\s]+|[A-Z][a-z]+(?:\\s+[A-Z][a-z]+)*)\\s*$", Pattern.MULTILINE);
    private static final Pattern BULLET_POINT_PATTERN = Pattern.compile("^\\s*[•●■◆▪▫⦿⦾⦿◉○◎◦-]\\s+(.+)$", Pattern.MULTILINE);
    
    @Value("${applicantai.temp.directory:/tmp/applicantai}")
    private String tempDirectory;
    
    @Value("${applicantai.tesseract.path:}")
    private String tesseractPath;
    
    @Value("${applicantai.enable.ocr:true}")
    private boolean enableOcr;

    /**
     * Processes a resume file and extracts its content.
     * Uses a direct PDF text extraction first, and falls back to OCR if needed.
     * 
     * @param file the resume file to process
     * @return ResumeContent object with the extracted information
     * @throws IOException if there is an error processing the file
     */
    @Override
    @Cacheable(value = "resumeCache", key = "#file.originalFilename + '-' + #file.size")
    public ResumeContent processResume(MultipartFile file) throws IOException {
        logger.info("Processing resume: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        // Create temp directory if it doesn't exist
        File tempDir = new File(tempDirectory);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        // Save the uploaded file to a temporary location
        Path tempFile = Files.createTempFile(tempDir.toPath(), "resume-", ".pdf");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
        
        ResumeContent resumeContent = new ResumeContent();
        resumeContent.setOriginalFileName(file.getOriginalFilename());
        resumeContent.setFileSize(file.getSize());
        
        List<String> warnings = new ArrayList<>();
        String extractionMethod = "DIRECT";
        int extractionQuality = 100;
        
        try {
            // Try direct PDF text extraction first
            String extractedText = extractTextFromPdf(tempFile.toFile());
            
            // Check if we got enough text back
            if (extractedText.length() < MIN_TEXT_LENGTH) {
                logger.warn("Direct text extraction produced insufficient text ({}). Attempting OCR fallback.", 
                        extractedText.length());
                warnings.add("Direct text extraction produced limited results.");
                
                if (enableOcr) {
                    // Use OCR as fallback
                    extractedText = extractTextWithOcr(tempFile.toFile());
                    extractionMethod = "OCR";
                    extractionQuality = 80; // OCR is assumed to be less accurate
                    
                    if (extractedText.length() < MIN_TEXT_LENGTH) {
                        logger.error("OCR extraction also failed to produce sufficient text.");
                        warnings.add("OCR extraction also produced limited results. The PDF may be protected or damaged.");
                        extractionQuality = 40;
                    }
                } else {
                    logger.warn("OCR is disabled. Unable to extract text from image-based PDF.");
                    warnings.add("OCR processing is disabled. Unable to extract text from image-based PDF.");
                    extractionQuality = 30;
                }
            }
            
            // Set the raw text
            resumeContent.setRawText(extractedText);
            
            // Extract sections and bullet points
            Map<String, String> sections = extractSections(extractedText);
            resumeContent.setSections(sections);
            
            List<String> bulletPoints = extractBulletPoints(extractedText);
            resumeContent.setBulletPoints(bulletPoints);
            
            // Set metadata
            resumeContent.setMetadata(new HashMap<>());
            resumeContent.getMetadata().put("extractionMethod", extractionMethod);
            resumeContent.getMetadata().put("extractionQuality", String.valueOf(extractionQuality));
            resumeContent.getMetadata().put("warnings", String.join(", ", warnings));
            
            return resumeContent;
            
        } catch (Exception e) {
            logger.error("Error processing resume", e);
            throw new IOException("Failed to process resume: " + e.getMessage(), e);
        } finally {
            // Clean up the temporary file
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException e) {
                logger.warn("Failed to delete temporary file: {}", tempFile, e);
            }
        }
    }
    
    /**
     * Extracts text directly from a PDF file using PDFBox.
     * 
     * @param pdfFile the PDF file
     * @return the extracted text
     * @throws IOException if there is an error extracting text
     */
    private String extractTextFromPdf(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
    }
    
    /**
     * Extracts text from a PDF file using OCR (Tesseract).
     * This is a fallback method for image-based PDFs.
     * 
     * @param pdfFile the PDF file
     * @return the extracted text
     * @throws IOException if there is an error during OCR processing
     */
    private String extractTextWithOcr(File pdfFile) throws IOException {
        // For now, this is a simplified OCR implementation
        // In a production environment, you would convert PDF pages to images and use Tesseract
        
        // For demonstration purposes, we're just showing the potential implementation
        if (!StringUtils.hasText(tesseractPath)) {
            throw new IOException("Tesseract path not configured for OCR processing");
        }
        
        // Here we would:
        // 1. Convert PDF pages to images (using PDFBox or other libraries)
        // 2. Process each image with Tesseract
        // 3. Combine the results
        
        // For now, just return a placeholder
        logger.info("OCR processing would be performed here using Tesseract at: {}", tesseractPath);
        return "OCR PROCESSING RESULT WOULD APPEAR HERE. THIS IS A PLACEHOLDER.";
    }
    
    /**
     * Extracts sections from the resume text based on pattern recognition.
     * 
     * @param text the raw resume text
     * @return a map of section headers to their content
     */
    private Map<String, String> extractSections(String text) {
        Map<String, String> sections = new LinkedHashMap<>();
        
        Matcher matcher = SECTION_HEADER_PATTERN.matcher(text);
        List<Integer> headerPositions = new ArrayList<>();
        List<String> headers = new ArrayList<>();
        
        // Find all potential section headers
        while (matcher.find()) {
            String header = matcher.group().trim();
            // Filter out false positives (too short, common text)
            if (header.length() > 3 && !isCommonText(header)) {
                headers.add(header);
                headerPositions.add(matcher.start());
            }
        }
        
        // Extract content between headers
        for (int i = 0; i < headers.size(); i++) {
            int startPos = headerPositions.get(i) + headers.get(i).length();
            int endPos = (i < headers.size() - 1) ? headerPositions.get(i + 1) : text.length();
            
            String content = text.substring(startPos, endPos).trim();
            sections.put(headers.get(i), content);
        }
        
        return sections;
    }
    
    /**
     * Extracts bullet points from the resume text.
     * 
     * @param text the raw resume text
     * @return a list of bullet points
     */
    private List<String> extractBulletPoints(String text) {
        List<String> bulletPoints = new ArrayList<>();
        Matcher matcher = BULLET_POINT_PATTERN.matcher(text);
        
        while (matcher.find()) {
            String bulletPoint = matcher.group(1).trim();
            if (bulletPoint.length() > 5) { // Ignore very short bullet points
                bulletPoints.add(bulletPoint);
            }
        }
        
        return bulletPoints;
    }
    
    /**
     * Checks if the text is common enough to not be a section header.
     * 
     * @param text the text to check
     * @return true if it's common text, false otherwise
     */
    private boolean isCommonText(String text) {
        String[] commonWords = {"THE", "AND", "FOR", "WITH", "FROM", "YOUR", "THAT", "THIS", "HAVE", "NOT"};
        for (String word : commonWords) {
            if (text.equals(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Skill> identifySkills(ResumeContent content) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'identifySkills'");
    }

    @Override
    public ResumeContent detectSections(String content) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'detectSections'");
    }

    @Override
    public int evaluateExtractionQuality(ResumeContent content) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'evaluateExtractionQuality'");
    }
} 