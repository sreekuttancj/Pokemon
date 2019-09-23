package com.sree.pokemon.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sree.pokemon.model.Pokemon

@Dao
interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemonNameList(pokemonList: List<Pokemon>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemon(pokemon: Pokemon)

    @Query("SELECT COUNT(*) FROM pokemons")
    fun isTableEmpty():Int

    @Update
    fun updatePokemon(pokemon: Pokemon)

    @Query("SELECT COUNT(name) from pokemons WHERE name = :name AND fetchStatus = 1")
    fun isPokemonPresent(name:String):Int

    @Query("SELECT name FROM pokemons ORDER BY number ASC LIMIT :limit OFFSET :offset ")
    fun getPokemonNameList(limit: Int ,offset:Int):List<String>

    @Query("SELECT * FROM pokemons WHERE fetchStatus = 1 ORDER BY number ASC LIMIT :limit OFFSET :offset")
    fun getPokemonList(limit: Int ,offset:Int):List<Pokemon>

    @Query("SELECT * FROM pokemons WHERE fetchStatus = 1 AND name = :name")
    fun getPokemonByName(name: String):Pokemon
}