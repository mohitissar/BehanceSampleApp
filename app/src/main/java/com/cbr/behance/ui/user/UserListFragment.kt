package com.cbr.behance.ui.user

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.cbr.behance.BehanceSampleApplication
import com.cbr.behance.R
import com.cbr.behance.common.Outcome
import com.cbr.behance.common.recycler.GridDecorator
import com.cbr.behance.ui.user.di.DaggerUserComponent
import com.cbr.behance.ui.user.recycler.UserGridAdapter
import com.futureworkshops.data.model.domain.User
import com.futureworkshops.domain.extension.gone
import com.futureworkshops.domain.extension.screenWidth
import com.futureworkshops.domain.extension.visible
import kotlinx.android.synthetic.main.fragment_list.*
import kotlinx.android.synthetic.main.include_error_layout.*
import kotlinx.android.synthetic.main.include_loading.*

import timber.log.Timber
import javax.inject.Inject


class UserListFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: UserListViewModelFactory

    private lateinit var usersViewModel: UserListViewModel

    lateinit var gridAdapter: UserGridAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        DaggerUserComponent
                .builder()
                .applicationComponent(BehanceSampleApplication.appComponent)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerview.apply {
            val itemWidth = (resources.getDimension(R.dimen.card_behace_user_item) + resources.getDimension(R.dimen.card_standard_padding)).toInt()
            val columnCount = context.screenWidth() / itemWidth
            val gridLayoutManager = GridLayoutManager(context, columnCount)
            gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = gridAdapter.getSpanForPosition(position)
            }
            layoutManager = gridLayoutManager
            addItemDecoration(GridDecorator(context, columnCount))
            gridAdapter = UserGridAdapter()
            adapter = gridAdapter
        }

        usersViewModel = ViewModelProviders.of(this, viewModelFactory).get(UserListViewModel::class.java)

        usersViewModel.userListLiveData().observe(this, Observer { outcome ->
            when (outcome) {
                is Outcome.Progress -> showLoading()
                is Outcome.Error -> showError(outcome.errorMsg)
                is Outcome.Success -> showUsers(outcome.data)
            }
        })
        usersViewModel.loadUsers()
    }

    private fun showUsers(data: List<User>?) {
        if (data?.isNotEmpty() == true) {
            loading.gone()
            recyclerview.visible()
            errorLayout.gone()
            gridAdapter.setUsers(data)
        } else {
            showError(getString(R.string.err_empty_data))
        }
    }

    private fun showError(message: String?) {
        Timber.e(message)
        loading.gone()
        recyclerview.gone()
        errorLayout.visible()
        errorTextView.text = message
    }

    private fun showLoading() {
        loading.visible()
        recyclerview.gone()
        errorLayout.gone()
    }
}