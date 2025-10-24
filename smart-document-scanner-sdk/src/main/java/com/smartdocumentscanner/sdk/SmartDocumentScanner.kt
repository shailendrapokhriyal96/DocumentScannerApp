package com.smartdocumentscanner.sdk

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.smartdocumentscanner.sdk.callbacks.FileOperationCallbacks
import com.smartdocumentscanner.sdk.callbacks.OCRCallbacks
import com.smartdocumentscanner.sdk.callbacks.ScanCallbacks
import com.smartdocumentscanner.sdk.config.ScannerConfig
import com.smartdocumentscanner.sdk.config.toGmsMode
import com.smartdocumentscanner.sdk.model.ScanError
import com.smartdocumentscanner.sdk.model.ScanResult
import com.smartdocumentscanner.sdk.model.ScanMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Main SDK class for Smart Document Scanner functionality
 */
class SmartDocumentScanner private constructor(
    private val context: Context,
    private val config: ScannerConfig
) {
    
    private var scanCallbacks: ScanCallbacks? = null
    private var ocrCallbacks: OCRCallbacks? = null
    private var fileOperationCallbacks: FileOperationCallbacks? = null
    
    companion object {
        /**
         * Creates a new SmartDocumentScanner instance with default configuration
         */
        fun create(context: Context): SmartDocumentScanner {
            return SmartDocumentScanner(context, ScannerConfig())
        }
        
        /**
         * Creates a new SmartDocumentScanner instance with custom configuration
         */
        fun create(context: Context, config: ScannerConfig): SmartDocumentScanner {
            return SmartDocumentScanner(context, config)
        }
    }
    
    /**
     * Sets the scan callbacks
     */
    fun setScanCallbacks(callbacks: ScanCallbacks) {
        this.scanCallbacks = callbacks
    }
    
    /**
     * Sets the OCR callbacks
     */
    fun setOCRCallbacks(callbacks: OCRCallbacks) {
        this.ocrCallbacks = callbacks
    }
    
    /**
     * Sets the file operation callbacks
     */
    fun setFileOperationCallbacks(callbacks: FileOperationCallbacks) {
        this.fileOperationCallbacks = callbacks
    }
    
    /**
     * Starts the document scanning process
     * @param activity The activity to launch the scanner from
     * @param launcher The activity result launcher for handling the result
     */
    fun startScanning(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>) {
        try {
            val options = GmsDocumentScannerOptions.Builder()
                .setScannerMode(config.scannerMode.toGmsMode())
                .setPageLimit(config.pageLimit)
                .setGalleryImportAllowed(config.galleryImport)
                .build()
            
            val scanner = GmsDocumentScanning.getClient(options)
            scanner.getStartScanIntent(activity)
                .addOnSuccessListener { intentSender: IntentSender ->
                    launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener { exception ->
                    scanCallbacks?.onScanError(ScanError.GenericError(exception.message ?: "Scanner initialization failed"))
                }
        } catch (e: Exception) {
            scanCallbacks?.onScanError(ScanError.ScannerInitializationFailed)
        }
    }
    
    /**
     * Processes the scan result from the activity result
     * @param resultCode The result code from the activity
     * @param data The intent data containing the scan result
     */
    fun processScanResult(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            scanCallbacks?.onScanCancelled()
            return
        }
        
        try {
            val scanResult = GmsDocumentScanningResult.fromActivityResultIntent(data)
            val page = scanResult?.pages?.firstOrNull()
            
            if (page?.imageUri == null) {
                scanCallbacks?.onScanError(ScanError.ImageProcessingFailed)
                return
            }
            
            val bitmap = context.contentResolver.openInputStream(page.imageUri)?.use { 
                BitmapFactory.decodeStream(it) 
            }
            
            if (bitmap == null) {
                scanCallbacks?.onScanError(ScanError.ImageProcessingFailed)
                return
            }
            
            val metadata = ScanMetadata(
                width = bitmap.width,
                height = bitmap.height,
                format = "JPEG"
            )
            
            val result = ScanResult(
                bitmap = bitmap,
                imageUri = page.imageUri,
                metadata = metadata
            )
            
            scanCallbacks?.onScanSuccess(result)
            
            // Auto-perform OCR if configured
            if (config.ocrConfig.autoOCR) {
                performOCR(bitmap)
            }
            
        } catch (e: Exception) {
            scanCallbacks?.onScanError(ScanError.ImageProcessingFailed)
        }
    }
    
    /**
     * Performs OCR on the provided bitmap
     * @param bitmap The bitmap to perform OCR on
     */
    fun performOCR(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val image = InputImage.fromBitmap(bitmap, 0)
                val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                
                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val confidence = calculateConfidence(visionText.text)
                        if (confidence >= config.ocrConfig.minConfidence) {
                            ocrCallbacks?.onTextExtracted(visionText.text, confidence)
                        } else {
                            ocrCallbacks?.onOCRError(ScanError.OCRProcessingFailed)
                        }
                    }
                    .addOnFailureListener { _ ->
                        ocrCallbacks?.onOCRError(ScanError.OCRProcessingFailed)
                    }
            } catch (e: Exception) {
                ocrCallbacks?.onOCRError(ScanError.OCRProcessingFailed)
            }
        }
    }
    
    /**
     * Saves the bitmap as an image file
     * @param bitmap The bitmap to save
     * @param filename The filename (optional, will generate if not provided)
     */
    fun saveAsImage(bitmap: Bitmap, filename: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputDir = config.exportConfig.outputDirectory?.let { 
                    File(context.getExternalFilesDir(null), it) 
                } ?: File(context.getExternalFilesDir(null), "ScannedDocs")
                
                outputDir.mkdirs()
                
                val fileName = filename ?: "scan_${System.currentTimeMillis()}.${config.exportConfig.imageFormat.name.lowercase()}"
                val file = File(outputDir, fileName)
                
                val format = when (config.exportConfig.imageFormat) {
                    com.smartdocumentscanner.sdk.config.ImageFormat.JPEG -> Bitmap.CompressFormat.JPEG
                    com.smartdocumentscanner.sdk.config.ImageFormat.PNG -> Bitmap.CompressFormat.PNG
                    com.smartdocumentscanner.sdk.config.ImageFormat.WEBP -> Bitmap.CompressFormat.WEBP_LOSSY
                }
                
                FileOutputStream(file).use { 
                    bitmap.compress(format, config.exportConfig.imageQuality, it) 
                }
                
                withContext(Dispatchers.Main) {
                    fileOperationCallbacks?.onFileSaved(file.absolutePath, com.smartdocumentscanner.sdk.callbacks.FileOperationType.IMAGE)
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    fileOperationCallbacks?.onFileOperationError(ScanError.FileSaveFailed)
                }
            }
        }
    }
    
    /**
     * Saves the bitmap as a PDF file
     * @param bitmap The bitmap to save as PDF
     * @param filename The filename (optional, will generate if not provided)
     */
    fun saveAsPDF(bitmap: Bitmap, filename: String? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val outputDir = config.exportConfig.outputDirectory?.let { 
                    File(context.getExternalFilesDir(null), it) 
                } ?: File(context.getExternalFilesDir(null), "ScannedDocsPdf")
                
                outputDir.mkdirs()
                
                val fileName = filename ?: "scan_${System.currentTimeMillis()}.pdf"
                val file = File(outputDir, fileName)
                
                val pdf = PdfDocument()
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                val page = pdf.startPage(pageInfo)
                page.canvas.drawBitmap(bitmap, 0f, 0f, null)
                pdf.finishPage(page)
                
                FileOutputStream(file).use { pdf.writeTo(it) }
                pdf.close()
                
                withContext(Dispatchers.Main) {
                    fileOperationCallbacks?.onFileSaved(file.absolutePath, com.smartdocumentscanner.sdk.callbacks.FileOperationType.PDF)
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    fileOperationCallbacks?.onFileOperationError(ScanError.PDFGenerationFailed)
                }
            }
        }
    }
    
    /**
     * Calculates confidence score for OCR text
     */
    private fun calculateConfidence(text: String): Float {
        // Simple confidence calculation based on text length and character types
        if (text.isEmpty()) return 0f
        
        val alphaNumericCount = text.count { it.isLetterOrDigit() }
        val totalCount = text.length
        
        return if (totalCount > 0) {
            (alphaNumericCount.toFloat() / totalCount.toFloat()).coerceIn(0f, 1f)
        } else {
            0f
        }
    }
}
