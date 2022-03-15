package com.example.moviecatalogue.db

import android.net.Uri
import android.provider.BaseColumns

object DatabaseContract {

    const val AUTHORITY = "com.example.moviecatalogue"
    const val SCHEME = "content"

    class MovieColumns : BaseColumns{
        companion object{
            const val TABLE_NAME = "movie"
            const val _ID = "_id"
            const val TITLE = "title"
            const val RATING = "rating"
            const val RELEASE_DATE = "release_date"
            const val OVERVIEW = "overview"
            const val POSTER = "poster"
            const val BACKGROUND = "background"
            const val DATE = "date"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }

    class ShowColumns : BaseColumns{
        companion object{
            const val TABLE_NAME = "tvshow"
            const val _ID = "_id"
            const val TITLE = "title"
            const val RATING = "rating"
            const val RELEASE_DATE = "release_date"
            const val OVERVIEW = "overview"
            const val POSTER = "poster"
            const val BACKGROUND = "background"
            const val DATE = "date"

            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(TABLE_NAME)
                .build()
        }
    }
}