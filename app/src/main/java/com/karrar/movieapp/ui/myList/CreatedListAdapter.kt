package com.karrar.movieapp.ui.myList

import com.karrar.movieapp.R
import com.karrar.movieapp.domain.models.CreatedList
import com.karrar.movieapp.ui.base.*

class CreatedListAdapter(items: List<CreatedList>, listener: CreatedListInteractionListener) :
    BaseAdapter<CreatedList>(items, listener) {
    override val layoutID: Int = R.layout.item_saved_list
}

interface CreatedListInteractionListener : BaseInteractionListener{
    fun onListClick(item: CreatedList)
}