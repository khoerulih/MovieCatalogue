package com.example.moviecatalogue.widget

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.os.Binder
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.moviecatalogue.R
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.example.moviecatalogue.entity.Favourite
import com.example.moviecatalogue.helper.MappingHelper

internal class FavouriteMovieRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private var mWidgetItems = ArrayList<Favourite>()
    private var cursor: Cursor? = null

    override fun onDataSetChanged() {
        val identityToken = Binder.clearCallingIdentity()

        cursor = context.contentResolver.query(CONTENT_URI, null, null, null, null)
        mWidgetItems = MappingHelper.mapCursorToArrayListMovie(cursor)

        Binder.restoreCallingIdentity(identityToken)
    }

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(context.packageName, R.layout.widget_item)
        try{
            val poster : Bitmap =
                Glide.with(context)
                .asBitmap()
                .load(mWidgetItems[position].poster)
                .submit(550,400)
                .get()

            rv.setImageViewBitmap(R.id.imageView, poster)
        }catch (e: Exception){
            e.printStackTrace()
        }

        val extras = bundleOf(
            FavouriteMovieWidget.EXTRA_ITEM to mWidgetItems[position].title
        )

        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv

    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = 0

    override fun hasStableIds(): Boolean = false

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewTypeCount(): Int = 1

    override fun onCreate() {}

    override fun onDestroy() {}
}