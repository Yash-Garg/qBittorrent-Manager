package dev.yashgarg.qbit.utils

class ClientConnectionError : Exception("Failed to connect")

class TorrentRemovedError : Exception("Torrent has been removed")
