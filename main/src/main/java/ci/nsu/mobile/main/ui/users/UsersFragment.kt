package ci.nsu.mobile.main.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.data.model.UserDto
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class UsersFragment : Fragment() {

    private val viewModel: UsersViewModel by viewModels { appViewModelFactory() }
    private val adapter = UserAdapter(::showUserDetails)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefresh = view.findViewById<SwipeRefreshLayout>(R.id.swipeUsers)
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvUsers)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressUsers)
        val errorText = view.findViewById<TextView>(R.id.tvUsersError)
        val emptyText = view.findViewById<TextView>(R.id.tvUsersEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        swipeRefresh.setOnRefreshListener {
            viewModel.clearError()
            viewModel.loadUsers()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    swipeRefresh.isRefreshing = false
                    progressBar.isVisible = state.isLoading
                    errorText.isVisible = !state.error.isNullOrBlank()
                    errorText.text = state.error
                    emptyText.isVisible = !state.isLoading && state.users.isEmpty()
                    adapter.submitList(state.users)
                }
            }
        }
    }

    private fun showUserDetails(user: UserDto) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(user.fullName())
            .setMessage(
                "ID: ${user.id}\n" +
                    "Логин: ${user.login}\n" +
                    "Email: ${user.email}\n" +
                    "Группа ID: ${user.person.groupId}\n" +
                    "Дата рождения: ${user.person.birthDate}\n" +
                    "Пол: ${user.person.gender}"
            )
            .setPositiveButton("Закрыть", null)
            .show()
    }
}
