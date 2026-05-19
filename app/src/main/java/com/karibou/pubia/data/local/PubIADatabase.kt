package com.karibou.pubia.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AdProjectEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class PubIADatabase : RoomDatabase() {
    abstract fun adProjectDao(): AdProjectDao

    companion object {
        const val DATABASE_NAME = "pubia_karibou.db"
    }
}
