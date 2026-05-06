package com.gabpawang.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE workout_sessions ADD COLUMN setHistory TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE workout_sessions ADD COLUMN mode TEXT NOT NULL DEFAULT ''")
    }
}

@Database(entities = [WorkoutSessionEntity::class], version = 3, exportSchema = false)
abstract class GabpaDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
}
