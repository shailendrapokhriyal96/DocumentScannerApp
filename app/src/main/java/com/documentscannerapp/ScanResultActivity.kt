package com.documentscannerapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.documentscannerapp.databinding.ActivityScanResultBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.FileOutputStream

class ScanResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScanResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriStr = intent.getStringExtra("imageUri") ?: return
        val uri = Uri.parse(uriStr)
        val bitmap = contentResolver.openInputStream(uri).use { BitmapFactory.decodeStream(it) }

        binding.imageView.setImageBitmap(bitmap)

        binding.saveImageBtn.setOnClickListener {
            saveBitmap(bitmap)
        }

        binding.savePdfBtn.setOnClickListener {
            saveAsPdf(bitmap)
        }

        binding.ocrBtn.setOnClickListener {
            // Run ML Kit Text Recognition and open OCR Activity
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val i = Intent(this, OcrResultActivity::class.java)
                    i.putExtra("ocrText", visionText.text)
                    startActivity(i)
                }
                .addOnFailureListener { e -> e.printStackTrace() }
        }
    }

    private fun saveBitmap(bitmap: Bitmap) {
        val dir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "ScannedDocs")
        dir.mkdirs()
        val file = File(dir, "scan_\${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
    }

    private fun saveAsPdf(bitmap: Bitmap) {
        val dir = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ScannedDocsPdf")
        dir.mkdirs()
        val file = File(dir, "scan_\${System.currentTimeMillis()}.pdf")
        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = pdf.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        pdf.finishPage(page)
        pdf.writeTo(FileOutputStream(file))
        pdf.close()
    }
}
