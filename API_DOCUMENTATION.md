# Smart Document Scanner SDK - API Documentation

## Core Classes

### SmartDocumentScanner

Main SDK class for document scanning operations.

#### Methods

**create(context: Context)**
- Creates a new SmartDocumentScanner instance with default configuration
- Returns: SmartDocumentScanner instance

**create(context: Context, config: ScannerConfig)**
- Creates a new SmartDocumentScanner instance with custom configuration
- Parameters: context - Android context, config - Scanner configuration
- Returns: SmartDocumentScanner instance

**setScanCallbacks(callbacks: ScanCallbacks)**
- Sets the scan result callbacks
- Parameters: callbacks - Implementation of ScanCallbacks interface

**setOCRCallbacks(callbacks: OCRCallbacks)**
- Sets the OCR result callbacks
- Parameters: callbacks - Implementation of OCRCallbacks interface

**setFileOperationCallbacks(callbacks: FileOperationCallbacks)**
- Sets the file operation callbacks
- Parameters: callbacks - Implementation of FileOperationCallbacks interface

**startScanning(activity: Activity, launcher: ActivityResultLauncher<IntentSenderRequest>)**
- Starts the document scanning process
- Parameters: activity - Activity to launch scanner from, launcher - Activity result launcher

**processScanResult(resultCode: Int, data: Intent?)**
- Processes the scan result from activity result
- Parameters: resultCode - Activity result code, data - Intent data

**performOCR(bitmap: Bitmap)**
- Performs OCR on the provided bitmap
- Parameters: bitmap - Bitmap to perform OCR on

**saveAsImage(bitmap: Bitmap, filename: String?)**
- Saves the bitmap as an image file
- Parameters: bitmap - Bitmap to save, filename - Optional filename

**saveAsPDF(bitmap: Bitmap, filename: String?)**
- Saves the bitmap as a PDF file
- Parameters: bitmap - Bitmap to save, filename - Optional filename

### SmartDocumentScannerBuilder

Builder class for creating configured scanner instances.

#### Methods

**setScannerMode(mode: ScannerMode)**
- Sets the scanner mode
- Parameters: mode - FULL or BASE mode
- Returns: SmartDocumentScannerBuilder instance

**setPageLimit(limit: Int)**
- Sets the page limit for multi-page documents
- Parameters: limit - Page limit (1-10)
- Returns: SmartDocumentScannerBuilder instance

**setGalleryImport(enabled: Boolean)**
- Enables or disables gallery import
- Parameters: enabled - Whether to enable gallery import
- Returns: SmartDocumentScannerBuilder instance

**configureOCR(block: OCRConfigBuilder.() -> Unit)**
- Configures OCR settings
- Parameters: block - Configuration block for OCR settings
- Returns: SmartDocumentScannerBuilder instance

**configureExport(block: ExportConfigBuilder.() -> Unit)**
- Configures export settings
- Parameters: block - Configuration block for export settings
- Returns: SmartDocumentScannerBuilder instance

**configureUI(block: UIConfigBuilder.() -> Unit)**
- Configures UI settings
- Parameters: block - Configuration block for UI settings
- Returns: SmartDocumentScannerBuilder instance

**build()**
- Builds the SmartDocumentScanner instance
- Returns: SmartDocumentScanner instance

## Callback Interfaces

### ScanCallbacks

Interface for handling scan results.

**onScanSuccess(result: ScanResult)**
- Called when scan operation is successful
- Parameters: result - Scan result containing bitmap and metadata

**onScanError(error: ScanError)**
- Called when scan operation fails
- Parameters: error - Error that occurred during scanning

**onScanCancelled()**
- Called when scan operation is cancelled by user

### OCRCallbacks

Interface for handling OCR results.

**onTextExtracted(text: String, confidence: Float)**
- Called when OCR text extraction is successful
- Parameters: text - Extracted text, confidence - Confidence score (0.0 to 1.0)

**onOCRError(error: ScanError)**
- Called when OCR text extraction fails
- Parameters: error - Error that occurred during OCR processing

### FileOperationCallbacks

Interface for handling file operations.

**onFileSaved(filePath: String, operationType: FileOperationType)**
- Called when file operation is successful
- Parameters: filePath - Path where file was saved, operationType - Type of operation

