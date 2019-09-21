package com.sree.pokemon.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sree.pokemon.model.Pokemon

@Database(entities = [Pokemon::class],version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao

    companion object{
        @Volatile
        var INSTANCE: AppDatabase? =null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pokemon")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
    }
}