package com.karrar.movieapp.ui.explore

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.compose.ui.unit.Constraints
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentExploringBinding
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.Constants
import com.karrar.movieapp.utilities.EventObserve
import com.karrar.movieapp.utilities.observeEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ExploringFragment : BaseFragment<FragmentExploringBinding>() {
    override val layoutIdFragment: Int = R.layout.fragment_exploring
    override val viewModel: ExploringViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(true,resources.getString(R.string.explore_label))
        observeEvents()
        binding.recyclerTrend.adapter = TrendAdapter(mutableListOf(), viewModel)
    }

    private fun observeEvents(){
        navigateToSearch()
        navigateToMovies()
        navigateToSeries()
        navigateToActors()
        navigateToMediaDetails()
    }

    private fun navigateToSearch(){
        val extras = FragmentNavigatorExtras(binding.inputSearch to "search_box")
        viewModel.clickSearchEvent.observe(viewLifecycleOwner, EventObserve{
            Navigation.findNavController(binding.root)
                .navigate(ExploringFragmentDirections.actionExploringFragmentToSearchFragment(), extras)
        })
    }

    private fun navigateToMovies(){
        viewModel.clickMoviesEvent.observe(viewLifecycleOwner, EventObserve{
            findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToCategoryFragment(
                Constants.MOVIE_CATEGORIES_ID
            ))
        })
    }

    private fun navigateToSeries(){
        viewModel.clickSeriesEvent.observe(viewLifecycleOwner, EventObserve{
            findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToCategoryFragment(
                Constants.TV_CATEGORIES_ID
            ))
        })

        viewModel.clickTrendTVShowEvent.observeEvent(viewLifecycleOwner){
            findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToTvShowDetailsFragment(
                it
            ))
        }
    }

    private fun navigateToActors(){
        viewModel.clickActorsEvent.observe(viewLifecycleOwner, EventObserve{
            findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToActorsFragment())
        })
    }

    private fun navigateToMediaDetails(){
        viewModel.clickTrendEvent.observe(viewLifecycleOwner, EventObserve{ mediaId ->
            when(viewModel.mediaType.value){
                Constants.MOVIE -> {
                    findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToMovieDetailFragment(mediaId))
                }
                Constants.TV_SHOWS -> {
                    findNavController().navigate(ExploringFragmentDirections.actionExploringFragmentToTvShowDetailsFragment(mediaId))
                }
                Constants.ACTOR -> {}
            }
        })
    }
}