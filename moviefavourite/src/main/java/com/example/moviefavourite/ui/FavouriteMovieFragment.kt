package com.example.moviefavourite.ui

import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviefavourite.DetailFavouriteActivity
import com.example.moviefavourite.R
import com.example.moviefavourite.adapter.FavouriteAdapter
import com.example.moviefavourite.entity.Favourite
import com.example.moviefavourite.helper.MappingHelper
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import kotlinx.android.synthetic.main.fragment_favorite_movie.*
import kotlinx.coroutines.*

class FavouriteMovieFragment : Fragment() {
    private lateinit var adapter: FavouriteAdapter

    companion object {
        private const val EXTRA_STATE = "extra_state"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite_movie, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rv_favorite_movies.layoutManager = GridLayoutManager(context, 2)
        rv_favorite_movies.setHasFixedSize(true)
        adapter = FavouriteAdapter()
        rv_favorite_movies.adapter = adapter

        adapter.setOnItemClickCallback(object : FavouriteAdapter.OnItemClickCallback {
            override fun onItemClicked(favourite: Favourite) {
                val favItems = Favourite(
                    favourite.id,
                    favourite.title,
                    favourite.rating,
                    favourite.release_date,
                    favourite.overview,
                    favourite.poster,
                    favourite.background
                )
                val goToDetailIntent = Intent(context, DetailFavouriteActivity::class.java)
                goToDetailIntent.putExtra(DetailFavouriteActivity.EXTRA_DATA, favItems)
                goToDetailIntent.putExtra(DetailFavouriteActivity.REQUEST_CODE, 100)
                startActivityForResult(goToDetailIntent, DetailFavouriteActivity.REQUEST_UPDATE)
            }
        })

        val handlerThread = HandlerThread("DataMovieObserver")
        handlerThread.start()
        val handler = Handler(handlerThread.looper)

        val myObserver = object : ContentObserver(handler){
            override fun onChange(selfChange: Boolean) {
                loadFavoriteAsync()
            }
        }

        context?.contentResolver?.registerContentObserver(CONTENT_URI, true, myObserver)

        if (savedInstanceState == null) {
            loadFavoriteAsync()
        } else {
            val list = savedInstanceState.getParcelableArrayList<Favourite>(EXTRA_STATE)
            if (list != null) {
                adapter.listFavorite = list
            }
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList(EXTRA_STATE, adapter.listFavorite)
    }

    private fun loadFavoriteAsync() {
        GlobalScope.launch(Dispatchers.Main) {
            showLoading(true)
            delay(200)
            val deferredFavorite = async(Dispatchers.IO) {
                val cursor = context?.contentResolver?.query(CONTENT_URI, null, null, null, null)
                MappingHelper.mapCursorToArrayListMovie(cursor)
            }
            val favorite = deferredFavorite.await()
            showLoading(false)
            if (favorite.size > 0) {
                adapter.listFavorite = favorite
                isEmpty(false)
            } else {
                adapter.listFavorite = ArrayList()
                isEmpty(true)
            }
        }
    }

    private fun showLoading(state: Boolean){
        if(pb_favorite_movies != null){
            if(state){
                pb_favorite_movies.visibility = View.VISIBLE
            }else{
                pb_favorite_movies.visibility = View.GONE
            }
        }
    }

    private fun isEmpty(state: Boolean){
        if(rl_movie_empty != null){
            if(state){
                rl_movie_empty.visibility = View.VISIBLE
            }else{
                rl_movie_empty.visibility = View.GONE
            }
        }
    }
}