package com.example.moviecatalogue.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviecatalogue.entity.DataItems
import com.example.moviecatalogue.R
import kotlinx.android.synthetic.main.item_grid.view.*

class DataAdapter: RecyclerView.Adapter<DataAdapter.DataViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
    private val mData = ArrayList<DataItems>()

    fun setData(items: ArrayList<DataItems>){
        mData.clear()
        mData.addAll(items)
        notifyDataSetChanged()
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val mView = LayoutInflater.from(parent.context).inflate(R.layout.item_grid, parent, false)
        return DataViewHolder(mView)
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    inner class DataViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(dataItems: DataItems){
            with(itemView){
                if(dataItems.poster == "null"){
                    Glide.with(itemView.context)
                        .load(R.drawable.empty_poster)
                        .apply(RequestOptions().override(400,550))
                        .into(img_poster)
                }else {
                    Glide.with(itemView.context)
                        .load(dataItems.poster)
                        .apply(RequestOptions().override(400,550))
                        .into(img_poster)
                }

                tv_title.text = dataItems.title
                tv_rating.text = dataItems.rating
                itemView.setOnClickListener{onItemClickCallback?.onItemClicked(dataItems)}
            }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: DataItems)
    }
}