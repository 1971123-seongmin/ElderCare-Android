package com.example.elder_care.utils.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

@SuppressLint("MissingPermission")
class ConnectThread(
    private val myUUID: UUID,
    private val device: BluetoothDevice,
) : Thread() {

    companion object {
        private const val TAG = "BluetoothService"
    }

    private var bluetoothSocket: BluetoothSocket? = null
    private lateinit var inputStream: InputStream
    private lateinit var outputStream: OutputStream

    override fun run() {
        // 소켓 연결 시도
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID)
            bluetoothSocket?.connect()
            Log.d(TAG, "Socket connected")
            manageConnectedSocket(bluetoothSocket!!)
        } catch (e: IOException) {
            Log.e(TAG, "Socket connection failed", e)
            try {
                bluetoothSocket?.close()
            } catch (closeException: IOException) {
                Log.e(TAG, "Failed to close socket", closeException)
            }
        }
    }

    // 연결된 소켓을 관리하고 데이터 수신을 담당하는 메서드
    private fun manageConnectedSocket(socket: BluetoothSocket) {
        try {
            inputStream = socket.inputStream
            outputStream = socket.outputStream

            // 데이터 수신을 위한 무한 루프
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    bytes = inputStream.read(buffer)
                    val receivedMessage = String(buffer, 0, bytes)
                    Log.d(TAG, "Received: $receivedMessage")

                    // 여기서 receivedMessage를 UI나 다른 곳으로 전달할 수 있음

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
            bluetoothSocket?.close()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close socket", e)
        }
    }
}