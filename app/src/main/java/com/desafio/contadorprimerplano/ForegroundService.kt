package com.desafio.contadorprimerplano

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log

class ForegroundService : Service() {
    private var isServiceRunning = false
    private var counter = 0
    private var mHandler: Handler? = null
    private val MESSAGE_FROM_SERVICE = 1

    private val localBinder : LocalBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        // Iniciar la lógica en segundo plano en un hilo aquí
        // Puedes usar un Thread o un Coroutine para realizar tareas en segundo plano
        // y enviar actualizaciones al MainActivity a través del handler.
        Log.i("myService", "onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("myService", "starCommand $flags $isServiceRunning")
        if(!isServiceRunning){
            isServiceRunning = true
            startForegroundServiceLogic()

        }

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        Log.i("myService", "stopservice $isServiceRunning")
        isServiceRunning = false
        return super.stopService(name)
    }

    override fun onBind(intent: Intent): IBinder {
        return localBinder
    }

    fun stopService() {
        isServiceRunning = false
        this.stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
        if(mHandler != null){
            mHandler = null
        }
        // Detener la lógica en segundo plano aquí
    }

    // Este método ejecuta la lógica en segundo plano
    private fun startForegroundServiceLogic() {
        Log.i("myService", "counterService")
        Thread {
            while (isServiceRunning) {
                counter++
                // Envía un mensaje al handler en MainActivity
                val message = Message.obtain()
                message.what = MESSAGE_FROM_SERVICE
                message.arg1 = counter
                mHandler?.sendMessage(message)
                // Realiza alguna tarea en segundo plano aquí
                try {
                    Thread.sleep(1000) // Espera 7 segundos
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
        }.start()
    }

    fun setHandler(handler: Handler){
        mHandler = handler
    }

    inner class LocalBinder : Binder(){
        fun getInstance() : ForegroundService {
            return this@ForegroundService
        }
    }

}
