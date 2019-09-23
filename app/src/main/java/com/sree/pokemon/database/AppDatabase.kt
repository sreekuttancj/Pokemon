package com.sree.pokemon.database

import android.content.Context
import androidx.room.*
import com.sree.pokemon.model.Pokemon

@Database(entities = [Pokemon::class,EvolutionEntity::class],version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun pokemonDao(): PokemonDao
    abstract fun evolutionDao():EvolutionDao

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