internal typealias Handle = Long
internal class ConcurrentHandleMap<T>(
    private val handles: MutableMap<Handle, T> = mutableMapOf(),
) {
    private val lock = java.util.concurrent.locks.ReentrantLock()
    private val currentHandle = AtomicLong(0L)
    private val stride = 1L

    fun insert(obj: T): Handle =
        lock.withLock {
            currentHandle.getAndAdd(stride)
                .also { handle ->
                    handles[handle] = obj
                }
        }

    fun get(handle: Handle) = lock.withLock {
        handles[handle]
    }

    fun delete(handle: Handle) {
        this.remove(handle)
    }

    fun remove(handle: Handle): T? =
        lock.withLock {
            handles.remove(handle)
        }
}

interface ForeignCallback : com.sun.jna.Callback {
    public fun callback(handle: Handle, method: Int, argsData: Pointer, argsLen: Int, outBuf: RustBufferByReference): Int
}

// Magic number for the Rust proxy to call using the same mechanism as every other method,
// to free the callback once it's dropped by Rust.
internal const val IDX_CALLBACK_FREE = 0
// Callback return codes
internal const val UNIFFI_CALLBACK_SUCCESS = 0
internal const val UNIFFI_CALLBACK_ERROR = 1
internal const val UNIFFI_CALLBACK_UNEXPECTED_ERROR = 2

public abstract class FfiConverterCallbackInterface<CallbackInterface>(
    protected val foreignCallback: ForeignCallback
): FfiConverter<CallbackInterface, Handle> {
    private val handleMap = ConcurrentHandleMap<CallbackInterface>()

    // Registers the foreign callback with the Rust side.
    // This method is generated for each callback interface.
    internal abstract fun register(lib: _UniFFILib)

    fun drop(handle: Handle): RustBuffer.ByValue {
        return handleMap.remove(handle).let { RustBuffer.ByValue() }
    }

    override fun lift(value: Handle): CallbackInterface {
        return handleMap.get(value) ?: throw InternalException("No callback in handlemap; this is a Uniffi bug")
    }

    override fun read(buf: ByteBuffer) = lift(buf.getLong())

    override fun lower(value: CallbackInterface) =
        handleMap.insert(value).also {
            assert(handleMap.get(it) === value) { "Handle map is not returning the object we just placed there. This is a bug in the HandleMap." }
        }

    override fun allocationSize(value: CallbackInterface) = 8

    override fun write(value: CallbackInterface, buf: ByteBuffer) {
        buf.putLong(lower(value))
    }
}
