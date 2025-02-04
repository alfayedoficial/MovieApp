package com.karrar.movieapp.ui.category

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.karrar.movieapp.R
import com.karrar.movieapp.databinding.FragmentCategoryBinding
import com.karrar.movieapp.ui.adapters.LoadUIStateAdapter
import com.karrar.movieapp.ui.allMedia.AllMediaAdapter
import com.karrar.movieapp.ui.base.BaseFragment
import com.karrar.movieapp.utilities.*
import com.karrar.movieapp.utilities.Constants.TV_CATEGORIES_ID
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : BaseFragment<FragmentCategoryBinding>() {

    override val layoutIdFragment = R.layout.fragment_category
    override val viewModel: CategoryViewModel by viewModels()
    private val allMediaAdapter: AllMediaAdapter by lazy { AllMediaAdapter(viewModel) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setTitle(true, getTitle())
        setMediaAdapter()
        observeEvents()
    }

    private fun setMediaAdapter() {
        val footerAdapter = LoadUIStateAdapter(allMediaAdapter::retry)
        binding.recyclerMedia.adapter = allMediaAdapter.withLoadStateFooter(footerAdapter)

        val mManager = binding.recyclerMedia.layoutManager as GridLayoutManager
        mManager.setSpanSize(footerAdapter, allMediaAdapter, mManager.spanCount)

        collect(flow = allMediaAdapter.loadStateFlow,
            action = { viewModel.setErrorUiState(it.source.refresh) })

        getDataByCategory()
    }

    private fun getDataByCategory() {
        viewModel.selectedCategory.observe(viewLifecycleOwner) { categoryId ->
            allMediaAdapter.submitData(lifecycle, PagingData.empty())
            categoryId?.let {
                collectLast(viewModel.setAllMediaList(categoryId))
                { allMediaAdapter.submitData(it) }
            }
        }
    }

    private fun observeEvents() {
        viewModel.clickMovieEvent.observe(viewLifecycleOwner, EventObserve {
            if (viewModel.args.mediaId == TV_CATEGORIES_ID) {
                navigateToTvShowDetails(it)
            } else {
                navigateToMovieDetails(it)
            }
        })

        viewModel.clickRetryEvent.observeEvent(viewLifecycleOwner) {
            if (it) {
                allMediaAdapter.retry()
            }
        }
    }

    private fun navigateToMovieDetails(movieId: Int) {
        val action = CategoryFragmentDirections.actionCategoryFragmentToMovieDetailFragment(movieId)
        findNavController().navigate(action)
    }

    private fun navigateToTvShowDetails(tvShowId: Int) {
        val action =
            CategoryFragmentDirections.actionCategoryFragmentToTvShowDetailsFragment(tvShowId)
        findNavController().navigate(action)
    }

    private fun getTitle(): String {
        return if (viewModel.args.mediaId == TV_CATEGORIES_ID) {
            resources.getString(R.string.title_tv_shows)
        } else {
            resources.getString(R.string.movies)
        }
    }
}
