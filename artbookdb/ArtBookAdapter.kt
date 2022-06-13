package com.mguler.artbookdb

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mguler.artbookdb.databinding.RecylerRowBinding

class ArtBookAdapter(private val artList: ArrayList<ArtBookModel>) : RecyclerView.Adapter<ArtBookAdapter.ArtBookHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtBookHolder {
        val binding = RecylerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArtBookHolder(binding)
    }

    override fun onBindViewHolder(holder: ArtBookHolder, position: Int) {
        holder.binding.textRecyclerRow.text = artList.get(position).artName
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsActivity::class.java)
            intent.putExtra("id", artList.get(position).id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int { return artList.size }

    class ArtBookHolder(val binding: RecylerRowBinding) : RecyclerView.ViewHolder(binding.root)
}