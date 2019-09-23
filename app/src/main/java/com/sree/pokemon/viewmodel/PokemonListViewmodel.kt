package com.sree.pokemon.viewmodel

import android.app.Application
import android.os.Handler
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.sree.pokemon.helper.PokemonNameGeneratorForApi
import com.sree.pokemon.repository.PokemonRepository

class PokemonListViewmodel(application: Application):AndroidViewModel(application) {
    private val applicationContext = application
    private val pokemonRepository= PokemonRepository(applicationContext)
    private var apiHandler:Handler
    init {
        /**
         * Handler for communicating with okhttp
         */
         apiHandler = Handler {
            if (it.what == 1){
                //from pokemonNameListApi
                if (it.data.getString("status") == "success"){
                    pokemonListApiStatusLiveData.value = "success"
                }else{
                    pokemonListApiStatusLiveData.value = "failure"
                }
            }else if (it.what == 2){
                //from pokemonApi
                incrementPokemonNameGeneratorLiveDataStatus()
            }
            true
        }

    }
    /**
     * Live data's for ui update
     */
    //observe pokemon name list api call back
    private var pokemonListApiStatusLiveData: MutableLiveData<String> = MutableLiveData()
    //observe number of api generated is equal to number of response
    //for db values just increment
    @Volatile private var incrementPokemonNameGeneratorLiveDataStatus: MutableLiveData<Int> = MutableLiveData()
    @Volatile private var incrementPokemonNameGenerator=0

    private var apiCounter =0
    private var isInitialFetch = false
    private var isNormalFetch = false

    fun isInitialFetch():Boolean{
        return isInitialFetch
    }

    fun isNormalFetch():Boolean{
        return isNormalFetch
    }

    fun setInitialFetch(status: Boolean){
        isInitialFetch = status
    }

    fun setNormalFetch(status: Boolean){
        isNormalFetch = status
    }


    /**
     * name generation for api call
     */
    fun setApiCounter(count:Int){
        apiCounter =count
    }
    fun getApiCounter() = apiCounter
    //observe this for check all apis returns result
    fun incrementPokemonNameGeneratorLiveDataStatus(){
        incrementPokemonNameGenerator++
        incrementPokemonNameGeneratorLiveDataStatus.value =incrementPokemonNameGenerator
    }

    fun clearIncrementPokemonNameGeneratorLiveDataStatus(){
        incrementPokemonNameGenerator=0
        incrementPokemonNameGeneratorLiveDataStatus.value =incrementPokemonNameGenerator
    }

    fun getIncrementPokemonNameGeneratorLiveDataStatus() = incrementPokemonNameGeneratorLiveDataStatus

    fun generatePokemonNameForInitialFetch():List<String> = PokemonNameGeneratorForApi.getInstance().initialApiFetch(this)

    fun  generatePokemonNameForNormalFetch(sharedViewmodel:PokemonSharedViewmodel):List<String> = PokemonNameGeneratorForApi.getInstance().normalApiFetch(10,this,sharedViewmodel)

    /**
     * fetch from server
     */
    fun fetchPokemonNameListFromServer(){
        pokemonRepository.fetchPokemonNameListFromServer(apiHandler)
    }
    fun fetchPokemonFromServer(name:String){
        pokemonRepository.fetchPokemonFromServer("",name,apiHandler)
    }

    /**
     * From Database
     */
    fun isTableEmpty():Boolean = pokemonRepository.isTableEmpty()

    fun setPokemonListApiStatus(status:String){
        pokemonListApiStatusLiveData.value = status
    }

    fun isPokemonPresent(name: String):Boolean = pokemonRepository.isPokemonPresent(name)

    fun getPokemonListApiStatus() = pokemonListApiStatusLiveData

    fun getPokemonNameList(limit: Int ,offset:Int):List<String> = pokemonRepository.getPokemonNameList(limit,offset)
}