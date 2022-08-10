package cafe.adriel.bonsai.core.node

import androidx.compose.runtime.Composable
import cafe.adriel.bonsai.core.BonsaiScope
import cafe.adriel.bonsai.core.node.extension.ExpandableNode
import cafe.adriel.bonsai.core.node.extension.ExpandableNodeHandler
import cafe.adriel.bonsai.core.node.extension.SelectableNode
import cafe.adriel.bonsai.core.node.extension.SelectableNodeHandler
import cafe.adriel.bonsai.core.util.randomUUID

typealias NodeComponent<T> = @Composable BonsaiScope<T>.(Node<T>) -> Unit

sealed interface Node<T> {

    val key: String

    val content: T

    val name: String

    val depth: Int

    val isSelected: Boolean

    val iconComponent: NodeComponent<T>

    val nameComponent: NodeComponent<T>
}

class LeafNode<T>
internal constructor(
    override val content: T,
    override val depth: Int,
    override val key: String = randomUUID,
    override val name: String = content.toString(),
    override val iconComponent: NodeComponent<T> = { DefaultNodeIcon(it) },
    override val nameComponent: NodeComponent<T> = { DefaultNodeName(it) }
) : Node<T>, SelectableNode by SelectableNodeHandler()

class BranchNode<T>
internal constructor(
    override val content: T,
    override val depth: Int,
    override val key: String = randomUUID,
    override val name: String = content.toString(),
    override val iconComponent: NodeComponent<T> = { DefaultNodeIcon(it) },
    override val nameComponent: NodeComponent<T> = { DefaultNodeName(it) }
) : Node<T>, SelectableNode by SelectableNodeHandler(), ExpandableNode by ExpandableNodeHandler()
