package com.smartdocumentscanner.sdk.model

import android.graphics.Bitmap
import android.net.Uri

/**
 * Represents the result of a document scan operation
 */
data class ScanResult(
    /**
     * The scanned image as a Bitmap
     */
    val bitmap: Bitmap,
    
    /**
     * The URI of the scanned image
     */
    val imageUri: Uri,
    
    /**
     * The extracted text from OCR (if performed)
     */
    val extractedText: String? = null,
    
    /**
     * The confidence score of the OCR extraction (0.0 to 1.0)
     */
    val confidence: Float? = null,
    
    /**
     * Additional metadata about the scan
     */
    val metadata: ScanMetadata? = null
)

/**
 * Metadata about the scan operation
 */
data class ScanMetadata(
    /**
     * The width of the scanned image
     */
    val width: Int,
    
    /**
     * The height of the scanned image
     */
    val height: Int,
    
    /**
     * The format of the scanned image (JPEG, PNG, etc.)
     */
    val format: String,
    
    /**
     * The timestamp when the scan was performed
     */
    val timestamp: Long = System.currentTimeMillis(),
    
    /**
     * The quality of the scan (if available)
     */
    val quality: Float? = null
)
