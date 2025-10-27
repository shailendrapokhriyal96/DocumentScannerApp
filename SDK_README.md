# Smart Document Scanner SDK

A comprehensive Android SDK for document scanning with OCR capabilities, built on top of Google ML Kit.

## Features

- Document Scanning: High-quality document scanning using Google ML Kit
- OCR Text Extraction: Extract text from scanned documents with confidence scoring
- Multiple Export Formats: Save as JPEG, PNG, WebP, or PDF
- Customizable UI: Theme colors, tips, and branding options
- Multi-language Support: OCR support for multiple languages
- Easy Integration: Simple API with comprehensive callbacks
- Highly Configurable: Extensive configuration options for all features

## Quick Start

### 1. Add Dependency

Add the SDK to your app's build.gradle:

```gradle
dependencies {
    implementation 'com.smartdocumentscanner:smart-document-scanner-sdk:1.0.0'
}
```

### 2. Add Permissions

Add required permissions to your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
```

### 3. Basic Usage

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var documentScanner: SmartDocumentScanner
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create scanner with default configuration
        documentScanner = SmartDocumentScanner.create(this)
        
        // Set up callbacks
        documentScanner.setScanCallbacks(object : ScanCallbacks {
            override fun onScanSuccess(result: ScanResult) {
                // Handle successful scan
                imageView.setImageBitmap(result.bitmap)
            }
            
            override fun onScanError(error: ScanError) {
                // Handle scan error
                Toast.makeText(this@MainActivity, error.getErrorMessage(), Toast.LENGTH_SHORT).show()
            }
            
            override fun onScanCancelled() {
                // Handle scan cancellation
            }
        })
    }
    
    private fun startScanning() {
        val launcher = registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            documentScanner.processScanResult(result.resultCode, result.data)
        }
        
        documentScanner.startScanning(this, launcher)
    }
}
```

## Advanced Configuration

### Custom Configuration

```kotlin
val documentScanner = SmartDocumentScannerBuilder(context)
    .setScannerMode(ScannerMode.FULL)
    .setPageLimit(5)
    .setGalleryImport(true)
    .configureOCR {
        setAutoOCR(true)
        setLanguages(listOf("en", "es", "fr"))
        setMinConfidence(0.7f)
    }
    .configureExport {
        setImageFormat(ImageFormat.JPEG)
        setImageQuality(95)
        setAutoGeneratePDF(true)
        setOutputDirectory("MyApp/ScannedDocs")
    }
    .configureUI {
        setThemeColor("#2196F3")
        setShowTips(true)
        setAppName("My App")
    }
    .build()
```

## Requirements

- Android API 24+ (Android 7.0)
- Camera permission
- Storage permission (for saving files)
- Google Play Services (for ML Kit)

## Demo App

A complete demo app is included in the sdk-demo-app module, showcasing all SDK features and usage patterns.

## License

This SDK is licensed under the Apache License 2.0.

## Support

For support and questions, please open an issue on the GitHub repository.
