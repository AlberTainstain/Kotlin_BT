package com.example.kotlinbluetooth


import android.app.Activity
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.substring
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class ListaDispositivos : ListActivity() {


    private lateinit var adpBT2: BluetoothAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ArrayBluetooth = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)

        adpBT2 = BluetoothAdapter.getDefaultAdapter()

        val dispositivospareados = adpBT2!!.bondedDevices

        if (dispositivospareados.size > 0) {
            for (dispositivo in dispositivospareados) {
                val nomeBt = dispositivo.name
                val macBt = dispositivo.address
                ArrayBluetooth.add(nomeBt + "\n" + macBt)
            }
            listAdapter = ArrayBluetooth
        }
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)

        val GeralInfo = (v as TextView).text.toString()

        // Toast.makeText(getApplicationContext(), "Info():" + GeralInfo, Toast.LENGTH_LONG).show();

        val MacAdress = GeralInfo.substring(GeralInfo.length - 17)

        // Toast.makeText(getApplicationContext(), "Info():" + MacAdress, Toast.LENGTH_LONG).show();

        val ReturnMac = Intent()
        ReturnMac.putExtra(AdressM, MacAdress)
        setResult(Activity.RESULT_OK, ReturnMac)
        finish()
    }

    companion object {
        internal var AdressM: String? = null
}
}