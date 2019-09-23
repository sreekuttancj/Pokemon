package com.sree.pokemon.repository

import android.app.Application
import android.os.Handler
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.PokemonByNameOrIDQuery
import com.apollographql.apollo.PokemonListQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.sree.pokemon.database.AppDatabase
import com.sree.pokemon.database.AppDatabase_Impl
import com.sree.pokemon.model.Pokemon
import com.sree.pokemon.nework.PokemonApplication
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel
import org.jetbrains.annotations.NotNull
import android.R.attr.bitmap
import android.os.Bundle
import androidx.lifecycle.LiveData
import com.google.gson.JsonArray
import com.sree.pokemon.database.EvolutionEntity
import com.sree.pokemon.viewmodel.PokemonListViewmodel


class PokemonRepository(application: Application) {

    private var appDatabase = AppDatabase.getInstance(application)
    private val UPDATE_POKEMONLIST_API_STATUS = 1
    private val UPDATE_POKEMONDATA_API_STATUS = 2
    private val UPDATE_POKEMON_EVOLUTION_DATA_API_STATUS = 3

    /**
     * For Api call
     */
    fun fetchPokemonNameListFromServer(apiHandler: Handler){
        Log.i("name_list_pokemon", "api called")

        //total count is 151 so fetch all of them
        PokemonApplication.getApolloClient().query(
            PokemonListQuery.builder().first(151)
                .build()
        ).enqueue( object :ApolloCall.Callback<PokemonListQuery.Data>(){
            override fun onResponse(response: Response<PokemonListQuery.Data>) {
                //insert data to db
                val pokemonListQuery = response.data()?.pokemons()
                val pokemonNameList:ArrayList<Pokemon> = ArrayList()
                pokemonListQuery?.forEach {
                    val pokemon = Pokemon(
                        it.id(),
                        it.number()!!,
                        it.name()!!
                    )
                    pokemonNameList.add(pokemon)
                }
                insertPokemonNameList(pokemonNameList)
                //for observing response
                val msg = apiHandler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("status","success")
                msg.what = UPDATE_POKEMONLIST_API_STATUS
                msg.data =bundle
                apiHandler.sendMessage(msg)

                Log.d("list_pokemon", "Response: size: ${pokemonNameList.size}" + response.data())
            }
            override fun onFailure(e: ApolloException) {
                //for observing response
                val msg = apiHandler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("status","failure")
                msg.what = UPDATE_POKEMONLIST_API_STATUS
                msg.data =bundle
                apiHandler.sendMessage(msg)
                Log.d("list_pokemon", "Failure: " + e.message)
            }
        })
    }
    fun fetchPokemonFromServer(id:String,name:String,apiHandler: Handler){
        Log.i("list_pokemon", "api called: $name")

        PokemonApplication.getApolloClient().query(
            PokemonByNameOrIDQuery.builder()
                .id(id)
                .name(name)
                .build()
        )
            .enqueue(object : ApolloCall.Callback<PokemonByNameOrIDQuery.Data>() {

                override fun onResponse(@NotNull response: Response<PokemonByNameOrIDQuery.Data>) {
                    val pokemonQuery = response.data()?.pokemon()
                    val pokemonSpecialList = ArrayList<String>()
                    val pokemonResistantList = ArrayList<String>()
                    val pokemonEvolutionList = ArrayList<String>()
                if (response.data()!=null) {
                    //add special power
                    response.data()?.pokemon()?.attacks()?.special()?.forEach {
                        pokemonSpecialList.add(it.type()!!)
                    }
                    //add resistant
                    response.data()?.pokemon()?.resistant()?.forEach {
                        pokemonResistantList.add(it!!)
                    }
                    //add evolution
                    response.data()?.pokemon()?.evolutions()?.forEach {
                        val evolution = EvolutionEntity()
                        evolution.id = it.id()
                        evolution.number = it.number()!!
                        evolution.name = it.name()!!
                        evolution.parentName = response.data()?.pokemon()?.name()!!
                        evolution.image = it.image()

                        pokemonEvolutionList.add(it.name()!!)
                        //add evolution to db
                        insertEvolution(evolution)
                    }
                    pokemonQuery.let {
                        val pokemon: Pokemon = Pokemon(
                            it!!.id(),
                            it.number()!!,
                            it.name()!!,
                            pokemonSpecialList,
                            pokemonResistantList,
                            it.image(),
                            pokemonEvolutionList,
                            true
                        )
                        //insert data to db
                        insertPokemon(pokemon)
                    }

                    //for observing response
                    val msg = apiHandler.obtainMessage()
                    msg.what = UPDATE_POKEMONDATA_API_STATUS
                    apiHandler.sendMessage(msg)
                    Log.d("list_pokemon", "Response: " + response.data())
                }else{
                    Log.i("list_pokemon", "empty: " + response.data())

                }
                }

                override fun onFailure(@NotNull e: ApolloException) {
                    //for observing response
                    val msg = apiHandler.obtainMessage()
                    msg.what = UPDATE_POKEMONDATA_API_STATUS
                    apiHandler.sendMessage(msg)
                    Log.d("list_pokemon", "Failure: " + e.message)
                }
            })
    }
    fun fetchPokemonForEvoulutionFromServer(id:String,name:String,apiHandler: Handler){
        Log.i("list_pokemon_evo", "api called: $name")

        PokemonApplication.getApolloClient().query(
            PokemonByNameOrIDQuery.builder()
                .id(id)
                .name(name)
                .build()
        )
            .enqueue(object : ApolloCall.Callback<PokemonByNameOrIDQuery.Data>() {

                override fun onResponse(@NotNull response: Response<PokemonByNameOrIDQuery.Data>) {
                    val pokemonQuery = response.data()?.pokemon()
                    val pokemonSpecialList = ArrayList<String>()
                    val pokemonResistantList = ArrayList<String>()
                    val pokemonEvolutionList = ArrayList<String>()
                    //add special power
                    response.data()?.pokemon()?.attacks()?.special()?.forEach {
                        pokemonSpecialList.add(it.type()!!)
                    }
                    //add resistant
                    response.data()?.pokemon()?.resistant()?.forEach {
                        pokemonResistantList.add(it!!)
                    }
                    //add evolution
                    response.data()?.pokemon()?.evolutions()?.forEach {
                        val evolution = EvolutionEntity()
                        evolution.id =it.id()
                        evolution.number =it.number()!!
                        evolution.name =it.name()!!
                        evolution.parentName =response.data()?.pokemon()?.name()!!
                        evolution.image =it.image()

                        pokemonEvolutionList.add(it.name()!!)
                        //add evolution to db
                        insertEvolution(evolution)
                    }
                    pokemonQuery.let {
                        val pokemon:Pokemon = Pokemon(
                            it!!.id(),
                            it.number()!!,
                            it.name()!!,
                            pokemonSpecialList,
                            pokemonResistantList,
                            it.image(),
                            pokemonEvolutionList,
                            true
                        )
                        //insert data to db
                        insertPokemon(pokemon)
                    }

                    //for observing response
                    val msg = apiHandler.obtainMessage()
                    msg.what = UPDATE_POKEMON_EVOLUTION_DATA_API_STATUS
                    apiHandler.sendMessage(msg)
                    Log.d("list_pokemon_evo", "Response: " + response.data())
                }

                override fun onFailure(@NotNull e: ApolloException) {
                    //for observing response
                    val bundle = Bundle()
                    bundle.putString("status","failure")
                    val msg = apiHandler.obtainMessage()
                    msg.what = UPDATE_POKEMON_EVOLUTION_DATA_API_STATUS
                    msg.data = bundle
                    apiHandler.sendMessage(msg)
                    Log.d("list_pokemon_evo", "Failure: " + e.message)
                }
            })
    }


