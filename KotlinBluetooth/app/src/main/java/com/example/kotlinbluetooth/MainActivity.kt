package com.example.kotlinbluetooth

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.SeekBar
import android.widget.Toast

import com.example.kotlinbluetooth.R

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID


class MainActivity : AppCompatActivity() {

    internal lateinit var LampButton: Button
    internal lateinit var btnConnect: Button
    internal lateinit var PWMseekBar: SeekBar

    var connection: Boolean = false

    internal lateinit var connectedThread: ConnectedThread

    internal lateinit var adpBT: BluetoothAdapter
    internal lateinit var BTdevice: BluetoothDevice
    internal lateinit var BTSocket: BluetoothSocket

    internal var BT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LampButton = findViewById<View>(R.id.LampButton) as Button
        btnConnect = findViewById<View>(R.id.btnConnect) as Button

        PWMseekBar = findViewById<View>(R.id.seekBar) as SeekBar



        adpBT = BluetoothAdapter.getDefaultAdapter()

        if (adpBT == null) {
            Toast.makeText(applicationContext, "Dispositívo sem Bluetooth!", Toast.LENGTH_LONG).show()
        } else if (!adpBT!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        btnConnect.setOnClickListener {
            if (connection == true) {
                //desconectar
                try {
                    BTSocket!!.close()
                    connection = false
                    btnConnect.text = "Connect"

                    Toast.makeText(applicationContext, "Bluetooth desconectado.", Toast.LENGTH_LONG).show()
                } catch (erro: IOException) {
                    Toast.makeText(applicationContext, "DEU RUIM: $erro", Toast.LENGTH_LONG).show()
                }

            } else {
                //conectar
                val AbreLista = Intent(this@MainActivity, com.example.kotlinbluetooth.ListaDispositivos::class.java)
                startActivityForResult(AbreLista, REQUEST_CONEXAO)
            }
        }

        LampButton.setOnClickListener {
            if (connection!!) {
                connectedThread.write("a1")

            } else {
                Toast.makeText(applicationContext, "Bluetooth não conectado.", Toast.LENGTH_LONG).show()
            }
        }

        PWMseekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (connection!!) {
                    connectedThread.write("pwm" + seekBar.progress)

                } else {
                    Toast.makeText(applicationContext, "Bluetooth não conectado.", Toast.LENGTH_LONG).show()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data);

        when (requestCode) {
            REQUEST_ENABLE_BT -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(applicationContext, "Bluetooth ON", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(applicationContext, "Bluetooth not ON", Toast.LENGTH_LONG).show()
                }
                if (resultCode == Activity.RESULT_OK) {
                    MAC = data!!.extras!!.getString(com.example.kotlinbluetooth.ListaDispositivos.AdressM)

                    //Toast.makeText(getApplicationContext(), "CONECTOU: " + MAC, Toast.LENGTH_LONG).show();

                    BTdevice = adpBT!!.getRemoteDevice(MAC)

                    try {
                        BTSocket = BTdevice!!.createRfcommSocketToServiceRecord(BT_UUID)

                        BTSocket!!.connect()

                        connection = true

                        connectedThread = ConnectedThread(BTSocket!!)
                        connectedThread.start()

                        btnConnect.text = "Disconnect"

                        Toast.makeText(applicationContext, "CONECTOU: " + MAC!!, Toast.LENGTH_LONG).show()
                    } catch (erro: IOException) {

                        connection = false
                        Toast.makeText(applicationContext, "DEU RUIM: $erro", Toast.LENGTH_LONG).show() //RETIRAR EM VERSAO FINAL
                    }

                } else {
                    Toast.makeText(applicationContext, "DEU RUIM, NAO CONECTOU!1!", Toast.LENGTH_LONG).show()
                }
            }
            REQUEST_CONEXAO -> if (resultCode == Activity.RESULT_OK) {
                MAC = data!!.extras!!.getString(com.example.kotlinbluetooth.ListaDispositivos.AdressM)
                BTdevice = adpBT!!.getRemoteDevice(MAC)
                try {
                    BTSocket = BTdevice!!.createRfcommSocketToServiceRecord(BT_UUID)
                    BTSocket!!.connect()
                    connection = true
                    connectedThread = ConnectedThread(BTSocket!!)
                    connectedThread.start()
                    btnConnect.text = "Disconnect"
                    Toast.makeText(applicationContext, "CONECTOU: " + MAC!!, Toast.LENGTH_LONG).show()
                } catch (erro: IOException) {
                    connection = false
                    Toast.makeText(applicationContext, "DEU RUIM: $erro", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(applicationContext, "DEU RUIM, NAO CONECTOU!1!", Toast.LENGTH_LONG).show()
            }
        }
    }

    class ConnectedThread(socket: BluetoothSocket) : Thread() {
        // private final BluetoothSocket mmSocket;
        private val mmInStream: InputStream?
        private val mmOutStream: OutputStream?

        init {
            //   mmSocket = socket;
            var tmpIn: InputStream? = null
            var tmpOut: OutputStream? = null

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.inputStream
                tmpOut = socket.outputStream
            } catch (e: IOException) {
            }

            mmInStream = tmpIn
            mmOutStream = tmpOut
        }

        override fun run() {
            val buffer = ByteArray(1024)  // buffer store for the stream
            val bytes: Int // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            /* while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    // Send the obtained bytes to the UI activity
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                            .sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }*/
        }

        val byteArray = "Hello".toByteArray()

        /* Call this from the main activity to send data to the remote device */
        fun write(dadosenviar: String) {
            val msgBuffer = dadosenviar.toByteArray()
            try {
                mmOutStream!!.write(msgBuffer)
            } catch (e: IOException) {
            }

        }

    }

    companion object {

        private val REQUEST_ENABLE_BT = 1
        private val REQUEST_CONEXAO = 2

        private var MAC: String? = null
    }
}