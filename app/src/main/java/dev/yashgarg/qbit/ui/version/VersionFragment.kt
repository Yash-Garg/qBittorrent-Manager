package dev.yashgarg.qbit.ui.version

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.yashgarg.qbit.R
import dev.yashgarg.qbit.databinding.VersionFragmentBinding
import dev.yashgarg.qbit.utils.viewBinding

@AndroidEntryPoint
class VersionFragment : Fragment(R.layout.version_fragment) {
    private val binding by viewBinding(VersionFragmentBinding::bind)
    private val viewModel by viewModels<VersionViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        with(binding) {
            toolbar.setNavigationOnClickListener { it.findNavController().navigateUp() }
            composeView.setContent {
                val state by viewModel.uiState.collectAsState()

                Mdc3Theme(setTextColors = true, setDefaultFontFamily = true) { VersionView(state) }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        (activity as AppCompatActivity).setSupportActionBar(null)
    }
}

@Composable
fun VersionView(state: VersionState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (state.loading) LinearProgressIndicator(color = colorResource(R.color.accent))
        else
            Text(
                text = "Web Api v${state.apiVersion}",
                modifier = Modifier.padding(16.dp),
                style =
                    TextStyle(
                        fontSize = 20.sp,
                        color = colorResource(R.color.grey),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = .1.sp
                    )
            )
    }
}
