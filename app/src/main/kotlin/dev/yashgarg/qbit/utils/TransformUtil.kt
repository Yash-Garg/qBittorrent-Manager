package dev.yashgarg.qbit.utils

import dev.yashgarg.qbit.data.models.ContentTreeItem
import qbittorrent.models.TorrentFile

object TransformUtil {
    private const val FILE_KEY = "/FILE/"
    private const val UNWANTED_FILE = ".unwanted"

    fun transformFilesToTree(files: List<TorrentFile>, start: Int): List<ContentTreeItem> {
        val tree = mutableListOf<ContentTreeItem>()
        var folderIndex = 0
        files.sortedBy { it.name.lowercase() }

        val entries = files.groupBy { getFileFolder(it, start) }
        for ((folder, values) in entries) {
            if (folder == UNWANTED_FILE) {
                for (item in values) {
                    tree.add(
                        ContentTreeItem(
                            id = item.index,
                            name = item.name.substring(start + folder.length + 1),
                            item,
                            size = item.size,
                            progress = item.progress.toLong(),
                        )
                    )
                }
                continue
            }

            if (folder != FILE_KEY) {
                val subTree = transformFilesToTree(values, start + folder.length + 1)
                tree.add(
                    ContentTreeItem(
                        id = files.size + folderIndex++,
                        name = folder,
                        children = subTree,
                        size = subTree.sumOf { it.size },
                        progress = subTree.sumOf { it.progress } / subTree.size
                    )
                )
                continue
            }

            for (item in values) {
                tree.add(
                    ContentTreeItem(
                        id = item.index,
                        name = item.name.substring(start),
                        item,
                        size = item.size,
                        progress = item.progress.toLong()
                    )
                )
            }
        }

        return tree
    }

    private fun getFileFolder(item: TorrentFile, start: Int): String {
        val name = item.name
        val index = name.indexOf("/", start)

        if (index == -1) {
            return FILE_KEY
        }

        return name.substring(start, index)
    }
}
