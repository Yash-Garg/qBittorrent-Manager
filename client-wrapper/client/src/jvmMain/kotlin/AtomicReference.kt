package qbittorrent.internal

internal actual class AtomicReference<T> actual constructor(value: T) {

    private val ref = java.util.concurrent.atomic.AtomicReference(value)

    actual var value: T
        get() = ref.get()
        set(value) {
            ref.set(value)
        }
}
