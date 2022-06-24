package dev.yashgarg.qbit.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.HomeFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.home_fragment) {
    private val binding: HomeFragmentBinding by viewBinding(HomeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
        binding.addServerFab.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_homeFragment_to_addServerFragment)
        }
    }
}
