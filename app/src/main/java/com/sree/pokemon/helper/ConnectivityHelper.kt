package com.sree.pokemon.helper

import android.content.Context
import android.net.ConnectivityManager

class ConnectivityHelper{

    companion object{
        fun isConnectedToNetwork(context: Context): Boolean{
            val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            var isConnected = false
                val activeNetwork = connectivityManager.activeNetworkInfo
                isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting

            return isConnected
        }
    }
}