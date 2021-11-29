package com.example.individualusprojektas

class DataConversion {

    companion object {

        public fun LongToBytes(buffer: ByteArray, lng: Long, offset: Int) {

            buffer[offset + 0] = (lng shr 0).toByte()
            buffer[offset + 1] = (lng shr 8).toByte()
            buffer[offset + 2] = (lng shr 16).toByte()
            buffer[offset + 3] = (lng shr 24).toByte()
            buffer[offset + 4] = (lng shr 32).toByte()
            buffer[offset + 5] = (lng shr 40).toByte()
            buffer[offset + 6] = (lng shr 48).toByte()
            buffer[offset + 7] = (lng shr 56).toByte()

        }
        public fun BytesToLong(bytes: ByteArray, offset: Int): Long {
            return (
                    (bytes[offset + 7].toLong() shl 56) or
                            (bytes[offset + 6].toLong() and 0xff shl 48) or
                            (bytes[offset + 5].toLong() and 0xff shl 40) or
                            (bytes[offset + 4].toLong() and 0xff shl 32) or
                            (bytes[offset + 3].toLong() and 0xff shl 24) or
                            (bytes[offset + 2].toLong() and 0xff shl 16) or
                            (bytes[offset + 1].toLong() and 0xff shl 8) or
                            (bytes[offset + 0].toLong() and 0xff)
                    )
        }
        public fun IntToBytes(buffer: ByteArray, data: Int, offset: Int) {
            buffer[offset + 0] = (data shr 0).toByte()
            buffer[offset + 1] = (data shr 8).toByte()
            buffer[offset + 2] = (data shr 16).toByte()
            buffer[offset + 3] = (data shr 24).toByte()
        }
        public fun BytesToInt(buffer: ByteArray, offset: Int): Int {
            return (buffer[offset + 3].toInt() shl 24) or
                    (buffer[offset + 2].toInt() and 0xff shl 16) or
                    (buffer[offset + 1].toInt() and 0xff shl 8) or
                    (buffer[offset + 0].toInt() and 0xff)
        }
    }
}