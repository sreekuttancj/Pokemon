package com.sree.pokemon.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sree.pokemon.repository.PokemonRepository

class PokemonSharedViewmodel(application: Application): AndroidViewModel(application){
    private val pokemonRepository= PokemonRepository(application)

    fun fetchPokemonFromServer(){
        pokemonRepository.fetchPokemonFromServer("","Bulbasaur")
    }
}