package com.example.elder_care.utils.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.UUID

class ConnectThread(
    private val myUUID: UUID,
    private val device: BluetoothDevice,
    private val viewModel: BluetoothDataViewModel
) : Thread() {

    companion object {
        private const val TAG = "BluetoothService"
    }

    // BluetoothDevice 로부터 connectSocket 획득
    private val connectSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(myUUID)
    }

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    override fun run() {
        try {
            connectSocket?.connect()
            Log.d(TAG, "Socket connected")
            manageConnectedSocket(connectSocket!!)
        } catch (e: IOException) {
            Log.e(TAG, "Socket connection failed", e)
            try {
                connectSocket?.close()
            } catch (closeException: IOException) {
                Log.e(TAG, "Failed to close socket", closeException)
            }
        }
    }

    // 연결된 소켓을 관리 및 데이터 수신 메서드
    private fun manageConnectedSocket(socket: BluetoothSocket) {
        try {
            inputStream = socket.inputStream
            outputStream = socket.outputStream

            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    Log.d("BluetoothService", "Bytes read: $bytes")

                    if (bytes > 0) {
                        val receivedData = String(buffer, 0, bytes, Charsets.UTF_8).trim()
                        Log.d(TAG, "Received: $receivedData")
                    }

                } catch (e: IOException) {
                    Log.e(TAG, "Error reading from input stream", e)
                    break
                }
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error managing connected socket", e)
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to close socket", e)
            }
        }
    }


    // 데이터를 전송하는 메서드
    fun write(data: ByteArray) {
        try {
            outputStream.write(data)
            Log.d(TAG, "Sent: ${String(data)}")
        } catch (e: IOException) {
            Log.e(TAG, "Error writing to output stream", e)
        }
    }

    // 연결을 종료하는 메서드
    fun cancel() {
        try {
            connectSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close socket", e)
        }
    }
}