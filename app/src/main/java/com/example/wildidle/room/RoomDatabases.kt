package com.example.wildidle.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [GameValues::class, Producer::class, Boost::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BigDecimalConverter::class)
abstract class RoomDatabases : RoomDatabase() {
    abstract val gameValueDao: GameValueDao

    abstract val producerDao: ProducerDao

    abstract val boostDao: BoostDao
}