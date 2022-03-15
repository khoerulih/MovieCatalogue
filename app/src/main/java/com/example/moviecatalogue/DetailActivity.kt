package com.example.moviecatalogue

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.contentValuesOf
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.moviecatalogue.db.DatabaseContract
import com.example.moviecatalogue.db.MovieHelper
import com.example.moviecatalogue.db.TvShowHelper
import com.example.moviecatalogue.entity.DataItems
import com.example.moviecatalogue.widget.FavouriteMovieWidget
import com.example.moviecatalogue.widget.FavouriteShowWidget
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.favorite_button
import kotlinx.android.synthetic.main.activity_detail.iv_background
import kotlinx.android.synthetic.main.activity_detail.iv_poster
import kotlinx.android.synthetic.main.activity_detail.tv_overview
import kotlinx.android.synthetic.main.activity_detail.tv_rating
import kotlinx.android.synthetic.main.activity_detail.tv_release
import kotlinx.android.synthetic.main.activity_detail.tv_title
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity(), OnLikeListener{
    private var status: Int = 0
    private lateinit var uriWithId: Uri
    private lateinit var movieHelper: MovieHelper
    private lateinit var tvShowHelper: TvShowHelper

    companion object{
        const val EXTRA_DATA = "extra_data"
        const val REQUEST_CODE = "request_code"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        status = intent.getIntExtra(REQUEST_CODE, 0)

        val data = intent.getParcelableExtra(EXTRA_DATA) as DataItems

        movieHelper = MovieHelper.getInstance(applicationContext)
        movieHelper.open()

        tvShowHelper = TvShowHelper.getInstance(applicationContext)
        tvShowHelper.open()

        if(status == 100){
            val checkData = movieHelper.getDataById(data.id)
            if(checkData.isNotEmpty()){
                favorite_button.isLiked = true
            }
        }else{
            val checkData = tvShowHelper.getDataById(data.id)
            if(checkData.isNotEmpty()){
                favorite_button.isLiked = true
            }
        }

        supportActionBar?.title = data.title

        GlobalScope.launch(Dispatchers.Main){
            showLoading(true)
            delay(500)
            withContext(Dispatchers.Main){
                showLoading(false)

                if(data.poster == "null"){
                    Glide.with(applicationContext)
                        .load(R.drawable.empty_poster)
                        .apply(RequestOptions().override(350,550))
                        .into(iv_poster)
                }else {
                    Glide.with(applicationContext)
                        .load(data.poster)
                        .apply(RequestOptions().override(350, 550))
                        .into(iv_poster)
                }

                if(data.poster == "null"){
                    Glide.with(applicationContext)
                        .load(R.drawable.empty_poster)
                        .apply(RequestOptions().override(700, 330))
                        .into(iv_background)
                }else{
                    Glide.with(applicationContext)
                        .load(data.background)
                        .apply(RequestOptions().override(700, 330))
                        .into(iv_background)
                }

                tv_title.text = data.title
                tv_rating.text = data.rating.toString()
                tv_release.text = data.release_date

                if(data.overview == ""){
                    tv_overview.text = resources.getString(R.string.empty_overview)
                }else{
                    tv_overview.text = data.overview
                }
                favorite_button.setOnLikeListener(this@DetailActivity)
            }
        }
    }

    override fun liked(likeButton: LikeButton) {
        val data = intent.getParcelableExtra(EXTRA_DATA) as DataItems

        status = intent.getIntExtra(REQUEST_CODE, 0)

        if(status == 100){
            val values = contentValuesOf(
                DatabaseContract.MovieColumns._ID to data.id,
                DatabaseContract.MovieColumns.TITLE to data.title,
                DatabaseContract.MovieColumns.RATING to data.rating,
                DatabaseContract.MovieColumns.RELEASE_DATE to data.release_date,
                DatabaseContract.MovieColumns.OVERVIEW to data.overview,
                DatabaseContract.MovieColumns.POSTER to data.poster,
                DatabaseContract.MovieColumns.BACKGROUND to data.background,
                DatabaseContract.MovieColumns.DATE to getCurrentDate()
            )
            val result = contentResolver.insert(DatabaseContract.MovieColumns.CONTENT_URI, values)
            if(result?.lastPathSegment != "-1" ){
                Toast.makeText(this, R.string.success_favourite, Toast.LENGTH_SHORT).show()
                notifyMovieWidget()
            }else{
                Toast.makeText(this, R.string.failed_favourite, Toast.LENGTH_SHORT).show()
            }

        } else{
            val values = contentValuesOf(
                DatabaseContract.ShowColumns._ID to data.id,
                DatabaseContract.ShowColumns.TITLE to data.title,
                DatabaseContract.ShowColumns.RATING to data.rating,
                DatabaseContract.ShowColumns.RELEASE_DATE to data.release_date,
                DatabaseContract.ShowColumns.OVERVIEW to data.overview,
                DatabaseContract.ShowColumns.POSTER to data.poster,
                DatabaseContract.ShowColumns.BACKGROUND to data.background,
                DatabaseContract.ShowColumns.DATE to getCurrentDate()
            )
            val result = contentResolver.insert(DatabaseContract.ShowColumns.CONTENT_URI, values)
            if(result?.lastPathSegment != "-1" ){
                Toast.makeText(this, R.string.success_favourite, Toast.LENGTH_SHORT).show()
                notifyShowWidget()
            }else{
                Toast.makeText(this, R.string.failed_favourite, Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun unLiked(likeButton: LikeButton?) {
        val data = intent.getParcelableExtra(EXTRA_DATA) as DataItems
        if(status == 100){
            uriWithId = Uri.parse(DatabaseContract.MovieColumns.CONTENT_URI.toString() + "/" + data.id)
            val result = contentResolver.delete(uriWithId, null, null)
            if(result > 0){
                Toast.makeText(this, R.string.success_delete, Toast.LENGTH_SHORT).show()
                notifyMovieWidget()
            } else {
                Toast.makeText(this, R.string.failed_delete, Toast.LENGTH_SHORT).show()
            }
        }else{
            uriWithId = Uri.parse(DatabaseContract.ShowColumns.CONTENT_URI.toString() + "/" + data.id)
            val result = contentResolver.delete(uriWithId, null, null)
            if(result > 0){
                Toast.makeText(this, R.string.success_delete, Toast.LENGTH_SHORT).show()
                notifyShowWidget()
            } else {
                Toast.makeText(this, R.string.failed_delete, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getCurrentDate(): String{
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault())
        val date = Date()

        return dateFormat.format(date)
    }

    private fun showLoading(state: Boolean){
        if(state){
            pb_detail.visibility = View.VISIBLE
        }else{
            pb_detail.visibility = View.GONE
        }
    }

    private fun notifyMovieWidget(){
        val intent = Intent(this, FavouriteMovieWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        sendBroadcast(intent)
    }

    private fun notifyShowWidget(){
        val intent = Intent(this, FavouriteShowWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        sendBroadcast(intent)
    }
}