    /**
     * For Database
     */
    fun isTableEmpty():Boolean{
        val count = appDatabase.pokemonDao().isTableEmpty()
        return count<=0
    }

    fun insertPokemonNameList(pokemonList: List<Pokemon>){
        appDatabase.pokemonDao().insertPokemonNameList(pokemonList)
    }

    fun insertPokemon(pokemon: Pokemon){
        appDatabase.pokemonDao().insertPokemon(pokemon)
    }

    fun isPokemonPresent(name: String):Boolean{
        val count = appDatabase.pokemonDao().isPokemonPresent(name)
        if (count>0){
            return true
        }else
            return false
    }

    fun getPokemonNameList(limit: Int ,offset:Int):List<String> = appDatabase.pokemonDao().getPokemonNameList(limit,offset)

    fun getPokemonListLiveDataFromDB(limit: Int,offset: Int):List<Pokemon>{
        return appDatabase.pokemonDao().getPokemonList(limit,offset)
    }
    //add evolution to db
    fun insertEvolution(evolution:EvolutionEntity){
        appDatabase.evolutionDao().insertEvolution(evolution)
    }

    fun getPokemonByName(name:String):Pokemon{
        return appDatabase.pokemonDao().getPokemonByName(name)
    }
    fun getEvolution(name:String):List<EvolutionEntity> = appDatabase.evolutionDao().getEvolution(name)
}