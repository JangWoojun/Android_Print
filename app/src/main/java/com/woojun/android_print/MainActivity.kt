package com.woojun.android_print

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.print.PrintHelper
import com.woojun.android_print.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var photoUri: Uri

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            photoUri = uri
            binding.imageView.setImageURI(uri)
        } else {
            Toast.makeText(this@MainActivity, "사진 불러오기 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.photoPickerButton.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.printButton1.setOnClickListener {
            if (this::photoUri.isInitialized) {
                getBitmapFromUri(photoUri)?.let { bitmap -> doPhotoPrint1(bitmap) }
            } else {
                Toast.makeText(this@MainActivity, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }

        binding.printButton2.setOnClickListener {
            if (this::photoUri.isInitialized) {
                getBitmapFromUri(photoUri)?.let { bitmap -> doPhotoPrint2(bitmap) }
            } else {
                Toast.makeText(this@MainActivity, "이미지를 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun doPhotoPrint1(bitmap: Bitmap) {
        this.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FIT
            }.also { printHelper ->
                printHelper.printBitmap("test.jpg - test print", bitmap)
            }
        }
    }

    private fun doPhotoPrint2(bitmap: Bitmap) {
        this.also { context ->
            PrintHelper(context).apply {
                scaleMode = PrintHelper.SCALE_MODE_FILL
            }.also { printHelper ->
                printHelper.printBitmap("test.jpg - test print", bitmap)
            }
        }
    }

    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = this.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}