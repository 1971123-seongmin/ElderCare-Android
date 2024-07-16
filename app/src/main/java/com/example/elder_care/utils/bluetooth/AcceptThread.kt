package com.example.elder_care.utils.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

// 블루투스에서 서버의 역할을 수행하는 스레드
class AcceptThread(private val bluetoothAdapter: BluetoothAdapter): Thread() {

    private lateinit var serverSocket: BluetoothServerSocket

    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    companion object {
        private const val TAG = "ACCEPT_THREAD"
        private const val SOCKET_NAME = "server"
        private val MY_UUID = UUID.fromString("00001101-0000-1000-8000-70dc7c4fffec5b05")
    }

    init {
        try {
            // 서버 소켓
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(SOCKET_NAME, MY_UUID)
        } catch(e: Exception) {
            Log.d(TAG, e.message.toString())
        }
    }

    override fun run() {
        var socket: BluetoothSocket? = null

        while (true) {
            socket = try {
                // 클라이언트 소켓 수락
                serverSocket.accept()
            } catch (e: IOException) {
                Log.e(TAG, "Socket's accept() method failed", e)
                break
            }

            socket?.also {
                manageConnectedSocket(it)
                serverSocket.close()
            }
        }
    }

    fun manageConnectedSocket(socket: BluetoothSocket) {
        inputStream = socket.inputStream
        outputStream = socket.outputStream

        val buffer = ByteArray(1024)
        var numBytes: Int

        while (true) {
            numBytes = try {
                inputStream.read(buffer)
            } catch (e: IOException) {
                Log.e(TAG, "Input stream was disconnected", e)
                break
            }

            val readMessage = String(buffer, 0, numBytes)
            Log.d(TAG, "Received: $readMessage")
            // 필요한 데이터 처리 로직 추가
        }
    }

    fun cancel() {
        try {
            serverSocket.close()
        } catch (e: IOException) {
            Log.d(TAG, e.message.toString())
        }
    }
}