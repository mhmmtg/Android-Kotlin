package com.mguler.artbookdb

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mguler.artbookdb.databinding.ActivityDetailsBinding


class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getListItems()

    }

    private fun getListItems() {
        val intent = intent
        val artId = intent.getIntExtra("id", 0)

        try {
            val myDb = openOrCreateDatabase("ArtBook", MODE_PRIVATE, null)
            val cursor = myDb.rawQuery("SELECT * FROM arts WHERE id=?", arrayOf(artId.toString()))

            //val idIx = cursor.getColumnIndex("id")
            val artNameIx = cursor.getColumnIndex("artName")
            val artistNameIx = cursor.getColumnIndex("artistName")
            val yearIx = cursor.getColumnIndex("year")
            val imageIx = cursor.getColumnIndex("image")

            //hangileri while içinde
            while (cursor.moveToNext()) {

                val imageByte = cursor.getBlob(imageIx)
                println("image ix $imageIx")
                println("image byte ${imageByte.size}")

                val bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
                binding.imageDetail.setImageBitmap(bitmap)

                binding.textArtName.text = cursor.getString(artNameIx)
                binding.textArtistName.text = cursor.getString(artistNameIx)
                binding.textYear.text = cursor.getString(yearIx)

            }
            cursor.close()
        } catch (e: Exception) { e.printStackTrace() }



    }
}