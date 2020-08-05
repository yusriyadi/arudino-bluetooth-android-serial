package tellabs.android.basekotlin.utils

import android.util.Log.d
import com.github.ajalt.timberkt.d
import me.aflak.bluetooth.reader.SocketReader
import okio.IOException
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader


class LineReader(inputStream: InputStream?) : SocketReader(inputStream) {
    private val reader: BufferedReader

    @Throws(IOException::class)
    override fun read(): ByteArray {
        return reader.readLine().toByteArray()
    }

    init {
        reader = BufferedReader(InputStreamReader(inputStream))
    }
}