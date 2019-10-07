package com.friday.ar.dashboard.ui.dashboardFragment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.friday.ar.core.Theme
import com.friday.ar.core_ui.recyclerview.layoutmanager.ScrollableLayoutmanager
import com.friday.ar.dashboard.R
import com.friday.ar.dashboard.list.DashboardAdapter
import kotlinx.android.synthetic.main.fragment_dashboard_framgent.view.*
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * A simple [Fragment] subclass.
 */
class DashboardFramgent : Fragment() {
    companion object {
        const val LOGTAG = "DashboardFragment"
    }

    private val viewModel by viewModel<DashboardFragmentViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_dashboard_framgent, container, false)
        // Inflate the layout for this fragment
        Thread(Runnable {
            val theme = Theme(requireContext())
            requireActivity().runOnUiThread { fragmentView.mainTitleView.background = theme.createAppThemeGadient() }


        }).start()
        fragmentView.mainPageDashboardList.layoutManager = ScrollableLayoutmanager(requireContext())
        fragmentView.mainPageDashboardList.adapter = DashboardAdapter(requireContext(), ArrayList(0))

        fragmentView.mainSwipeRefreshLayout.setOnRefreshListener {
            viewModel.runRefresh()
        }

        viewModel.getDashBoardListData().observe(this, Observer { list ->
            fragmentView.mainSwipeRefreshLayout.isRefreshing = false
            (fragmentView.mainPageDashboardList.adapter!! as DashboardAdapter).onRefresh(list)
            if (list.isEmpty()) {
                Log.d(LOGTAG, "list is empty")
                fragmentView.mainTitleViewAppBarLayout.setExpanded(true, true)
                fragmentView.dashboardEmptyView.visibility = View.VISIBLE
                fragmentView.mainPageDashboardList.visibility = View.GONE
            } else {
                fragmentView.dashboardEmptyView.visibility = View.GONE
                fragmentView.mainPageDashboardList.visibility = View.VISIBLE
            }
        })

        fragmentView.mainSwipeRefreshLayout.isRefreshing = true
        return fragmentView
    }


}
