package com.smartdocumentscanner.sdk

import android.content.Context
import com.smartdocumentscanner.sdk.config.ScannerConfig
import com.smartdocumentscanner.sdk.config.ScannerMode
import com.smartdocumentscanner.sdk.config.OCRConfig
import com.smartdocumentscanner.sdk.config.ExportConfig
import com.smartdocumentscanner.sdk.config.UIConfig

/**
 * Builder class for creating SmartDocumentScanner instances with custom configuration
 */
class SmartDocumentScannerBuilder(private val context: Context) {
    
    private var config = ScannerConfig()
    
    /**
     * Sets the scanner mode
     */
    fun setScannerMode(mode: ScannerMode): SmartDocumentScannerBuilder {
        config = config.copy(scannerMode = mode)
        return this
    }
    
    /**
     * Sets the page limit for multi-page documents
     */
    fun setPageLimit(limit: Int): SmartDocumentScannerBuilder {
        config = config.copy(pageLimit = limit.coerceIn(1, 10))
        return this
    }
    
    /**
     * Enables or disables gallery import
     */
    fun setGalleryImport(enabled: Boolean): SmartDocumentScannerBuilder {
        config = config.copy(galleryImport = enabled)
        return this
    }
    
    /**
     * Configures OCR settings
     */
    fun configureOCR(block: OCRConfigBuilder.() -> Unit): SmartDocumentScannerBuilder {
        val ocrBuilder = OCRConfigBuilder()
        ocrBuilder.block()
        config = config.copy(ocrConfig = ocrBuilder.build())
        return this
    }
    
    /**
     * Configures export settings
     */
    fun configureExport(block: ExportConfigBuilder.() -> Unit): SmartDocumentScannerBuilder {
        val exportBuilder = ExportConfigBuilder()
        exportBuilder.block()
        config = config.copy(exportConfig = exportBuilder.build())
        return this
    }
    
    /**
     * Configures UI settings
     */
    fun configureUI(block: UIConfigBuilder.() -> Unit): SmartDocumentScannerBuilder {
        val uiBuilder = UIConfigBuilder()
        uiBuilder.block()
        config = config.copy(uiConfig = uiBuilder.build())
        return this
    }
    
    /**
     * Builds the SmartDocumentScanner instance
     */
    fun build(): SmartDocumentScanner {
        return SmartDocumentScanner.create(context, config)
    }
}

/**
 * Builder for OCR configuration
 */
class OCRConfigBuilder {
    private var autoOCR = false
    private var languages = listOf("en")
    private var minConfidence = 0.5f
    
    fun setAutoOCR(enabled: Boolean) {
        autoOCR = enabled
    }
    
    fun setLanguages(languages: List<String>) {
        this.languages = languages
    }
    
    fun setMinConfidence(confidence: Float) {
        minConfidence = confidence.coerceIn(0f, 1f)
    }
    
    internal fun build(): OCRConfig {
        return OCRConfig(
            autoOCR = autoOCR,
            languages = languages,
            minConfidence = minConfidence
        )
    }
}

/**
 * Builder for export configuration
 */
class ExportConfigBuilder {
    private var imageFormat = com.smartdocumentscanner.sdk.config.ImageFormat.JPEG
    private var imageQuality = 90
    private var autoGeneratePDF = false
    private var pdfPageSize = com.smartdocumentscanner.sdk.config.PDFPageSize.A4
    private var outputDirectory: String? = null
    
    fun setImageFormat(format: com.smartdocumentscanner.sdk.config.ImageFormat) {
        imageFormat = format
    }
    
    fun setImageQuality(quality: Int) {
        imageQuality = quality.coerceIn(1, 100)
    }
    
    fun setAutoGeneratePDF(enabled: Boolean) {
        autoGeneratePDF = enabled
    }
    
    fun setPDFPageSize(size: com.smartdocumentscanner.sdk.config.PDFPageSize) {
        pdfPageSize = size
    }
    
    fun setOutputDirectory(directory: String) {
        outputDirectory = directory
    }
    
    internal fun build(): ExportConfig {
        return ExportConfig(
            imageFormat = imageFormat,
            imageQuality = imageQuality,
            autoGeneratePDF = autoGeneratePDF,
            pdfPageSize = pdfPageSize,
            outputDirectory = outputDirectory
        )
    }
}

/**
 * Builder for UI configuration
 */
class UIConfigBuilder {
    private var themeColor: String? = null
    private var showTips = true
    private var appName: String? = null
    
    fun setThemeColor(color: String) {
        themeColor = color
    }
    
    fun setShowTips(enabled: Boolean) {
        showTips = enabled
    }
    
    fun setAppName(name: String) {
        appName = name
    }
    
    internal fun build(): UIConfig {
        return UIConfig(
            themeColor = themeColor,
            showTips = showTips,
            appName = appName
        )
    }
}
