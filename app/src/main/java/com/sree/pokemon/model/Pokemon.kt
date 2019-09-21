package com.sree.pokemon.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemons")
data class Pokemon(@PrimaryKey var id: Int,
                   var number: Int,
                   var name: String,
                   var specialPower:List<String>?,
                    var resistant:List<String>?,
                   var image:String?,
                   var evolution:List<Pokemon>?

                   ){

    constructor(id:Int,number:Int,name:String):this(id,number,name,null,null,null,null)
}
