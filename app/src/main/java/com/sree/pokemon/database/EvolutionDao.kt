package com.sree.pokemon.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface EvolutionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvolution(evolution:EvolutionEntity)

    @Query("SELECT * FROM evolution WHERE parentName =:parentName GROUP BY name ORDER BY number ASC")
    fun getEvolution(parentName:String):List<EvolutionEntity>
}