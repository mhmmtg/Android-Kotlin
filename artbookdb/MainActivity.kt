package com.mguler.artbookdb

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.mguler.artbookdb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var artList: ArrayList<ArtBookModel>
    private lateinit var artAdapter: ArtBookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        artList = ArrayList()
        getListItems()
    }

    override fun onResume() {
        super.onResume()
        binding.recyclerMain.layoutManager = LinearLayoutManager(this)
        artAdapter = ArtBookAdapter(artList)
        binding.recyclerMain.adapter = artAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.optionmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.addItem) {
            startActivity(Intent(this, AddArtActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getListItems() {
        try {
            val myDb = openOrCreateDatabase("ArtBook", MODE_PRIVATE, null)
            val cursor = myDb.rawQuery("SELECT * FROM arts", null)

            val idIx = cursor.getColumnIndex("id")
            val artNameIx = cursor.getColumnIndex("artName")

            //hangileri while içinde
            while (cursor.moveToNext()) {
                val artId = cursor.getInt(idIx)
                val artName = cursor.getString(artNameIx)

                val artBook = ArtBookModel(artId, artName)
                artList.add(artBook)
            }

            //artAdapter.notifyDataSetChanged()
            cursor.close()
        } catch (e: Exception) { e.printStackTrace() }

    }
}