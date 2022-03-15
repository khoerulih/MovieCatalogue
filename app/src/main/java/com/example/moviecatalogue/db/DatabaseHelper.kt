package com.example.moviecatalogue.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

internal class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "dbmoviecatalogue"

        private const val DATABASE_VERSION = 1

        private const val SQL_CREATE_TABLE_MOVIE = "CREATE TABLE ${DatabaseContract.MovieColumns.TABLE_NAME}" +
                "(${DatabaseContract.MovieColumns._ID} INTEGER PRIMARY KEY," +
                "${DatabaseContract.MovieColumns.TITLE} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.RATING} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.RELEASE_DATE} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.OVERVIEW} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.POSTER} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.BACKGROUND} TEXT NOT NULL," +
                "${DatabaseContract.MovieColumns.DATE} TEXT NOT NULL)"

        private const val SQL_CREATE_TABLE_TVSHOW = "CREATE TABLE ${DatabaseContract.ShowColumns.TABLE_NAME}" +
                "(${DatabaseContract.ShowColumns._ID} INTEGER PRIMARY KEY," +
                "${DatabaseContract.ShowColumns.TITLE} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.RATING} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.RELEASE_DATE} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.OVERVIEW} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.POSTER} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.BACKGROUND} TEXT NOT NULL," +
                "${DatabaseContract.ShowColumns.DATE} TEXT NOT NULL)"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE_MOVIE)
        db.execSQL(SQL_CREATE_TABLE_TVSHOW)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.MovieColumns.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${DatabaseContract.ShowColumns.TABLE_NAME}")
        onCreate(db)
    }


}