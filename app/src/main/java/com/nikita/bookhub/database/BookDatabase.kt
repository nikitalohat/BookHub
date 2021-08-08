package com.nikita.bookhub.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BookEntity::class], version = 1)          //version is always a natural number
abstract class BookDatabase: RoomDatabase() {

    abstract fun bookDao() : BookDao

}