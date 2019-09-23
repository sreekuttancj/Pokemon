package com.sree.pokemon.viewmodel

import android.app.Application
import android.os.Handler
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sree.pokemon.database.EvolutionEntity
import com.sree.pokemon.model.Pokemon
import com.sree.pokemon.repository.PokemonRepository

class PokemonDetailsViewmodel(application: Application):AndroidViewModel(application) {
    private val pokemonRepository = PokemonRepository(application)
    private var pokemonForEvolutionApiStatusLiveData: MutableLiveData<String> = MutableLiveData()

    /**
     * Handler for communicating with okhttp
     */
    private var apiHandler = Handler {
        if (it.what == 3){
            //from pokemonForEvolution
            if (it.data.getString("status") == "success"){
                pokemonForEvolutionApiStatusLiveData.value = "success"
            }else{
                pokemonForEvolutionApiStatusLiveData.value = "failure"
            }
        }
        true
    }

    private lateinit var parentName:String

    //for tracking evolution correctly
    fun setParentName(name:String){
        parentName =name
    }

    fun getPokemonForEvolutionApiStatusLiveData():MutableLiveData<String>{
        return pokemonForEvolutionApiStatusLiveData
    }
    /**
     * For Api call
     */

    fun fetchPokemonForEvolutionFromServer(name: String){
        pokemonRepository.fetchPokemonForEvoulutionFromServer("",name,apiHandler)
    }

    /**
     * For Database
     */

    fun getEvolution():List<EvolutionEntity> = pokemonRepository.getEvolution(parentName)

    fun isPokemonPresent(name: String):Boolean = pokemonRepository.isPokemonPresent(name)

    fun getPokemonByName(name:String):Pokemon = pokemonRepository.getPokemonByName(name)
}