/*
 * SPDX-FileCopyrightText: NONE
 *
 * SPDX-License-Identifier: Unlicense
 */
package name.kulakov444.wadbswitch

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.preference.PreferenceManager

class SwitchBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (!intent?.extras?.getString("flag").equals(context?.let { PreferenceManager.getDefaultSharedPreferences(it) }
            ?.getString("flag", null)))
            return
        val key = "adb_wifi_enabled"
        intent?.extras?.getInt("value")?.let {
            Settings.Global.putInt(context?.contentResolver,key,
                it
            )
            Log.i(javaClass.simpleName, "Set $key to $it")
        }
    }
}
