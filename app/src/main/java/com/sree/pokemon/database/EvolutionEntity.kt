package com.sree.pokemon.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evolution")
data class EvolutionEntity(@PrimaryKey(autoGenerate = true)
                           var evolutionID:Int,
                           var id: String,
                           var number: String?,
                           var name: String?,
                           var parentName:String,
                           var image:String?
                           ){
    constructor():this(0,"","","","","")
}