**onFileOperationError(error: ScanError)**
- Called when file operation fails
- Parameters: error - Error that occurred during file operation

## Data Models

### ScanResult

Represents the result of a document scan operation.

**Properties:**
- bitmap: Bitmap - The scanned image as a Bitmap
- imageUri: Uri - The URI of the scanned image
- extractedText: String? - The extracted text from OCR (if performed)
- confidence: Float? - The confidence score of the OCR extraction (0.0 to 1.0)
- metadata: ScanMetadata? - Additional metadata about the scan

### ScanMetadata

Metadata about the scan operation.

**Properties:**
- width: Int - The width of the scanned image
- height: Int - The height of the scanned image
- format: String - The format of the scanned image (JPEG, PNG, etc.)
- timestamp: Long - The timestamp when the scan was performed
- quality: Float? - The quality of the scan (if available)

### ScanError

Sealed class representing different error types.

**Types:**
- CameraPermissionDenied - Camera permission was denied
- StoragePermissionDenied - Storage permission was denied
- CameraNotAvailable - Camera is not available on the device
- MLKitNotAvailable - ML Kit services are not available
- ScannerInitializationFailed - Document scanner failed to initialize
- OCRProcessingFailed - OCR processing failed
- ImageProcessingFailed - Image processing failed
- FileSaveFailed - File save operation failed
- PDFGenerationFailed - PDF generation failed
- GenericError(message: String) - Generic error with custom message
- NetworkError(message: String) - Network error (if using cloud-based services)

**Methods:**
- getErrorMessage(): String - Returns a user-friendly error message

## Configuration Classes

### ScannerConfig

Main configuration class for the SDK.

**Properties:**
- scannerMode: ScannerMode - Scanner mode (FULL/BASE)
- pageLimit: Int - Page limit for multi-page documents (1-10)
- galleryImport: Boolean - Whether to allow importing from gallery
- ocrConfig: OCRConfig - OCR configuration
- exportConfig: ExportConfig - File export configuration
- uiConfig: UIConfig - UI customization options

### OCRConfig

OCR (Optical Character Recognition) configuration.

**Properties:**
- autoOCR: Boolean - Whether to automatically perform OCR after scanning
- languages: List<String> - OCR language codes (e.g., "en", "es", "fr")
- minConfidence: Float - Minimum confidence threshold for OCR results (0.0 to 1.0)

### ExportConfig

File export configuration.

**Properties:**
- imageFormat: ImageFormat - Default image format for saving
- imageQuality: Int - Image quality for JPEG format (1-100)
- autoGeneratePDF: Boolean - Whether to generate PDF automatically
- pdfPageSize: PDFPageSize - PDF page size
- outputDirectory: String? - Output directory for saved files

### UIConfig

UI customization configuration.

**Properties:**
- themeColor: String? - Custom theme color (hex color code)
- showTips: Boolean - Whether to show scanning tips
- appName: String? - Custom app name for the scanner

## Enums

### ScannerMode
- FULL - Full scanning experience with all features
- BASE - Basic scanning with minimal features

### ImageFormat
- JPEG
- PNG
- WEBP

### PDFPageSize
- A4
- LETTER
- LEGAL

### FileOperationType
- IMAGE
- PDF
- TEXT

## Builder Classes

### OCRConfigBuilder

Builder for OCR configuration.

**Methods:**
- setAutoOCR(enabled: Boolean) - Set auto OCR
- setLanguages(languages: List<String>) - Set supported languages
- setMinConfidence(confidence: Float) - Set minimum confidence threshold

### ExportConfigBuilder

Builder for export configuration.

**Methods:**
- setImageFormat(format: ImageFormat) - Set image format
- setImageQuality(quality: Int) - Set image quality
- setAutoGeneratePDF(enabled: Boolean) - Set auto PDF generation
- setPDFPageSize(size: PDFPageSize) - Set PDF page size
- setOutputDirectory(directory: String) - Set output directory

### UIConfigBuilder

Builder for UI configuration.

**Methods:**
- setThemeColor(color: String) - Set theme color
- setShowTips(enabled: Boolean) - Set show tips
- setAppName(name: String) - Set app name
