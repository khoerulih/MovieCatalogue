package com.example.moviecatalogue.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.moviecatalogue.db.DatabaseContract
import com.example.moviecatalogue.db.DatabaseContract.AUTHORITY
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.CONTENT_URI
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.TABLE_NAME
import com.example.moviecatalogue.db.MovieHelper
import com.example.moviecatalogue.db.TvShowHelper

class FavouriteProvider : ContentProvider() {

    companion object{
        private const val MOVIE = 1
        private const val MOVIE_ID = 2
        private lateinit var movieHelper: MovieHelper

        private const val SHOW = 3
        private const val SHOW_ID = 4
        private lateinit var showHelper: TvShowHelper

        private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            sUriMatcher.addURI(AUTHORITY, TABLE_NAME, MOVIE)

            sUriMatcher.addURI(AUTHORITY, "$TABLE_NAME/#", MOVIE_ID)

            sUriMatcher.addURI(AUTHORITY, DatabaseContract.ShowColumns.TABLE_NAME, SHOW)

            sUriMatcher.addURI(AUTHORITY, "${DatabaseContract.ShowColumns.TABLE_NAME}/#", SHOW_ID)
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val deleted: Int = when(sUriMatcher.match(uri)){
            MOVIE_ID -> movieHelper.deleteById(uri.lastPathSegment.toString())
            SHOW_ID -> showHelper.deleteById(uri.lastPathSegment.toString())
            else -> 0
        }

        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        context?.contentResolver?.notifyChange(DatabaseContract.ShowColumns.CONTENT_URI, null)

        return deleted
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val added: Long = when (sUriMatcher.match(uri)){
            MOVIE -> movieHelper.insert(values)
            SHOW -> showHelper.insert(values)
            else -> 0
        }
        context?.contentResolver?.notifyChange(CONTENT_URI, null)
        context?.contentResolver?.notifyChange(DatabaseContract.ShowColumns.CONTENT_URI, null)


        return Uri.parse("$CONTENT_URI/$added")
    }

    override fun onCreate(): Boolean {
        movieHelper = MovieHelper.getInstance(context as Context)
        movieHelper.open()

        showHelper = TvShowHelper.getInstance(context as Context)
        showHelper.open()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val cursor: Cursor?
        when(sUriMatcher.match(uri)){
            MOVIE -> cursor = movieHelper.queryAll()
            MOVIE_ID -> cursor = movieHelper.queryById(uri.lastPathSegment.toString())

            SHOW -> cursor = showHelper.queryAll()
            SHOW_ID -> cursor = showHelper.queryById(uri.lastPathSegment.toString())
            else -> cursor = null
        }
        return cursor
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }
}
