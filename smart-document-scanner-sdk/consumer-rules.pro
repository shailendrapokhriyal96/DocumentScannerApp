# Consumer ProGuard rules for Smart Document Scanner SDK
# These rules will be applied to apps that use this SDK

# Keep SDK public API
-keep public class com.smartdocumentscanner.sdk.SmartDocumentScanner { *; }
-keep public class com.smartdocumentscanner.sdk.SmartDocumentScannerBuilder { *; }
-keep public class com.smartdocumentscanner.sdk.config.** { *; }
-keep public interface com.smartdocumentscanner.sdk.callbacks.** { *; }

# Keep result classes
-keep class com.smartdocumentscanner.sdk.model.** { *; }

# Keep ML Kit classes that might be used by consumers
-keep class com.google.mlkit.vision.** { *; }
-keep class com.google.android.gms.mlkit.** { *; }
