package com.karibou.pubia.di

import android.content.Context
import androidx.room.Room
import com.karibou.pubia.data.local.AdProjectDao
import com.karibou.pubia.data.local.PubIADatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module Hilt pour le stockage local Room.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providePubIADatabase(@ApplicationContext context: Context): PubIADatabase =
        Room.databaseBuilder(
            context,
            PubIADatabase::class.java,
            PubIADatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration() // Phase prototype — schemas migrent par recreation
         .build()

    @Provides
    fun provideAdProjectDao(db: PubIADatabase): AdProjectDao = db.adProjectDao()
}
