package dev.yashgarg.qbit.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.yashgarg.qbit.data.models.ContentTreeItem

@Composable
fun ContentTreeView(nodes: List<ContentTreeItem>) {
    val expandedItems = remember { mutableStateListOf<ContentTreeItem>() }
    expandedItems.addAll(nodes)
    LazyColumn {
        nodes(
            nodes,
            isExpanded = { expandedItems.contains(it) },
            toggleExpanded = {
                if (expandedItems.contains(it)) {
                    expandedItems.remove(it)
                } else {
                    expandedItems.add(it)
                }
            },
        )
    }
}

fun LazyListScope.nodes(
    nodes: List<ContentTreeItem>,
    isExpanded: (ContentTreeItem) -> Boolean,
    toggleExpanded: (ContentTreeItem) -> Unit,
) {
    nodes.forEach { node ->
        node(
            node,
            isExpanded = isExpanded,
            toggleExpanded = toggleExpanded,
        )
    }
}

fun LazyListScope.node(
    node: ContentTreeItem,
    isExpanded: (ContentTreeItem) -> Boolean,
    toggleExpanded: (ContentTreeItem) -> Unit,
) {
    item { Text(node.name, Modifier.clickable { toggleExpanded(node) }) }
    if (isExpanded(node)) {
        node.children?.let {
            nodes(
                it,
                isExpanded = isExpanded,
                toggleExpanded = toggleExpanded,
            )
        }
    }
}
