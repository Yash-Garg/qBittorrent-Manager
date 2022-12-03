package dev.yashgarg.qbit.ui.config

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R as AppR
import dev.yashgarg.qbit.common.R
import dev.yashgarg.qbit.data.manager.ClientManager
import dev.yashgarg.qbit.databinding.ConfigFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConfigFragment : Fragment(AppR.layout.config_fragment) {
    private val binding by viewBinding(ConfigFragmentBinding::bind)
    private val viewModel by viewModels<ConfigViewModel>()

    private val connectionTypes = arrayOf("HTTP", "HTTPS")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlows()
        watchTextFields()
        setupActionbar()

        val adapter = ArrayAdapter(requireContext(), AppR.layout.list_item, connectionTypes)
        (binding.typeDropdown.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        binding.saveButton.setOnClickListener {
            viewModel.validateForm(
                binding.serverNameTil.editText?.text.toString(),
                binding.serverHostTil.editText?.text.toString(),
                binding.serverPortTil.editText?.text.toString(),
                binding.typeDropdown.editText?.text.toString(),
                binding.serverUsernameTil.editText?.text.toString(),
                binding.serverPasswordTil.editText?.text.toString(),
            )
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).setSupportActionBar(null)
    }

    private fun setupActionbar() {
        val activity = (activity as AppCompatActivity)
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setHomeButtonEnabled(true)
        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.setNavigationOnClickListener { it.findNavController().navigateUp() }
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)

        viewModel.validationEvents
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::handleEvent)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun watchTextFields() {
        with(binding) {
            serverHostTiet.doAfterTextChanged { text -> viewModel.validateHostUrl(text.toString()) }

            serverPortTiet.doAfterTextChanged { text -> viewModel.validatePort(text.toString()) }

            serverUsernameTiet.doAfterTextChanged { text ->
                viewModel.validateUsername(text.toString())
            }

            typeTextview.doAfterTextChanged { text ->
                viewModel.validateConnectionType(text.toString())
            }

            serverNameTiet.doAfterTextChanged { text -> viewModel.validateName(text.toString()) }

            serverPasswordTiet.doAfterTextChanged { text ->
                viewModel.validatePassword(text.toString())
            }
        }
    }

    private fun enableFields(enabled: Boolean) {
        binding.serverNameTiet.isEnabled = enabled
        binding.serverHostTiet.isEnabled = enabled
        binding.serverPortTiet.isEnabled = enabled
        binding.typeDropdown.isEnabled = enabled
        binding.serverUsernameTiet.isEnabled = enabled
        binding.serverPasswordTiet.isEnabled = enabled
        binding.saveButton.isEnabled = enabled
    }

    private fun handleEvent(event: ConfigViewModel.ValidationEvent) {
        when (event) {
            is ConfigViewModel.ValidationEvent.Success -> {
                enableFields(false)
                val checkSnackbar =
                    Snackbar.make(
                        requireView(),
                        "Checking connection, please wait...",
                        Snackbar.LENGTH_SHORT
                    )
                checkSnackbar.show()

                viewLifecycleOwner.lifecycleScope.launch {
                    val connectionResponse =
                        viewModel.testConfig(
                            "${binding.typeDropdown.editText?.text.toString().lowercase()}://" +
                                "${binding.serverHostTil.editText?.text}:${binding.serverPortTil.editText?.text}",
                            binding.serverUsernameTil.editText?.text.toString(),
                            binding.serverPasswordTil.editText?.text.toString(),
                            binding.trustCert.isChecked
                        )

                    when (connectionResponse) {
                        is Ok -> {
                            checkSnackbar.dismiss()
                            Toast.makeText(
                                    context,
                                    "Success! Client app version is ${connectionResponse.value}",
                                    Toast.LENGTH_LONG
                                )
                                .show()

                            viewModel.insert(
                                binding.serverNameTil.editText?.text.toString(),
                                binding.serverHostTil.editText?.text.toString(),
                                binding.serverPortTil.editText?.text.toString(),
                                binding.typeDropdown.editText?.text.toString(),
                                binding.serverUsernameTil.editText?.text.toString(),
                                binding.serverPasswordTil.editText?.text.toString(),
                                binding.trustCert.isChecked
                            )

                            findNavController().navigateUp()
                        }
                        is Err -> {
                            Log.e(ClientManager.tag, connectionResponse.error.toString())
                            Snackbar.make(
                                    requireView(),
                                    "Failed! ${connectionResponse.error.message}",
                                    Snackbar.LENGTH_LONG
                                )
                                .show()
                        }
                    }
                    enableFields(true)
                }
            }
        }
    }

    private fun render(state: ConfigState) {
        with(binding) {
            if (state.showServerNameError) {
                serverNameTil.error = getString(R.string.invalid_name)
            } else {
                serverNameTil.error = null
            }

            if (state.showUrlError) {
                serverHostTil.error = getString(R.string.invalid_url)
            } else {
                serverHostTil.error = null
            }

            if (state.showPortError) {
                serverPortTil.error = getString(R.string.invalid_port)
            } else {
                serverPortTil.error = null
            }

            if (state.showUsernameError) {
                serverUsernameTil.error = getString(R.string.invalid_username)
            } else {
                serverUsernameTil.error = null
            }

            if (state.showConnectionTypeError) {
                typeDropdown.error = getString(R.string.invalid_type)
            } else {
                typeDropdown.error = null
            }

            if (state.showPasswordError) {
                serverPasswordTil.error = getString(R.string.invalid_password)
            } else {
                serverPasswordTil.error = null
            }
        }
    }
}
