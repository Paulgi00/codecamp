package com.example.wildidle.di

import android.content.Context
import androidx.room.Room
import com.example.wildidle.room.BoostDao
import com.example.wildidle.room.GameValueDao
import com.example.wildidle.room.ProducerDao
import com.example.wildidle.room.RoomDatabases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        RoomDatabases::class.java,
        "Database"
    ).build()

    @Provides
    fun provideGameValueDao(db: RoomDatabases): GameValueDao {
        return db.gameValueDao
    }

    @Provides
    fun provideProducerDao(db: RoomDatabases): ProducerDao {
        return db.producerDao
    }

    @Provides
    fun provideBoostDao(db: RoomDatabases): BoostDao {
        return db.boostDao
    }

}