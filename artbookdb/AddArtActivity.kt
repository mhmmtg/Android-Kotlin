package com.mguler.artbookdb

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mguler.artbookdb.databinding.ActivityAddArtBinding
import java.io.ByteArrayOutputStream

class AddArtActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddArtBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    private lateinit var selectedBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArtBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener { addNewArt() }
        binding.imageSelect.setOnClickListener { selectImage() }

        registerLaunchers()
    }

    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    val imageUri = intentFromResult.data
                    if (imageUri != null) {
                        selectedBitmap = uriToBitmap(imageUri)
                        binding.imageSelect.setImageBitmap(selectedBitmap)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) { //granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
            else { //denied //check manifest
                Toast.makeText(this, "Permission Needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun selectImage() {
        val permRead = Manifest.permission.READ_EXTERNAL_STORAGE

        val permBool = ContextCompat.checkSelfPermission(this@AddArtActivity, permRead) == PackageManager.PERMISSION_GRANTED
        val rationaleBool = ActivityCompat.shouldShowRequestPermissionRationale(this@AddArtActivity, permRead)

        if (!permBool) {
            if (rationaleBool) { // izin verilmedi ve mantık göstermeliyim, snackbar tıklanınca izin iste
                Snackbar.make(binding.root, "Permission needed for gallery!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") { permissionLauncher.launch(permRead) }.show()
            }
            else { //zorunda değilim select image tıklanınca izin iste
                permissionLauncher.launch(permRead)
            }
        }
        else { //izin verildi, galeriye git resim seç resmin uri sini al
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }



    private fun addNewArt() {
        val artName = binding.editArtName.text.toString()
        val artistName = binding.editArtistName.text.toString()
        val year = binding.editYear.text.toString()

        val smallerImage = makeSmallerBitmap(selectedBitmap, 300)
        val outputStream = ByteArrayOutputStream()
        smallerImage.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
        val imageByteArray = outputStream.toByteArray()

        try {
            val myDb = openOrCreateDatabase("ArtBook", MODE_PRIVATE, null)
            myDb.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY, artName VARCHAR, artistName VARCHAR, year VARCHAR, image BLOB)")

            //resimde sorun çıkardı
            //myDb.execSQL("INSERT INTO arts(artName, artistName, year, image) VALUES('${artName}', '${artistName}', '${year}', '${imageByteArray}')")

            val sqlString = "INSERT INTO arts(artName, artistName, year, image) VALUES(?,?,?,?)"
            val statement = myDb.compileStatement(sqlString)
            statement.bindString(1, artName)
            statement.bindString(2, artistName)
            statement.bindString(3, year)
            statement.bindBlob(4, imageByteArray)
            statement.execute()

            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
        catch (e: Exception) { e.printStackTrace()}
    }



    private fun uriToBitmap(imageUri: Uri): Bitmap {
        var bitmap: Bitmap? = null
        try {
            if (Build.VERSION.SDK_INT >= 28) {
                val source = ImageDecoder.createSource(contentResolver, imageUri)
                bitmap = ImageDecoder.decodeBitmap(source)
            }
            else {
                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            }
        } catch (e: Exception) {
        }
        return bitmap ?: throw NullPointerException("Expression 'bitmap' must not be null")
    }



    private fun makeSmallerBitmap(image: Bitmap, longSide: Int) : Bitmap {
        val width = image.width
        val height = image.height

        val ratio: Double
        if (width > height) { ratio = (width / longSide).toDouble() }
        else { ratio = (height / longSide).toDouble() }

        val newWidth = (width / ratio).toInt()
        val newHeight = (height / ratio).toInt()

        return Bitmap.createScaledBitmap(image, newWidth, newHeight, true)
    }
}