package ci.nsu.mobile.main.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
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
import ci.nsu.mobile.main.data.model.GroupDto
import ci.nsu.mobile.main.ui.common.appViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val viewModel: RegisterViewModel by viewModels { appViewModelFactory() }
    private var groups: List<GroupDto> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val firstNameInput = view.findViewById<TextInputEditText>(R.id.etFirstName)
        val lastNameInput = view.findViewById<TextInputEditText>(R.id.etLastName)
        val middleNameInput = view.findViewById<TextInputEditText>(R.id.etMiddleName)
        val birthDateInput = view.findViewById<TextInputEditText>(R.id.etBirthDate)
        val loginInput = view.findViewById<TextInputEditText>(R.id.etRegisterLogin)
        val passwordInput = view.findViewById<TextInputEditText>(R.id.etRegisterPassword)
        val emailInput = view.findViewById<TextInputEditText>(R.id.etEmail)
        val phoneInput = view.findViewById<TextInputEditText>(R.id.etPhone)
        val genderSpinner = view.findViewById<Spinner>(R.id.spinnerGender)
        val groupSpinner = view.findViewById<Spinner>(R.id.spinnerGroup)
        val registerButton = view.findViewById<Button>(R.id.btnRegister)
        val backButton = view.findViewById<Button>(R.id.btnBackToLogin)
        val retryGroupsButton = view.findViewById<Button>(R.id.btnRetryGroups)
        val errorText = view.findViewById<TextView>(R.id.tvRegisterError)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressRegister)

        setupGenderSpinner(genderSpinner)

        registerButton.setOnClickListener {
            val selectedGroupId = groups.getOrNull(groupSpinner.selectedItemPosition)?.id ?: 0
            viewModel.register(
                firstName = firstNameInput.text?.toString().orEmpty(),
                lastName = lastNameInput.text?.toString().orEmpty(),
                middleName = middleNameInput.text?.toString().orEmpty(),
                birthDate = birthDateInput.text?.toString().orEmpty(),
                gender = genderSpinner.selectedItem?.toString().orEmpty(),
                groupId = selectedGroupId,
                login = loginInput.text?.toString().orEmpty(),
                password = passwordInput.text?.toString().orEmpty(),
                email = emailInput.text?.toString().orEmpty(),
                phoneNumber = phoneInput.text?.toString().orEmpty()
            )
        }

        backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        retryGroupsButton.setOnClickListener {
            viewModel.loadGroups()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    groups = state.groups
                    progressBar.isVisible = state.isLoading
                    registerButton.isEnabled = !state.isLoading
                    errorText.isVisible = !state.error.isNullOrBlank()
                    errorText.text = state.error
                    retryGroupsButton.isVisible = !state.isLoading && state.groups.isEmpty()
                    updateGroupSpinner(groupSpinner, state.groups)

                    if (state.isSuccess) {
                        Toast.makeText(
                            requireContext(),
                            "Регистрация выполнена. Теперь войдите.",
                            Toast.LENGTH_SHORT
                        ).show()
                        viewModel.consumeSuccess()
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    private fun setupGenderSpinner(spinner: Spinner) {
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Male", "Female")
        )
    }

    private fun updateGroupSpinner(spinner: Spinner, groups: List<GroupDto>) {
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            groups.map { it.name }
        )
    }
}
