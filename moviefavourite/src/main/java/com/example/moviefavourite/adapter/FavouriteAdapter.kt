package com.example.moviefavourite.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviefavourite.R
import com.example.moviefavourite.entity.Favourite
import kotlinx.android.synthetic.main.item_favorite_grid.view.*

class FavouriteAdapter: RecyclerView.Adapter<FavouriteAdapter.FavoriteViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null

    var listFavorite = ArrayList<Favourite>()
        set(listFavorite){
            this.listFavorite.clear()
            this.listFavorite.addAll(listFavorite)
            notifyDataSetChanged()
        }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_grid, parent, false)
        return FavoriteViewHolder(mView)
    }

    override fun getItemCount(): Int = listFavorite.size

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(listFavorite[position])
    }

    inner class FavoriteViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(favourite: Favourite){
            with(itemView){
                Glide.with(itemView.context)
                    .load(favourite.poster)
                    .apply(RequestOptions().override(400,550))
                    .into(img_poster)

                tv_title.text = favourite.title
                tv_rating.text = favourite.rating
                itemView.setOnClickListener{onItemClickCallback?.onItemClicked(favourite)}
            }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(favourite: Favourite)
    }
}