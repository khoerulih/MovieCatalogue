package com.example.moviecatalogue.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviecatalogue.adapter.DataAdapter
import com.example.moviecatalogue.entity.DataItems
import com.example.moviecatalogue.DetailActivity
import com.example.moviecatalogue.R
import kotlinx.android.synthetic.main.fragment_tv_shows.*

class TvShowFragment : Fragment() {

    private lateinit var adapter: DataAdapter
    private lateinit var tvShowViewModel: TvShowViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tv_shows, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DataAdapter()
        adapter.notifyDataSetChanged()

        rv_tv_show.layoutManager = GridLayoutManager(context, 2)
        rv_tv_show.adapter = adapter

        search_show.isIconifiedByDefault = false
        search_show.queryHint = resources.getString(R.string.search_show)

        tvShowViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            TvShowViewModel::class.java)

        val languageCode = resources.getString(R.string.language_code)
        tvShowViewModel.setShows(languageCode, "")
        showLoading(true)

        search_show.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                tvShowViewModel.setShows(languageCode, query)
                showLoading(true)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if(newText == ""){
                    tvShowViewModel.setShows(languageCode, newText)
                    showLoading(true)
                }
                return true
            }
        })

        tvShowViewModel.getShows().observe(viewLifecycleOwner, Observer { dataItems ->
            if(dataItems != null){
                adapter.setData(dataItems)
                showLoading(false)
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
                goToDetailIntent.putExtra(DetailActivity.REQUEST_CODE, 200)
                startActivity(goToDetailIntent)
            }
        })

    }

    private fun showLoading(state: Boolean){
        if(state){
            pb_shows.visibility = View.VISIBLE
            rv_tv_show.visibility = View.INVISIBLE
        }else{
            pb_shows.visibility = View.GONE
            rv_tv_show.visibility = View.VISIBLE
        }
    }
}