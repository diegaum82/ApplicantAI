package com.applicantai.resumeoptimizer.service.impl;

import com.applicantai.resumeoptimizer.model.ResumeContent;
import com.applicantai.resumeoptimizer.model.Skill;
import com.applicantai.resumeoptimizer.service.ResumeProcessingService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

/**
 * The PDF Alchemy Engine that implements the ResumeProcessingService.
 * This service extracts content from PDF files, with fallback to OCR for image-based PDFs.
 */
@Service
@Primary
public class PdfProcessingService implements ResumeProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(PdfProcessingService.class);
    private static final int MIN_TEXT_LENGTH = 100; // Minimum characters to consider direct extraction successful
    private static final String[] SECTION_HEADERS = {"experience", "education", "skills", "certifications", 
                                                    "projects", "publications", "awards", "achievements",
                                                    "summary", "objective", "references", "profile"};
    private static final Pattern BULLET_POINT_PATTERN = Pattern.compile("(?m)^\\s*[•·\\-*]\\s+(.+)$");
    
    @Value("${app.resume.storage.location:./resume-uploads/}")
    private String storageLocation;
    
    @Value("${app.ocr.use-fallback:true}")
    private boolean useOcrFallback;
    
    @Value("${app.ocr.tesseract-data-path:/usr/share/tesseract-ocr/4.0/tessdata/}")
    private String tesseractDataPath;
    
    @Value("${app.ocr.language:eng}")
    private String ocrLanguage;
    
    private final ITesseract tesseract;
    
    /**
     * Constructor initializes the Tesseract OCR engine with configuration from properties
     */
    public PdfProcessingService(
            @Value("${app.ocr.tesseract-data-path:/usr/share/tesseract-ocr/4.0/tessdata/}") String tesseractDataPath,
            @Value("${app.ocr.language:eng}") String ocrLanguage) {
        // Initialize Tesseract instance with configuration from properties
        tesseract = new Tesseract();
        tesseract.setDatapath(tesseractDataPath);
        tesseract.setLanguage(ocrLanguage);
        
        logger.info("Initialized Tesseract OCR with data path: {} and language: {}", 
                tesseractDataPath, ocrLanguage);
    }
    
    /**
     * Processes a resume file and extracts content from it.
     *
     * @param file the resume file to process
     * @return ResumeContent containing the extracted data
     * @throws IOException if there is an error processing the file
     */
    @Override
    @Cacheable(value = "resumeCache", key = "#file.originalFilename + '-' + #file.size")
    public ResumeContent processResume(MultipartFile file) throws IOException {
        logger.info("Processing resume file: {}", file.getOriginalFilename());
        
        if (file.isEmpty()) {
            throw new IOException("Failed to process empty file");
        }
        
        // Verify it's a PDF
        String contentType = file.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IOException("File must be a PDF");
        }
        
        // Save the file temporarily
        Path tempFile = saveFileTemporarily(file);
        
        try {
            // Create ResumeContent object
            ResumeContent resumeContent = new ResumeContent();
            resumeContent.setOriginalFileName(file.getOriginalFilename());
            resumeContent.setFileSize(file.getSize());
            
            // Extract text using PDFBox
            String directExtractedText = extractTextWithPdfBox(tempFile.toFile());
            
            // Create metadata map to store extraction info
            Map<String, String> metadata = new HashMap<>();
            List<String> warnings = new ArrayList<>();
            
            // Check if direct extraction yielded sufficient text
            if (directExtractedText.length() > MIN_TEXT_LENGTH) {
                logger.info("Direct PDF text extraction successful");
                resumeContent.setRawText(directExtractedText);
                metadata.put("extractionMethod", "DIRECT");
                metadata.put("extractionQuality", "100");
            } 
            // If direct extraction is insufficient and OCR fallback is enabled
            else if (useOcrFallback) {
                logger.info("Direct extraction insufficient, attempting OCR fallback");
                String ocrText = extractTextWithOcr(tempFile.toFile());
                
                // If OCR also failed to extract sufficient text, use what we have from direct extraction
                if (ocrText.length() <= MIN_TEXT_LENGTH) {
                    logger.warn("Both direct extraction and OCR yielded insufficient text");
                    resumeContent.setRawText(directExtractedText.length() > ocrText.length() ? 
                            directExtractedText : ocrText);
                    metadata.put("extractionMethod", "DIRECT");
                    warnings.add("Low text extraction quality");
                } else {
                    // Use OCR text since it's better
                    resumeContent.setRawText(ocrText);
                    metadata.put("extractionMethod", "OCR");
                }
            } else {
                // OCR fallback is disabled, use direct extraction even if poor
                logger.warn("Direct extraction yielded minimal text and OCR fallback is disabled");
                resumeContent.setRawText(directExtractedText);
                metadata.put("extractionMethod", "DIRECT");
                warnings.add("Low text extraction quality, OCR fallback disabled");
            }
            
            // Extract sections and bullet points
            Map<String, String> sections = extractSections(resumeContent.getRawText());
            resumeContent.setSections(sections);
            
            List<String> bulletPoints = extractBulletPoints(resumeContent.getRawText());
            resumeContent.setBulletPoints(bulletPoints);
            
            // Evaluate extraction quality
            int quality = evaluateExtractionQuality(resumeContent);
            metadata.put("extractionQuality", String.valueOf(quality));
            
            if (quality < 50) {
                warnings.add("Low quality text extraction detected");
            }
            
            // Store warnings in metadata
            metadata.put("warnings", String.join("|", warnings));
            resumeContent.setMetadata(metadata);
            
            logger.info("Resume processing complete. Quality score: {}", quality);
            return resumeContent;
            
        } finally {
            // Clean up temp file
            try {
                Files.deleteIfExists(tempFile);
                logger.debug("Temporary file deleted: {}", tempFile);
            } catch (IOException e) {
                logger.warn("Failed to delete temporary file: {}", tempFile, e);
            }
        }
    }

    /**
     * Extracts sections from the resume text.
     * 
     * @param text the raw resume text
     * @return a map of section headers to their content
     */
    private Map<String, String> extractSections(String text) {
        Map<String, String> sections = new HashMap<>();
        String normalizedContent = text.toLowerCase();
        
        for (String sectionHeader : SECTION_HEADERS) {
            int sectionStart = normalizedContent.indexOf(sectionHeader + ":");
            if (sectionStart == -1) {
                sectionStart = normalizedContent.indexOf(sectionHeader.toUpperCase());
            }
            if (sectionStart == -1) {
                continue;
            }
            
            // Find the end of this section (start of next section)
            int sectionEnd = text.length();
            for (String nextHeader : SECTION_HEADERS) {
                if (nextHeader.equals(sectionHeader)) {
                    continue;
                }
                
                int nextStart = normalizedContent.indexOf(nextHeader + ":", sectionStart + sectionHeader.length());
                if (nextStart == -1) {
                    nextStart = normalizedContent.indexOf(nextHeader.toUpperCase(), sectionStart + sectionHeader.length());
                }
                if (nextStart != -1 && nextStart < sectionEnd) {
                    sectionEnd = nextStart;
                }
            }
            
            // Extract section content
            String sectionContent = text.substring(
                    sectionStart + sectionHeader.length() + 1, // +1 for the colon
                    sectionEnd
            ).trim();
            
            sections.put(sectionHeader, sectionContent);
            logger.debug("Found section: {} ({} characters)", sectionHeader, sectionContent.length());
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
            bulletPoints.add(matcher.group(1).trim());
        }
        
        logger.debug("Extracted {} bullet points", bulletPoints.size());
        return bulletPoints;
    }

    /**
     * Evaluates the quality of the text extraction
     * 
     * @param content The extracted content
     * @return Quality score (0-100)
     */
    @Override
    public int evaluateExtractionQuality(ResumeContent content) {
        logger.debug("Evaluating extraction quality");
        
        int score = 100;
        
        // Check raw text length
        String rawText = content.getRawText();
        if (rawText == null || rawText.isEmpty()) {
            return 0;
        }
        
        // Penalize for short text
        if (rawText.length() < 500) {
            score -= 30;
        } else if (rawText.length() < 1000) {
            score -= 15;
        }
        
        // Penalize for few sections
        if (content.getSections().size() < 3) {
            score -= 20;
        }
        
        // Penalize for OCR extraction (it's typically lower quality)
        String extractionMethod = content.getMetadata() != null ? 
                content.getMetadata().get("extractionMethod") : null;
        if ("OCR".equals(extractionMethod)) {
            score -= 10;
        }
        
        // Check for common extraction errors
        if (rawText.contains("") || rawText.contains("□")) {
            score -= 15; // Contains unrecognized characters
        }
        
        // Limit score to 0-100 range
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * Extracts text from a PDF file using Apache PDFBox
     * 
     * @param pdfFile The PDF file
     * @return Raw text extracted from the PDF
     * @throws IOException If there's an error reading the PDF
     */
    private String extractTextWithPdfBox(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e) {
            logger.error("Error extracting text from PDF using PDFBox", e);
            throw e;
        }
    }
    
    /**
     * Extracts text from a PDF file using OCR
     * 
     * @param pdfFile The PDF file
     * @return Text extracted via OCR
     * @throws IOException If there's an error processing the file
     */
    private String extractTextWithOcr(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            StringBuilder sb = new StringBuilder();
            
            // Process each page
            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
                
                try {
                    String pageText = tesseract.doOCR(image);
                    sb.append(pageText).append("\n");
                } catch (TesseractException e) {
                    logger.error("Error performing OCR on page {}", pageIndex, e);
                    // Continue with next page
                }
            }
            
            return sb.toString();
        } catch (IOException e) {
            logger.error("Error rendering PDF for OCR", e);
            throw e;
        }
    }
    
    /**
     * Saves an uploaded file to a temporary location
     * 
     * @param file The uploaded file
     * @return Path to the temporary file
     * @throws IOException If there's an error saving the file
     */
    private Path saveFileTemporarily(MultipartFile file) throws IOException {
        // Create storage directory if it doesn't exist
        File directory = new File(storageLocation);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create storage directory");
            }
        }
        
        // Create a unique filename
        String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
        Path targetPath = Path.of(storageLocation, filename);
        
        // Save the file
        try (FileOutputStream fos = new FileOutputStream(targetPath.toFile())) {
            fos.write(file.getBytes());
        }
        
        return targetPath;
    }

    /**
     * Identifies skills from resume content
     * 
     * @param content The extracted resume content
     * @return Set of skills identified in the resume
     */
    @Override
    public Set<Skill> identifySkills(ResumeContent content) {
        // This is a placeholder. In a real implementation, this would use NLP to extract skills.
        // We'll implement this in the NLP module later
        logger.info("Skill identification called - this is a placeholder implementation");
        
        Set<Skill> skills = new HashSet<>();
        // Just return an empty set for now
        return skills;
    }

    /**
     * Detects sections in the resume (e.g., experience, education, skills)
     * 
     * @param content The raw text content from the resume
     * @return ResumeContent with populated sections
     */
    @Override
    public ResumeContent detectSections(String content) {
        logger.debug("Detecting sections in resume content");
        
        ResumeContent resumeContent = new ResumeContent();
        resumeContent.setRawText(content);
        
        // Extract sections using the private method
        Map<String, String> sections = extractSections(content);
        resumeContent.setSections(sections);
        
        // Extract bullet points
        List<String> bulletPoints = extractBulletPoints(content);
        resumeContent.setBulletPoints(bulletPoints);
        
        return resumeContent;
    }
} 