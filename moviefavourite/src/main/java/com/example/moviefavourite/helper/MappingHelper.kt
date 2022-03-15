package com.example.moviefavourite.helper

import android.database.Cursor
import com.example.moviefavourite.db.DatabaseContract
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.BACKGROUND
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.DATE
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.OVERVIEW
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.POSTER
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.RATING
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.RELEASE_DATE
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion.TITLE
import com.example.moviefavourite.db.DatabaseContract.MovieColumns.Companion._ID
import com.example.moviefavourite.entity.Favourite

object MappingHelper {

    fun mapCursorToArrayListMovie(cursor: Cursor?): ArrayList<Favourite> {
        val favoriteList = ArrayList<Favourite>()
        cursor?.apply {
            while (moveToNext()) {
                val id = getString(cursor.getColumnIndexOrThrow(_ID))
                val title = getString(cursor.getColumnIndexOrThrow(TITLE))
                val rating = getString(cursor.getColumnIndexOrThrow(RATING))
                val releaseDate = getString(cursor.getColumnIndexOrThrow(RELEASE_DATE))
                val overview = getString(cursor.getColumnIndexOrThrow(OVERVIEW))
                val poster = getString(cursor.getColumnIndexOrThrow(POSTER))
                val background = getString(cursor.getColumnIndexOrThrow(BACKGROUND))
                val date = getString(cursor.getColumnIndexOrThrow(DATE))
                favoriteList.add(
                    Favourite(
                        id,
                        title,
                        rating,
                        releaseDate,
                        overview,
                        poster,
                        background,
                        date
                    )
                )
            }
        }
        return favoriteList
    }

    fun mapCursorToArrayListShow(cursor: Cursor?): ArrayList<Favourite> {
        val favoriteList = ArrayList<Favourite>()
        cursor?.apply {
            while (moveToNext()) {
                val id = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns._ID))
                val title = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.TITLE))
                val rating = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.RATING))
                val releaseDate = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.RELEASE_DATE))
                val overview = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.OVERVIEW))
                val poster = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.POSTER))
                val background = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.BACKGROUND))
                val date = getString(cursor.getColumnIndexOrThrow(DatabaseContract.ShowColumns.DATE))
                favoriteList.add(
                    Favourite(
                        id,
                        title,
                        rating,
                        releaseDate,
                        overview,
                        poster,
                        background,
                        date
                    )
                )
            }
        }
        return favoriteList
    }
}
