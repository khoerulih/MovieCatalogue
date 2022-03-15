package com.example.moviecatalogue.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviecatalogue.entity.DataItems
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject

class MovieViewModel : ViewModel() {
    companion object {
        private const val API_KEY = "8472c517d8542b00b656d218b02ad968"
    }

    val listMovie = MutableLiveData<ArrayList<DataItems>>()

    internal fun setMovie(languageCode: String, movieName: String){
        val client = AsyncHttpClient()
        val listItems = ArrayList<DataItems>()
        val url: String?

        if(movieName == ""){
            url = "https://api.themoviedb.org/3/discover/movie?api_key=$API_KEY&language=$languageCode"
        }else{
            url = "https://api.themoviedb.org/3/search/movie?api_key=$API_KEY&language=$languageCode&query=$movieName"
        }


        client.get(url, object: AsyncHttpResponseHandler(){
            override fun onSuccess(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray
            ) {
                try{
                    val result = String(responseBody)
                    val responseObject = JSONObject(result)
                    val list = responseObject.getJSONArray("results")

                    for(i in 0 until list.length()){
                        val movie = list.getJSONObject(i)
                        val movieItems = DataItems()
                        movieItems.id = movie.getString("id")
                        movieItems.title = movie.getString("title")
                        val rating = movie.getString("vote_average")
                        if(rating == "0"){
                            movieItems.rating = "N/A"
                        }else{
                            movieItems.rating = rating
                        }
                        movieItems.release_date = movie.getString("release_date")
                        movieItems.overview = movie.getString("overview")
                        val urlPoster = movie.getString("poster_path")
                        if(urlPoster == "null"){
                            movieItems.poster = urlPoster
                        }else{
                            movieItems.poster = "https://image.tmdb.org/t/p/w500${urlPoster}"
                        }
                        val urlBackground = movie.getString("backdrop_path")
                        if(urlBackground == "null"){
                            movieItems.background = urlBackground
                        }else{
                            movieItems.background = "https://image.tmdb.org/t/p/w780${urlBackground}"
                        }
                        listItems.add(movieItems)
                    }
                    listMovie.postValue(listItems)
                }catch (e: Exception){
                    Log.d("Exception", e.message.toString())
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>,
                responseBody: ByteArray,
                error: Throwable
            ) {
                Log.d("onFailure", error.message.toString())
            }
        })
    }

    internal fun getMovies(): LiveData<ArrayList<DataItems>>{
        return listMovie
    }
}