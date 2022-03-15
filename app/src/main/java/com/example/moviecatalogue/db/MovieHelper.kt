package com.example.moviecatalogue.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.DATE
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion.TABLE_NAME
import com.example.moviecatalogue.db.DatabaseContract.MovieColumns.Companion._ID
import com.example.moviecatalogue.entity.Favourite
import java.sql.SQLException

class MovieHelper(context: Context) {
    private var databaseHelper: DatabaseHelper = DatabaseHelper(context)
    private lateinit var database: SQLiteDatabase

    companion object{
        private const val DATABASE_TABLE = TABLE_NAME
        private var INSTANCE: MovieHelper? = null

        fun getInstance(context: Context): MovieHelper =
            INSTANCE?: synchronized(this){
                INSTANCE ?: MovieHelper(context)
            }
    }

    @Throws(SQLException::class)
    fun open(){
        database = databaseHelper.writableDatabase
    }

    fun close(){
        databaseHelper.close()

        if(database.isOpen){
            database.close()
        }
    }

    fun queryAll(): Cursor{
        return database.query(
            DATABASE_TABLE,
            null,
            null,
            null,
            null,
            null,
            "$DATE DESC",
            null
        )
    }

    fun queryById(id: String): Cursor{
        return database.query(
            DATABASE_TABLE,
            null,
            "$_ID = ?",
            arrayOf(id),
            null,
            null,
            null,
            null
        )
    }

    fun getDataById(id: String?): ArrayList<Favourite>{
        val cursor = database.query(TABLE_NAME, null, "$_ID LIKE ?", arrayOf(id), null, null, null, null)
        cursor.moveToFirst()
        val arrayList = ArrayList<Favourite>()
        var favourite: Favourite
        if (cursor.count > 0){
            do{
                favourite = Favourite()
                favourite.id = cursor.getString(cursor.getColumnIndexOrThrow(_ID))

                arrayList.add(favourite)
                cursor.moveToNext()
            } while (!cursor.isAfterLast)
        }
        cursor.close()
        return arrayList
    }

    fun insert(values: ContentValues?): Long {
        return database.insert(DATABASE_TABLE, null, values)
    }

    fun deleteById(id: String): Int{
        return database.delete(DATABASE_TABLE, "$_ID = '$id'", null)
    }
}