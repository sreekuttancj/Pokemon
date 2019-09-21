package com.sree.pokemon.repository

import android.app.Application
import android.util.Log
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.PokemonByNameOrIDQuery
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.sree.pokemon.nework.PokemonApplication
import org.jetbrains.annotations.NotNull

class PokemonRepository(application: Application) {

    fun fetchPokemonFromServer(id:String,name:String){

        PokemonApplication.getApolloClient().query(
            PokemonByNameOrIDQuery.builder()
                .id(id)
                .name(name)
                .build()
        )
            .enqueue(object : ApolloCall.Callback<PokemonByNameOrIDQuery.Data>() {

                override fun onResponse(@NotNull response: Response<PokemonByNameOrIDQuery.Data>) {
                    Log.d("list_pokemon", "Response: " + response.data())
                }

                override fun onFailure(@NotNull e: ApolloException) {

                    Log.d("list_pokemon", "Exception " + e.message, e)
                }
            })
    }
}