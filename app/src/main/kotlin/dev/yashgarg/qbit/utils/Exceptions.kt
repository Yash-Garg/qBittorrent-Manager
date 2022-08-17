package dev.yashgarg.qbit.utils

class ClientConnectionError : Exception("Failed to connect client")

class TorrentRemovedError : Exception("Torrent has been removed")
