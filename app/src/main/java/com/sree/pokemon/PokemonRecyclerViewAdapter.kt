package com.sree.pokemon

import android.os.Handler
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView


import com.sree.pokemon.PokemonListFragment.OnListFragmentInteractionListener
import com.sree.pokemon.dummy.DummyContent.DummyItem
/**
 * [RecyclerView.Adapter] that can display a Pokemon and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */

class PokemonRecyclerViewAdapter(
    private val mValues: List<DummyItem>,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var VIEW_ITEM = 1
    private var VIEW_FOOTER = 2
    private var showFooter: Boolean = false
    private val myHandler =Handler()

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as DummyItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View
        if (viewType == VIEW_ITEM){
            view= LayoutInflater.from(parent.context).inflate(R.layout.layout_pokemon_list_item,parent,false)
            return MyViewHolder(view)
        }else if (viewType == VIEW_FOOTER){
            view= LayoutInflater.from(parent.context).inflate(R.layout.layout_footer,parent,false)
            return FooterViewHolder(view)
        }
        throw IllegalArgumentException("Invalid ViewType: $viewType")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FooterViewHolder) {
            if (showFooter) {
                holder.progressBarFooter.visibility = View.VISIBLE
            } else {
                holder.progressBarFooter.visibility = View.GONE
            }
        }
        if (holder is MyViewHolder) {
            val item = mValues[position]

            with(holder.view) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }
    }

    override fun getItemCount(): Int = mValues.size +1

    override fun getItemViewType(position: Int): Int {
        return if (position != 0 && position == itemCount-1) VIEW_FOOTER
        else VIEW_ITEM
    }

    inner class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    }

    inner class FooterViewHolder(view: View): RecyclerView.ViewHolder(view){
        val progressBarFooter: ProgressBar = view.findViewById(R.id.progressbar_footer)
    }
    fun showLoading(status: Boolean) {
        showFooter = status
    }

    fun loadingStatus(): Boolean = showFooter

    fun onLoadMore(){
        showLoading(true)

        myHandler.postDelayed({
            showLoading(false)
            Log.i("check_loadmore","called load more")
        },1500)
    }
}
