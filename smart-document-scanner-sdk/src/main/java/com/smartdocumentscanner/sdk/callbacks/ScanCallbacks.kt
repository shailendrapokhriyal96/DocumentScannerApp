package com.smartdocumentscanner.sdk.callbacks

import com.smartdocumentscanner.sdk.model.ScanResult
import com.smartdocumentscanner.sdk.model.ScanError

/**
 * Callback interface for document scanning operations
 */
interface ScanCallbacks {
    /**
     * Called when the scan operation is successful
     * @param result The scan result containing the scanned image and metadata
     */
    fun onScanSuccess(result: ScanResult)
    
    /**
     * Called when the scan operation fails
     * @param error The error that occurred during scanning
     */
    fun onScanError(error: ScanError)
    
    /**
     * Called when the scan operation is cancelled by the user
     */
    fun onScanCancelled()
}

/**
 * Callback interface for OCR text extraction
 */
interface OCRCallbacks {
    /**
     * Called when OCR text extraction is successful
     * @param text The extracted text
     * @param confidence The confidence score of the extraction (0.0 to 1.0)
     */
    fun onTextExtracted(text: String, confidence: Float)
    
    /**
     * Called when OCR text extraction fails
     * @param error The error that occurred during OCR processing
     */
    fun onOCRError(error: ScanError)
}

/**
 * Callback interface for file operations (save, export)
 */
interface FileOperationCallbacks {
    /**
     * Called when a file operation is successful
     * @param filePath The path where the file was saved
     * @param operationType The type of operation performed (IMAGE, PDF, etc.)
     */
    fun onFileSaved(filePath: String, operationType: FileOperationType)
    
    /**
     * Called when a file operation fails
     * @param error The error that occurred during the file operation
     */
    fun onFileOperationError(error: ScanError)
}

/**
 * Enum representing different types of file operations
 */
enum class FileOperationType {
    IMAGE,
    PDF,
    TEXT
}
