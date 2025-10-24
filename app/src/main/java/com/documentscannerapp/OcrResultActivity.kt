package com.documentscannerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.documentscannerapp.databinding.ActivityOcrResultBinding

class OcrResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOcrResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOcrResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val text = intent.getStringExtra("ocrText") ?: ""
        binding.ocrTextView.text = text
    }
}
