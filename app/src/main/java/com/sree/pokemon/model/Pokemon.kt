package com.sree.pokemon.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "pokemons")
data class Pokemon(@PrimaryKey var id: String,
                   var number: String?,
                   var name: String?,
                   var specialPower:ArrayList<String>?,
                    var resistant:ArrayList<String>?,
                   var image:String?,
                   var evolution:ArrayList<String>?,
                    var fetchStatus:Boolean
                   ){

    constructor(id:String,number:String,name:String):this(id,number,name,null,null,null,null,false)

}
