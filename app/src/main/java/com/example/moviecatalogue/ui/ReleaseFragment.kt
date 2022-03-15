package com.example.moviecatalogue.ui


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.moviecatalogue.DetailActivity

import com.example.moviecatalogue.R
import com.example.moviecatalogue.adapter.DataAdapter
import com.example.moviecatalogue.entity.DataItems
import kotlinx.android.synthetic.main.fragment_release.*
import java.text.SimpleDateFormat
import java.util.*

class ReleaseFragment : Fragment() {
    private lateinit var adapter: DataAdapter
    private lateinit var releaseViewModel: ReleaseViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_release, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DataAdapter()
        adapter.notifyDataSetChanged()

        rv_release.layoutManager = GridLayoutManager(context, 2)
        rv_release.adapter = adapter

        releaseViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            ReleaseViewModel::class.java)

        releaseViewModel.getMovies().observe(viewLifecycleOwner, Observer { dataItems ->
            if(dataItems != null){
                adapter.setData(dataItems)
                showLoading(false)
            }
        })

        val languageCode = resources.getString(R.string.language_code)
        releaseViewModel.setMovie(languageCode, getCurrentDate())
        showLoading(true)

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
            pb_release.visibility = View.VISIBLE
        }else{
            pb_release.visibility = View.GONE
        }
    }

    private fun getCurrentDate(): String{
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }
}
