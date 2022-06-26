package dev.yashgarg.qbit.ui.config

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.ConfigFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ConfigFragment : Fragment(R.layout.config_fragment) {
    private val binding: ConfigFragmentBinding by viewBinding(ConfigFragmentBinding::bind)
    private val viewModel: ConfigViewModel by viewModels()

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
        setupMenu()
        setupActionbar()

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, connectionTypes)
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
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()

        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) = Unit
                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    when (menuItem.itemId) {
                        android.R.id.home -> {
                            Navigation.findNavController(requireView()).navigateUp()
                            return true
                        }
                        else -> this.onMenuItemSelected(menuItem)
                    }
                    return true
                }
            },
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun watchTextFields() {
        binding.serverHostTiet.doAfterTextChanged { text ->
            viewModel.validateHostUrl(text.toString())
        }

        binding.serverPortTiet.doAfterTextChanged { text ->
            viewModel.validatePort(text.toString())
        }

        binding.serverUsernameTiet.doAfterTextChanged { text ->
            viewModel.validateUsername(text.toString())
        }

        binding.typeTextview.doAfterTextChanged { text ->
            viewModel.validateConnectionType(text.toString())
        }

        binding.serverNameTiet.doAfterTextChanged { text ->
            viewModel.validateName(text.toString())
        }

        binding.serverPasswordTiet.doAfterTextChanged { text ->
            viewModel.validatePassword(text.toString())
        }
    }

    private fun render(state: ConfigUiState) {
        with(binding) {
            if (state.showServerNameError) {
                serverNameTil.isErrorEnabled = true
                serverNameTil.error = getString(R.string.invalid_name)
            } else {
                serverNameTil.isErrorEnabled = false
                serverNameTil.error = null
            }

            if (state.showUrlError) {
                serverHostTil.isErrorEnabled = true
                serverHostTil.error = getString(R.string.invalid_url)
            } else {
                serverHostTil.isErrorEnabled = false
                serverHostTil.error = null
            }

            if (state.showPortError) {
                serverPortTil.isErrorEnabled = true
                serverPortTil.error = getString(R.string.invalid_port)
            } else {
                serverPortTil.isErrorEnabled = false
                serverPortTil.error = null
            }

            if (state.showUsernameError) {
                serverUsernameTil.isErrorEnabled = true
                serverUsernameTil.error = getString(R.string.invalid_username)
            } else {
                serverUsernameTil.isErrorEnabled = false
                serverUsernameTil.error = null
            }

            if (state.showConnectionTypeError) {
                typeDropdown.isErrorEnabled = true
                typeDropdown.error = getString(R.string.invalid_type)
            } else {
                typeDropdown.isErrorEnabled = false
                typeDropdown.error = null
            }

            if (state.showPasswordError) {
                serverPasswordTil.isErrorEnabled = true
                serverPasswordTil.error = getString(R.string.invalid_password)
            } else {
                serverPasswordTil.isErrorEnabled = false
                serverPasswordTil.error = null
            }
        }
    }
}
