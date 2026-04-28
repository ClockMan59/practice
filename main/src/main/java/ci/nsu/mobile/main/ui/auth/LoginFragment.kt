package ci.nsu.mobile.main.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import ci.nsu.mobile.main.R
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModels { appViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val loginInput = view.findViewById<TextInputEditText>(R.id.etLogin)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.etPassword)
        val loginButton = view.findViewById<Button>(R.id.btnLogin)
        val registerLink = view.findViewById<TextView>(R.id.tvGoToRegister)
        val errorText = view.findViewById<TextView>(R.id.tvLoginError)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressLogin)

        loginButton.setOnClickListener {
            viewModel.login(
                login = loginInput.text?.toString().orEmpty(),
                password = passwordInput.text?.toString().orEmpty()
            )
        }

        registerLink.setOnClickListener {
            viewModel.clearError()
            findNavController().navigate(R.id.registerFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    progressBar.isVisible = state.isLoading
                    loginButton.isEnabled = !state.isLoading
                    errorText.isVisible = !state.error.isNullOrBlank()
                    errorText.text = state.error

                    if (state.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            "Авторизация выполнена",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.consumeSuccess()
                        findNavController().navigate(
                            R.id.mainContainerFragment,
                            null,
                            androidx.navigation.NavOptions.Builder()
                                .setPopUpTo(R.id.loginFragment, true)
                                .build()
                        )
                    }
                }
            }
        }
    }
}
