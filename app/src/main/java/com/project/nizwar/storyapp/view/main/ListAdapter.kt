package com.project.nizwar.storyapp.view.main

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.nizwar.storyapp.R
import com.project.nizwar.storyapp.data.model.Story


class ListAdapter(private val listStory: List<Story>) :
    RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    interface OnItemClickCallback {
        fun onItemClicked(story: Story, optionsCompat: ActivityOptionsCompat)
    }

    fun setOnItemClickCallBack(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         var ivPhoto: ImageView = itemView.findViewById(R.id.iv_item_photo)
         var tvName: TextView = itemView.findViewById(R.id.tv_item_name)
         var tvDescription: TextView = itemView.findViewById(R.id.tv_item_description)

        fun bind(story: Story) {
            Glide.with(itemView.context)
                .load(story.photoUrl)
                .into(ivPhoto)
            tvName.text = story.name
            tvDescription.text = story.description
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
        holder.itemView.setOnClickListener {
            val optionsCompat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(
                    holder.itemView.context as Activity,
                    Pair(holder.ivPhoto, "photo"),
                    Pair(holder.tvName, "name"),
                    Pair(holder.tvDescription, "description")
                )
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition], optionsCompat)
        }

    }

    override fun getItemCount(): Int = listStory.size

}