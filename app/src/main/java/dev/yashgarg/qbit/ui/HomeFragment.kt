package dev.yashgarg.qbit.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.HomeFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {
    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addServerFab.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_configFragment)
        }
    }
}
