package ci.nsu.mobile.main.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainContainerFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<MaterialToolbar>(R.id.topToolbar)
        val bottomNavigation = view.findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.mainTabsNavHost) as NavHostFragment
        val childNavController = navHostFragment.navController

        bottomNavigation.setupWithNavController(childNavController)
        toolbar.subtitle = viewModel.currentUserName.value
        toolbar.setOnMenuItemClickListener { item -> handleMenuClick(item) }

        childNavController.addOnDestinationChangedListener { _, destination, _ ->
            toolbar.title = when (destination.id) {
                R.id.usersFragment -> getString(R.string.title_users)
                R.id.myCalculationsFragment -> getString(R.string.title_my_calculations)
                else -> getString(R.string.title_new_calculation)
            }
        }
    }

    private fun handleMenuClick(item: MenuItem): Boolean {
        return if (item.itemId == R.id.actionLogout) {
            viewModel.logout()
            findNavController().navigate(
                R.id.loginFragment,
                null,
                androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.mainContainerFragment, true)
                    .build()
            )
            true
        } else {
            false
        }
    }
}
