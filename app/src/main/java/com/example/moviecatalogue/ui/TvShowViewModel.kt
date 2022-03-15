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

class TvShowViewModel : ViewModel() {
    companion object {
        private const val API_KEY = "8472c517d8542b00b656d218b02ad968"
    }

    val listShow = MutableLiveData<ArrayList<DataItems>>()

    internal fun setShows(languageCode: String, showName: String?){
        val client = AsyncHttpClient()
        val listItems = ArrayList<DataItems>()
        val url: String?

        if(showName == ""){
            url = "https://api.themoviedb.org/3/discover/tv?api_key=$API_KEY&language=$languageCode"
        }else{
            url = "https://api.themoviedb.org/3/search/tv?api_key=$API_KEY&language=$languageCode&query=$showName"
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
                        val show = list.getJSONObject(i)
                        val showItems =
                            DataItems()
                        showItems.id = show.getString("id")
                        showItems.title = show.getString("name")
                        val rating = show.getString("vote_average")
                        if(rating == "0"){
                            showItems.rating = "N/A"
                        }else{
                            showItems.rating = rating
                        }
                        showItems.release_date = show.getString("first_air_date")
                        showItems.overview = show.getString("overview")
                        val urlPoster = show.getString("poster_path")
                        if(urlPoster == "null"){
                            showItems.poster = urlPoster
                        }else{
                            showItems.poster = "https://image.tmdb.org/t/p/w500${urlPoster}"
                        }
                        val urlBackground = show.getString("backdrop_path")
                        if(urlBackground == "null"){
                            showItems.background = urlBackground
                        }else{
                            showItems.background = "https://image.tmdb.org/t/p/w780${urlBackground}"
                        }
                        listItems.add(showItems)
                    }
                    listShow.postValue(listItems)
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

    internal fun getShows(): LiveData<ArrayList<DataItems>>{
        return listShow
    }
}