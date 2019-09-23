package com.sree.pokemon.viewmodel

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sree.pokemon.model.Pokemon
import com.sree.pokemon.repository.PokemonRepository
import java.io.IOException

/**
 * PokemonSharedViewmodel is shared by
 * PokemonListFragmenent and PokemonDetailsFragment
 */
class PokemonSharedViewmodel(application: Application): AndroidViewModel(application){

    val applicationContext = application
    private val pokemonRepository= PokemonRepository(applicationContext)
     /**
     * Live data's for ui update
     */
     //observe for ui updation
     var pokemonListLiveData:MutableLiveData<List<Pokemon>> = MutableLiveData()
    private var pokemonList: ArrayList<Pokemon> = ArrayList()

    /**
     * load pokemon list from db
     * add that list into live data for observing the change and update the recycler view
     */
    fun loadPokemonListFromDB(from:String){
        val limit:Int
        val offset:Int
        if (from=="initial_fetch"){
            limit = 20
            offset = 0
        }else {
            limit = 10
            offset = pokemonList[pokemonList.size-1].number!!.toInt()
        }
        pokemonList.addAll(pokemonRepository.getPokemonListLiveDataFromDB(limit,offset))
        //update live data
        pokemonListLiveData.value= pokemonList
    }

    fun getPokmonListLiveData():LiveData<List<Pokemon>> = pokemonListLiveData

    fun getPokemonList() = pokemonList

    fun getPokemonByPosition(position:Int) = pokemonList[position]
    //save images to internal storage once it fetch from server
    //no more call for api once an image is fetch from server
    fun saveImageToInternalStorage(fileName: String,image: Bitmap){
        try{
            applicationContext.openFileOutput(fileName, MODE_PRIVATE).use{
                image.compress(Bitmap.CompressFormat.PNG,100,it)
                it.close()
            }
        }catch (e: IOException){
            Log.e("saveToInternalStorage()", e.message)
        }
    }
    //check internal storage for pokemon images
    fun checkInternalDirForImage(fileName: String): Boolean{
        val filePath = applicationContext.getFileStreamPath(fileName)
        return filePath.exists()
    }
    //convert filename to uri
    fun getImageUri(fileName: String):Uri{
        val filePath = applicationContext.getFileStreamPath(fileName)
        return Uri.fromFile(filePath)
    }

}