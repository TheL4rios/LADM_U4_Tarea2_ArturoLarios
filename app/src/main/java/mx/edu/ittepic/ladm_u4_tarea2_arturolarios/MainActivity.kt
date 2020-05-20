package mx.edu.ittepic.ladm_u4_tarea2_arturolarios

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import mx.edu.ittepic.ladm_u3_practica1_arturolarios.Utils.Utils

class MainActivity : AppCompatActivity() {

    object Constants{
        const val READ_CALL = 10
        const val READ_SMS = 20
        const val SENT = "content://sms/sent/"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        grantPermission()

        btnMissedCalls.setOnClickListener {
            getMissedCalls()
        }

        btnDraftSMS.setOnClickListener {
            getSentSMS()
        }
    }

    private fun getSentSMS()
    {
        val cursor = contentResolver.query(Uri.parse(Constants.SENT), null, null, null, null)

        cursor?.let { c ->
            if (c.moveToFirst())
            {
                var messages = ""

                do {
                    var messageData = ""

                    for (i in (0 until c.columnCount))
                    {
                        messageData += "${c.getColumnName(i)} : ${c.getString(i)}\n"
                    }
                    messages += messageData + "\n --------------------- \n"
                } while (c.moveToNext())

                lbl.text = messages
            }
            else
            {
                Utils.showAlertMessage("Atención", "No hay mensajes enviados", this)
            }
        }
    }

    private fun getMissedCalls()
    {
        try {
            val uri = CallLog.Calls.CONTENT_URI

            val cursor = contentResolver.query(uri, null, null, null, null)

            cursor?.let {  c ->
                if (c.moveToFirst())
                {
                    var phones = ""

                    do {
                        if (c.getInt(c.getColumnIndex(CallLog.Calls.TYPE)) == CallLog.Calls.MISSED_TYPE)
                        {
                            phones += "${c.getString(c.getColumnIndex(CallLog.Calls.NUMBER))}\n -------------------------- \n"
                        }
                    } while (c.moveToNext())

                    if (phones.isEmpty())
                    {
                        lbl.text = "No hay registro de llamadas"
                        return
                    }

                    lbl.text = phones
                }
                else
                {
                    Utils.showAlertMessage("Atención", "No hay llamadas recibidas", this)
                }
            }
        }
        catch (e : SecurityException)
        {
            Utils.showAlertMessage("Atención", "Algo salió mal", this)
        }
    }

    private fun grantPermission()
    {
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_CALL_LOG), Constants.READ_CALL)
        }

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS), Constants.READ_SMS)
        }
    }
}
