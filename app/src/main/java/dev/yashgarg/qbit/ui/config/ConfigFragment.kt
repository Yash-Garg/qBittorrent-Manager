package dev.yashgarg.qbit.ui.config

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFlows()

        binding.serverHostUrl.doAfterTextChanged { editable ->
            viewModel.validateHostUrl(editable.toString())
        }
    }

    private fun observeFlows() {
        viewModel.uiState
            .flowWithLifecycle(viewLifecycleOwner.lifecycle)
            .onEach(::render)
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun render(state: ConfigUiState) {
        with(binding) {
            if (state.showUrlError) {
                serverHost.error = getString(R.string.url_invalid)
            } else {
                serverHost.error = null
            }
        }
    }
}
