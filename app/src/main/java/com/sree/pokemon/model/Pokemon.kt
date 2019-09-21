package com.sree.pokemon.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "pokemons")
data class Pokemon(@PrimaryKey var id: Int,
                   var number: Int,
                   var name: String,
                   var specialPower:ArrayList<String>?,
                    var resistant:ArrayList<String>?,
                   var image:String?,
                   var evolution:ArrayList<Pokemon>?
                   ){

    constructor(id:Int,number:Int,name:String):this(id,number,name,null,null,null,null)

}
