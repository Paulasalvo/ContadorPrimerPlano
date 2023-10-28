package com.desafio.contadorprimerplano

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.desafio.contadorprimerplano.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), Handler.Callback {
    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper(), this)
    private val MESSAGE_FROM_SERVICE = 1
    private var isServiceRunning = false

    private var foregroundService : ForegroundService? = null
    private var isBound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        doBindService()

        // Configurar el botón para iniciar/detener el servicio
        binding.startStopButton.setOnClickListener {
            if (isServiceRunning) {
                // Detener el servicio
                Log.i("myService", "stopService")
                val intent = Intent(this, ForegroundService::class.java)
                foregroundService?.stopService()
                isServiceRunning = false
                binding.startStopButton.text = "START"
            } else {
                // Iniciar el servicio
                Log.i("myService", "starService")
                val intent = Intent(this, ForegroundService::class.java)
                startService(intent)
                isServiceRunning = true
                binding.startStopButton.text = "STOP"
            }
        }
    }

    val connection : ServiceConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            foregroundService = (binder as? ForegroundService.LocalBinder)?.getInstance()
            foregroundService?.setHandler(handler)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
           foregroundService = null
        }

    }

    private fun doBindService() {
        val intent = Intent(this, ForegroundService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
        isBound = true
    }

    private fun doUnbindService() {
        if(isBound){
            unbindService(connection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        doUnbindService()
    }

    override fun handleMessage(msg: Message): Boolean {
        Log.i("myService", "Message")
        if (msg.what == MESSAGE_FROM_SERVICE) {
            val counterValue = msg.arg1
            updateUI(counterValue)
        }
        return true
    }

    // Actualizar la interfaz de usuario con el valor del contador
    private fun updateUI(counterValue: Int) {
        runOnUiThread {
            binding.counterTextView.text = "Contador: $counterValue"
        }
    }
}
