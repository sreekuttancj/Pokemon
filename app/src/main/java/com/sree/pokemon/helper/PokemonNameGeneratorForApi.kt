package com.sree.pokemon.helper

import android.util.Log
import com.sree.pokemon.viewmodel.PokemonListViewmodel
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

class PokemonNameGeneratorForApi {
    companion object{
        var numberKey: Int = 0
        @Volatile
        var INSTANCE: PokemonNameGeneratorForApi? = null

        fun getInstance(): PokemonNameGeneratorForApi =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: PokemonNameGeneratorForApi()
            }
    }

    /**
     * add names to array for fetching data for that particular pokemon
     * if it already in db no need to add it to list
     * if any api calls fail before that name will not be in db so
     *  add to list again
     */
    fun initialApiFetch(pokemonListViewmodel: PokemonListViewmodel): List<String>{
        val generateNameList = ArrayList<String>()
        val pokemonNameList = pokemonListViewmodel.getPokemonNameList(20,0)
        pokemonListViewmodel.setApiCounter(20)
        //clear before staring generation for a fresh start
        pokemonListViewmodel.clearIncrementPokemonNameGeneratorLiveDataStatus()

        pokemonNameList.forEach {
            if (pokemonListViewmodel.isPokemonPresent(it)){
                //update live data for api call simulation
                pokemonListViewmodel.incrementPokemonNameGeneratorLiveDataStatus()
            }else{
                //first time data will be empty
                generateNameList.add(it)
            }
        }
        return generateNameList
    }

    /**
     * @param count = 10 for normal fetch,20 for prefetch
     */
    fun normalApiFetch(count: Int,pokemonListViewmodel: PokemonListViewmodel,sharedViewmodel:PokemonSharedViewmodel):List<String>{
        numberKey = sharedViewmodel.getPokemonList()[sharedViewmodel.getPokemonList().size - 1].number!!.toInt()
        val generateNameList = ArrayList<String>()
        val pokemonNameList = pokemonListViewmodel.getPokemonNameList(count, numberKey)
        pokemonListViewmodel.setApiCounter(count)
        //clear before staring generation for a fresh start
        pokemonListViewmodel.clearIncrementPokemonNameGeneratorLiveDataStatus()

        pokemonNameList.forEach {
            if (pokemonListViewmodel.isPokemonPresent(it)){
                pokemonListViewmodel.incrementPokemonNameGeneratorLiveDataStatus()
            }else{
                generateNameList.add(it)
            }
        }
        return generateNameList
    }
}