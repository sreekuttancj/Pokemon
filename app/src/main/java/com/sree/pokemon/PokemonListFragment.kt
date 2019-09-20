package com.sree.pokemon

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.sree.pokemon.dummy.DummyContent
import com.sree.pokemon.dummy.DummyContent.DummyItem
import com.sree.pokemon.helper.DividerItemDecoration

/**
 * A fragment representing a list of pokemon.
 * Activities containing this fragment MUST implement the
 * [PokemonListFragment.OnListFragmentInteractionListener] interface.
 */
class PokemonListFragment : Fragment() {

    private var columnCount = 2
    private var listener: OnListFragmentInteractionListener? = null

    private lateinit var pokemonRecyclerViewAdapter: PokemonRecyclerViewAdapter

    private var totalItemCount =-1
    private var lastVisibleItem =-1
    private val visibleThreshold = 6

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_pokemon_list, container, false)
        val gridLayoutManager = GridLayoutManager(context, columnCount)
        //first item take combine the two span
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
        pokemonRecyclerViewAdapter = PokemonRecyclerViewAdapter(DummyContent.ITEMS, listener)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                addItemDecoration(DividerItemDecoration(8))
                layoutManager = gridLayoutManager
                adapter = pokemonRecyclerViewAdapter
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
        return view
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
        fun onListFragmentInteraction(item: DummyItem?)
    }
}
