package com.sree.pokemon.nework

import android.app.Application
import com.apollographql.apollo.ApolloClient
import okhttp3.OkHttpClient

class PokemonApplication: Application() {
    private val BASE_URL = "https://graphql-pokemon.now.sh"


    override fun onCreate() {
        super.onCreate()
        val okHttpClient = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder().method(original.method(),
                    original.body())
                chain.proceed(builder.build())
            }
            .build()

        apolloClient = ApolloClient.builder()
            .serverUrl(BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }
    companion object{
        private lateinit var apolloClient: ApolloClient

        fun getApolloClient(): ApolloClient {
            return apolloClient
        }
    }


}