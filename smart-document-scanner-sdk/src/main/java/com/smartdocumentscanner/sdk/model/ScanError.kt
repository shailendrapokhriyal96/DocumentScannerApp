package com.smartdocumentscanner.sdk.model

/**
 * Represents different types of errors that can occur during scanning
 */
sealed class ScanError {
    /**
     * Camera permission was denied
     */
    object CameraPermissionDenied : ScanError()
    
    /**
     * Storage permission was denied
     */
    object StoragePermissionDenied : ScanError()
    
    /**
     * Camera is not available on the device
     */
    object CameraNotAvailable : ScanError()
    
    /**
     * ML Kit services are not available
     */
    object MLKitNotAvailable : ScanError()
    
    /**
     * Document scanner failed to initialize
     */
    object ScannerInitializationFailed : ScanError()
    
    /**
     * OCR processing failed
     */
    object OCRProcessingFailed : ScanError()
    
    /**
     * Image processing failed
     */
    object ImageProcessingFailed : ScanError()
    
    /**
     * File save operation failed
     */
    object FileSaveFailed : ScanError()
    
    /**
     * PDF generation failed
     */
    object PDFGenerationFailed : ScanError()
    
    /**
     * Generic error with custom message
     */
    data class GenericError(val message: String) : ScanError()
    
    /**
     * Network error (if using cloud-based services)
     */
    data class NetworkError(val message: String) : ScanError()
}

/**
 * Extension function to get a user-friendly error message
 */
fun ScanError.getErrorMessage(): String {
    return when (this) {
        is ScanError.CameraPermissionDenied -> "Camera permission is required to scan documents"
        is ScanError.StoragePermissionDenied -> "Storage permission is required to save scanned documents"
        is ScanError.CameraNotAvailable -> "Camera is not available on this device"
        is ScanError.MLKitNotAvailable -> "ML Kit services are not available"
        is ScanError.ScannerInitializationFailed -> "Failed to initialize document scanner"
        is ScanError.OCRProcessingFailed -> "Failed to extract text from the document"
        is ScanError.ImageProcessingFailed -> "Failed to process the scanned image"
        is ScanError.FileSaveFailed -> "Failed to save the scanned document"
        is ScanError.PDFGenerationFailed -> "Failed to generate PDF from the document"
        is ScanError.GenericError -> message
        is ScanError.NetworkError -> "Network error: $message"
    }
}
