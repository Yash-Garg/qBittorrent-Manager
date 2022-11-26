package dev.yashgarg.qbit.ui.compose

import androidx.annotation.ColorRes
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource

@Composable
fun CenterLinearLoading(modifier: Modifier, @ColorRes color: Int) {
    Center(modifier) { LinearProgressIndicator(color = colorResource(color)) }
}
