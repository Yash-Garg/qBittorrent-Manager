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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlows()
        watchTextFields()
        setupMenu()

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, connectionTypes)
        (binding.dropdown.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        binding.autoTextview.setSelection(0)
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).setSupportActionBar(null)
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
        binding.serverHostUrl.doAfterTextChanged { editable ->
            viewModel.validateHostUrl(editable.toString())
        }

        binding.serverPortNumber.doAfterTextChanged { editable ->
            viewModel.validatePort(editable.toString())
        }
    }

    private fun render(state: ConfigUiState) {
        with(binding) {
            if (state.showUrlError) {
                serverHost.isErrorEnabled = true
                serverHost.error = getString(R.string.invalid_url)
            } else {
                serverHost.isErrorEnabled = false
                serverHost.error = null
            }

            if (state.showPortError) {
                serverPort.isErrorEnabled = true
                serverPort.error = getString(R.string.invalid_port)
            } else {
                serverPort.isErrorEnabled = false
                serverPort.error = null
            }
        }
    }
}
