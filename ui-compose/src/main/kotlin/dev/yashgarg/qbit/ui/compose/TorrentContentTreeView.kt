package dev.yashgarg.qbit.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.BonsaiStyle
import cafe.adriel.bonsai.core.node.Branch
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import dev.yashgarg.qbit.data.models.ContentTreeItem
import dev.yashgarg.qbit.ui.compose.theme.SpaceGrotesk

@Composable
fun TorrentContentTreeView(nodes: List<ContentTreeItem>) {
    val tree = Tree<ContentTreeItem> { ContentTree(nodes) }

    Bonsai(
        tree,
        style =
            BonsaiStyle(
                nodeNameTextStyle =
                    TextStyle(
                        color = Color.White,
                        fontSize = 14.sp,
                        fontFamily = SpaceGrotesk,
                    ),
                toggleIconColorFilter = ColorFilter.tint(Color.White)
            ),
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun TreeScope.ContentTree(nodes: List<ContentTreeItem>) {
    nodes.forEach { node -> ContentNode(node) }
}

@Composable
private fun TreeScope.ContentNode(node: ContentTreeItem) {
    if (node.item == null) {
        Branch(node.name) { node.children?.reversed()?.let { ContentTree(it) } }
    } else {
        Leaf(node.name)
    }
}
