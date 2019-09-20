package com.sree.pokemon

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import com.sree.pokemon.view.PokemonDetailsFragment
import com.sree.pokemon.view.PokemonListFragment

class MainActivity : AppCompatActivity(), PokemonListFragment.OnListFragmentInteractionListener,
PokemonDetailsFragment.OnDetailsFragmentInteractionListener{

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpView()
    }

    private fun setUpView(){
        toolbar =findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onListFragmentInteraction(position: Int) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_pokemonListFragment_to_pokemonDetailsFragment)
    }

    override fun onDetailsFragmentInteraction(position: Int) {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_pokemonDetailsFragment_self)
    }
}
