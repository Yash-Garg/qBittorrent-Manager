package dev.yashgarg.qbit.ui.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.bonsai.core.Bonsai
import cafe.adriel.bonsai.core.BonsaiStyle
import cafe.adriel.bonsai.core.node.Branch
import cafe.adriel.bonsai.core.node.BranchNode
import cafe.adriel.bonsai.core.node.Leaf
import cafe.adriel.bonsai.core.tree.Tree
import cafe.adriel.bonsai.core.tree.TreeScope
import dev.yashgarg.qbit.data.models.ContentTreeItem
import dev.yashgarg.qbit.ui.compose.theme.SpaceGrotesk

@Composable
fun TorrentContentTreeView(nodes: List<ContentTreeItem>) {
    val tree = Tree<ContentTreeItem> { ContentTree(nodes) }
    tree.expandRoot()

    Bonsai(
        tree,
        style = torrentContentStyle(),
        modifier = Modifier.fillMaxSize().padding(16.dp),
        onLongClick = null,
    )
}

private fun torrentContentStyle(): BonsaiStyle<ContentTreeItem> {
    return BonsaiStyle(
        nodeNameTextStyle =
            TextStyle(
                color = Color.White,
                fontSize = 16.sp,
                fontFamily = SpaceGrotesk,
            ),
        toggleIconColorFilter = ColorFilter.tint(Color.LightGray),
        nodeCollapsedIcon = { node ->
            rememberVectorPainter(
                if (node is BranchNode) Icons.Rounded.Folder else Icons.Default.FileCopy
            )
        },
        nodeCollapsedIconColorFilter = ColorFilter.tint(Color.LightGray),
        nodeNameStartPadding = 8.dp
    )
}

@Composable
private fun TreeScope.ContentTree(nodes: List<ContentTreeItem>) {
    nodes.forEach { node -> ContentNode(node) }
}

@Composable
private fun TreeScope.ContentNode(node: ContentTreeItem) {
    // If [node.item] is null, therefore it is a directory
    if (node.item == null) {
        Branch(node.name) { node.children?.reversed()?.let { ContentTree(it) } }
    } else {
        Leaf(node.name)
    }
}
