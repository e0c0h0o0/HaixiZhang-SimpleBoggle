package cs501.boggle

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.TelephonyManager

class SecretCodeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action.equals("android.provider.Telephony.SECRET_CODE")) {
            val code = intent.data!!.host
            if (code == "2333") {
                val mainActivityIntent = Intent(context, MainActivity::class.java)
                mainActivityIntent.putExtra("flagPreload", true)
                mainActivityIntent.setAction("com.example.action.START_ACTIVITY");
                mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mainActivityIntent)
            }
        }
    }

}