package com.smartdocumentscanner.sdk.config

import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions

/**
 * Configuration class for the Smart Document Scanner SDK
 */
data class ScannerConfig(
    /**
     * Scanner mode - FULL for complete scanning experience, BASE for basic scanning
     */
    val scannerMode: ScannerMode = ScannerMode.FULL,
    
    /**
     * Page limit for multi-page documents (1-10)
     */
    val pageLimit: Int = 10,
    
    /**
     * Gallery import mode - whether to allow importing from gallery
     */
    val galleryImport: Boolean = true,
    
    /**
     * OCR configuration
     */
    val ocrConfig: OCRConfig = OCRConfig(),
    
    /**
     * File export configuration
     */
    val exportConfig: ExportConfig = ExportConfig(),
    
    /**
     * UI customization options
     */
    val uiConfig: UIConfig = UIConfig()
)

/**
 * Scanner mode options
 */
enum class ScannerMode {
    /**
     * Full scanning experience with all features
     */
    FULL,
    
    /**
     * Basic scanning with minimal features
     */
    BASE
}

/**
 * OCR (Optical Character Recognition) configuration
 */
data class OCRConfig(
    /**
     * Whether to automatically perform OCR after scanning
     */
    val autoOCR: Boolean = false,
    
    /**
     * OCR language codes (e.g., "en", "es", "fr")
     */
    val languages: List<String> = listOf("en"),
    
    /**
     * Minimum confidence threshold for OCR results (0.0 to 1.0)
     */
    val minConfidence: Float = 0.5f
)

/**
 * File export configuration
 */
data class ExportConfig(
    /**
     * Default image format for saving
     */
    val imageFormat: ImageFormat = ImageFormat.JPEG,
    
    /**
     * Image quality for JPEG format (1-100)
     */
    val imageQuality: Int = 90,
    
    /**
     * Whether to generate PDF automatically
     */
    val autoGeneratePDF: Boolean = false,
    
    /**
     * PDF page size
     */
    val pdfPageSize: PDFPageSize = PDFPageSize.A4,
    
    /**
     * Output directory for saved files
     */
    val outputDirectory: String? = null
)

/**
 * Image format options
 */
enum class ImageFormat {
    JPEG,
    PNG,
    WEBP
}

/**
 * PDF page size options
 */
enum class PDFPageSize {
    A4,
    LETTER,
    LEGAL
}

/**
 * UI customization configuration
 */
data class UIConfig(
    /**
     * Custom theme color (hex color code)
     */
    val themeColor: String? = null,
    
    /**
     * Whether to show scanning tips
     */
    val showTips: Boolean = true,
    
    /**
     * Custom app name for the scanner
     */
    val appName: String? = null
)

/**
 * Extension function to convert ScannerMode to GmsDocumentScannerOptions
 */
fun ScannerMode.toGmsMode(): Int {
    return when (this) {
        ScannerMode.FULL -> GmsDocumentScannerOptions.SCANNER_MODE_FULL
        ScannerMode.BASE -> GmsDocumentScannerOptions.SCANNER_MODE_BASE
    }
}
