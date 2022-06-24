package dev.yashgarg.qbit.ui.config

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.AddServerFragmentBinding
import dev.yashgarg.qbit.models.ServerConfig

@AndroidEntryPoint
class AddServerFragment : Fragment(R.layout.add_server_fragment) {
    private var _binding: AddServerFragmentBinding? = null
    private val binding
        get() = _binding!!

    private val viewModel: ConfigViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AddServerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.saveConfigFab.setOnClickListener {
            val config =
                ServerConfig(
                    configId = 1,
                    baseUrl = binding.serverHost.editText?.text.toString(),
                    username = binding.serverUsername.editText?.text.toString(),
                    password = binding.serverPassword.editText?.text.toString(),
                    port = binding.serverPort.editText?.text.toString().toInt(),
                )

            viewModel.insert(config)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
