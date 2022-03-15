package com.example.moviecatalogue.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviecatalogue.adapter.DataAdapter
import com.example.moviecatalogue.entity.DataItems
import com.example.moviecatalogue.DetailActivity
import com.example.moviecatalogue.R
import kotlinx.android.synthetic.main.fragment_movies.*

class MovieFragment : Fragment() {
    private lateinit var adapter: DataAdapter
    private lateinit var movieViewModel: MovieViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movies, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DataAdapter()
        adapter.notifyDataSetChanged()

        rv_movies.layoutManager = GridLayoutManager(context, 2)
        rv_movies.adapter = adapter

        search_movie.isIconifiedByDefault = false
        search_movie.queryHint = resources.getString(R.string.search_movie)

        movieViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MovieViewModel::class.java)

        val languageCode = resources.getString(R.string.language_code)
        movieViewModel.setMovie(languageCode, "")
        showLoading(true)

        search_movie.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                movieViewModel.setMovie(languageCode, query)
                showLoading(true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == ""){
                    movieViewModel.setMovie(languageCode, newText)
                    showLoading(true)
                }
                return true
            }
        })

        movieViewModel.getMovies().observe(viewLifecycleOwner, Observer { dataItems ->
            if(dataItems != null){
                adapter.setData(dataItems)
                showLoading(false)
            } else{
                Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show()
            }
        })

        adapter.setOnItemClickCallback(object : DataAdapter.OnItemClickCallback{
            override fun onItemClicked(data: DataItems){
                val dataItems= DataItems(
                    data.id,
                    data.title,
                    data.rating,
                    data.release_date,
                    data.overview,
                    data.poster,
                    data.background
                )
                val goToDetailIntent = Intent(context, DetailActivity::class.java)
                goToDetailIntent.putExtra(DetailActivity.EXTRA_DATA, dataItems)
                goToDetailIntent.putExtra(DetailActivity.REQUEST_CODE, 100)
                startActivity(goToDetailIntent)
            }
        })
    }

    private fun showLoading(state: Boolean){
        if(state){
            pb_movies.visibility = View.VISIBLE
            rv_movies.visibility = View.INVISIBLE
        }else{
            pb_movies.visibility = View.GONE
            rv_movies.visibility = View.VISIBLE
        }
    }
}