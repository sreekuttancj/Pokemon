package com.sree.pokemon.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sree.pokemon.model.Pokemon

class Converters {

        @TypeConverter
        fun fromString(value: String): ArrayList<String>? {
            val listType = object : TypeToken<ArrayList<String>>() {
            }.type
            return Gson().fromJson(value, listType)
        }

        @TypeConverter
        fun fromArrayList(list: ArrayList<String>): String {
            val gson = Gson()
            return gson.toJson(list)
        }

        @TypeConverter
        fun fromObjectList(pokemonList:ArrayList<Pokemon>):String{
            val gson = Gson()
            return gson.toJson(pokemonList)
        }

        @TypeConverter
        fun fromStringPokemon(value: String): ArrayList<Pokemon>?{
            val listType = object : TypeToken<ArrayList<Pokemon>>(){}.type
            return Gson().fromJson(value, listType)
        }
}