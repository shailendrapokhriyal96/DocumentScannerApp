package com.smartdocumentscanner.sdk.demo

import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.smartdocumentscanner.sdk.SmartDocumentScanner
import com.smartdocumentscanner.sdk.SmartDocumentScannerBuilder
import com.smartdocumentscanner.sdk.callbacks.FileOperationCallbacks
import com.smartdocumentscanner.sdk.callbacks.OCRCallbacks
import com.smartdocumentscanner.sdk.callbacks.ScanCallbacks
import com.smartdocumentscanner.sdk.config.ImageFormat
import com.smartdocumentscanner.sdk.config.ScannerMode
import com.smartdocumentscanner.sdk.demo.databinding.ActivityMainBinding
import com.smartdocumentscanner.sdk.model.ScanError
import com.smartdocumentscanner.sdk.model.ScanResult
import com.smartdocumentscanner.sdk.model.getErrorMessage

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var documentScanner: SmartDocumentScanner
    
    private val scanLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        documentScanner.processScanResult(result.resultCode, result.data)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupScanner()
        setupUI()
    }
    
    private fun setupScanner() {
        // Create scanner with custom configuration
        documentScanner = SmartDocumentScannerBuilder(this)
            .setScannerMode(ScannerMode.FULL)
            .setPageLimit(5)
            .setGalleryImport(true)
            .configureOCR {
                setAutoOCR(true)
                setLanguages(listOf("en", "es"))
                setMinConfidence(0.6f)
            }
            .configureExport {
                setImageFormat(ImageFormat.JPEG)
                setImageQuality(95)
                setAutoGeneratePDF(true)
                setOutputDirectory("SmartScannerDemo")
            }
            .configureUI {
                setThemeColor("#2196F3")
                setShowTips(true)
                setAppName("Smart Scanner Demo")
            }
            .build()
        
        // Set up callbacks
        documentScanner.setScanCallbacks(object : ScanCallbacks {
            override fun onScanSuccess(result: ScanResult) {
                runOnUiThread {
                    binding.imageView.setImageBitmap(result.bitmap)
                    Toast.makeText(this@MainActivity, "Document scanned successfully!", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onScanError(error: ScanError) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, error.getErrorMessage(), Toast.LENGTH_LONG).show()
                }
            }
            
            override fun onScanCancelled() {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Scan cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        })
        
        documentScanner.setOCRCallbacks(object : OCRCallbacks {
            override fun onTextExtracted(text: String, confidence: Float) {
                runOnUiThread {
                    binding.ocrTextView.text = "Extracted Text (Confidence: ${(confidence * 100).toInt()}%):\n\n$text"
                    Toast.makeText(this@MainActivity, "Text extracted with ${(confidence * 100).toInt()}% confidence", Toast.LENGTH_SHORT).show()
                }
            }
            
            override fun onOCRError(error: ScanError) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "OCR failed: ${error.getErrorMessage()}", Toast.LENGTH_SHORT).show()
                }
            }
        })
        
        documentScanner.setFileOperationCallbacks(object : FileOperationCallbacks {
            override fun onFileSaved(filePath: String, operationType: com.smartdocumentscanner.sdk.callbacks.FileOperationType) {
                runOnUiThread {
                    val message = when (operationType) {
                        com.smartdocumentscanner.sdk.callbacks.FileOperationType.IMAGE -> "Image saved to: $filePath"
                        com.smartdocumentscanner.sdk.callbacks.FileOperationType.PDF -> "PDF saved to: $filePath"
                        com.smartdocumentscanner.sdk.callbacks.FileOperationType.TEXT -> "Text saved to: $filePath"
                    }
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                }
            }
            
            override fun onFileOperationError(error: ScanError) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "File operation failed: ${error.getErrorMessage()}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
    
    private fun setupUI() {
        binding.scanButton.setOnClickListener {
            documentScanner.startScanning(this, scanLauncher)
        }
        
        binding.saveImageButton.setOnClickListener {
            // Get the current bitmap from the ImageView
            binding.imageView.drawable?.let { drawable: android.graphics.drawable.Drawable ->
                if (drawable is android.graphics.drawable.BitmapDrawable) {
                    documentScanner.saveAsImage(drawable.bitmap)
                } else {
                    Toast.makeText(this, "No image to save", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.savePdfButton.setOnClickListener {
            // Get the current bitmap from the ImageView
            binding.imageView.drawable?.let { drawable: android.graphics.drawable.Drawable ->
                if (drawable is android.graphics.drawable.BitmapDrawable) {
                    documentScanner.saveAsPDF(drawable.bitmap)
                } else {
                    Toast.makeText(this, "No image to save as PDF", Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        binding.performOcrButton.setOnClickListener {
            // Get the current bitmap from the ImageView
            binding.imageView.drawable?.let { drawable: android.graphics.drawable.Drawable ->
                if (drawable is android.graphics.drawable.BitmapDrawable) {
                    documentScanner.performOCR(drawable.bitmap)
                } else {
                    Toast.makeText(this, "No image to perform OCR on", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
