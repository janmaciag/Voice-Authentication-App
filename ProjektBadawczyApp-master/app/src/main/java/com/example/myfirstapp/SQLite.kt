package com.example.myfirstapp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.widget.ArrayAdapter
import android.widget.ListView


class SQLite(context:Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{

        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "UserDatabase"
        private const val TABLE_CONTACTS = "UserTable"

        private const val KEY_ID = "_id"
        private const val NAME = "name"
        private const val SURNAME = "surname"
        private const val PATHTOVOICE1 = "pathToVoice1"
        private const val PATHTOVOICE2 = "pathToVoice2"
        private const val PATHTOVOICE3 = "pathToVoice3"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTBLCONTACTS = ("CREATE TABLE " + TABLE_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," +
                                NAME + " TEXT," + SURNAME + " TEXT," + PATHTOVOICE1 + " TEXT," +
                                PATHTOVOICE2 + " TEXT," + PATHTOVOICE3 + " TEXT" + ")")
        db?.execSQL(createTBLCONTACTS)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }

    fun addUser(usr: UsrModelClass): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(NAME, usr.name)
        contentValues.put(SURNAME, usr.surname)
        contentValues.put(PATHTOVOICE1, usr.pathToVoice1)
        contentValues.put(PATHTOVOICE2, usr.pathToVoice2)
        contentValues.put(PATHTOVOICE3, usr.pathToVoice3)

        val success = db.insert(TABLE_CONTACTS, null, contentValues)

        db.close()
        return success
    }

    @SuppressLint("Range")
    fun viewUser(): ArrayList<UsrModelClass> {

        val usrList: ArrayList<UsrModelClass> = ArrayList()

        // Query to select all the records from the table.
        val selectQuery = "SELECT  * FROM $TABLE_CONTACTS"

        val db = this.readableDatabase
        // Cursor is used to read the record one by one. Add them to data model class.
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)

        } catch (e: SQLiteException) {
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var name: String
        var surname: String
        var pathToVoice1: String
        var pathToVoice2: String
        var pathToVoice3: String

        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(cursor.getColumnIndex(KEY_ID))
                name = cursor.getString(cursor.getColumnIndex(NAME))
                surname = cursor.getString(cursor.getColumnIndex(SURNAME))
                pathToVoice1 = cursor.getString(cursor.getColumnIndex(PATHTOVOICE1))
                pathToVoice2 = cursor.getString(cursor.getColumnIndex(PATHTOVOICE2))
                pathToVoice3 = cursor.getString(cursor.getColumnIndex(PATHTOVOICE3))

                val user = UsrModelClass(id = id, name = name, surname = surname, pathToVoice1 = pathToVoice1, pathToVoice2 = pathToVoice2, pathToVoice3 = pathToVoice3)
                usrList.add(user)


            } while (cursor.moveToNext())
        }
        return usrList
    }



}