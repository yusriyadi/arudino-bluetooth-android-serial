package tellabs.android.basekotlin.utils

import me.aflak.bluetooth.reader.SocketReader
import java.io.IOException
import java.io.InputStream
import java.io.PushbackInputStream


class DelimiterReader(inputStream: InputStream?) : SocketReader(inputStream) {
    private val reader: PushbackInputStream
    private val delimiter: Byte

    @Throws(IOException::class)
    override fun read(): ByteArray {
        val byteList: MutableList<Byte> = ArrayList()
        val tmp = ByteArray(1)
        while (true) {
            val n: Int = reader.read()
            reader.unread(n)
            val count: Int = reader.read(tmp)
            if (count > 0) {
                if (tmp[0] == delimiter) {
                    val returnBytes = ByteArray(byteList.size)
                    for (i in byteList.indices) {
                        returnBytes[i] = byteList[i]
                    }
                    return returnBytes
                } else {
                    byteList.add(tmp[0])
                }
            }
        }
    }

    init {
        reader = PushbackInputStream(inputStream)
        delimiter = 0
    }
}