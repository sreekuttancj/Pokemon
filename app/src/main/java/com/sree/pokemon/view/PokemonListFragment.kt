package com.sree.pokemon.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProviders
import com.sree.pokemon.R
import com.sree.pokemon.helper.DividerItemDecoration
import com.sree.pokemon.helper.PokemonRecyclerViewAdapter
import com.sree.pokemon.viewmodel.PokemonSharedViewmodel

/**
 * A fragment representing a list of pokemon.
 * Activities containing this fragment MUST implement the
 * [PokemonListFragment.OnListFragmentInteractionListener] interface.
 */
class PokemonListFragment : Fragment() {

    private var columnCount = 2
    private var listener: OnListFragmentInteractionListener? = null
    private lateinit var pokemonSharedViewmodel:PokemonSharedViewmodel

    private var totalItemCount =-1
    private var lastVisibleItem =-1
    private val visibleThreshold = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity.let {
            pokemonSharedViewmodel = ViewModelProviders.of(it!!).get(PokemonSharedViewmodel::class.java)
        }
        pokemonSharedViewmodel.fetchPokemonFromServer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pokemon_list, container, false)
        setUpView(view)
        return view
    }

    private fun setUpView(view: View){
        val gridLayoutManager = GridLayoutManager(context, columnCount)
        //last item is footer
        // so need to combine grid,
        // if content size is odd then last item need to combine grid
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                //first and last item has combined span
                return when (position) {
                    gridLayoutManager.itemCount - 1 -> 2
                    gridLayoutManager.itemCount - 2 -> {
                        if (gridLayoutManager.itemCount % 2 == 0)
                            return 2
                        else
                            1
                    }
                    else -> 1
                }
            }
        }
        val pokemonRecyclerViewAdapter = PokemonRecyclerViewAdapter(listener)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                addItemDecoration(DividerItemDecoration(8))
                layoutManager = gridLayoutManager
                adapter = pokemonRecyclerViewAdapter
                setHasFixedSize(true)
                addOnScrollListener(object : RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)
                        totalItemCount = gridLayoutManager.itemCount
                        lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition()
                        if (!pokemonRecyclerViewAdapter.loadingStatus() && totalItemCount > 1 && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            pokemonRecyclerViewAdapter.onLoadMore()
                        }
                    }
                }) }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     */
    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(position:Int)
    }
}
