package com.example.elder_care.utils

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
        private const val TAG = "CONNECT_THREAD"
    }

    // BluetoothDevice 로부터 BluetoothSocket 획득
    private val connectSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        device.createRfcommSocketToServiceRecord(myUUID)
    }

    override fun run() {
        try {
            // 연결 수행
            connectSocket?.connect()
            connectSocket?.let {
                val connectedThread = ConnectedThread(bluetoothSocket = it)
                connectedThread.start()
            }
        } catch (e: IOException) { // 기기와의 연결이 실패할 경우 호출
            connectSocket?.close()
            throw Exception("연결 실패")
        }
    }

    // 블루투스 연결 취소, 소켓 닫음
    fun cancel() {
        try {
            connectSocket?.close()
        } catch (e: IOException) {
            Log.d(TAG, e.message.toString())
        }
    }

    // Bluetooth 기기와 연결된 후 데이터 통신 수행
    private inner class ConnectedThread(private val bluetoothSocket: BluetoothSocket) : Thread() {
        private lateinit var inputStream: InputStream
        private lateinit var outputStream: OutputStream

        init {
            try {
                inputStream = bluetoothSocket.inputStream
                outputStream = bluetoothSocket.outputStream
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
            }
        }

        override fun run() {
            val buffer = ByteArray(1024)
            var bytes: Int

            while (true) {
                try {
                    // 데이터 받기(읽기)
                    bytes = inputStream.read(buffer)
                    Log.d(TAG, bytes.toString())
                } catch (e: Exception) {
                    Log.d(TAG, "기기와의 연결이 끊겼습니다.")
                    break
                }
            }
        }

        fun write(bytes: ByteArray) {
            try {
                // 데이터 전송
                outputStream.write(bytes)
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
            }
        }

        fun cancel() {
            try {
                bluetoothSocket.close()
            } catch (e: IOException) {
                Log.d(TAG, e.message.toString())
            }
        }
    }
}