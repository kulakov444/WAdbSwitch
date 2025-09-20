/*
 * SPDX-FileCopyrightText: NONE
 *
 * SPDX-License-Identifier: Unlicense
 */

package name.kulakov444.wadbswitch

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import java.security.SecureRandom
import java.util.Base64

fun resetFlag(context: Context){
    val rng = SecureRandom()
    val key = ByteArray(16)
    rng.nextBytes(key)
    val flag = Base64.getEncoder().encodeToString(key)
    PreferenceManager.getDefaultSharedPreferences(context).edit { putString("flag", flag) }
}

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        if (preferenceManager.sharedPreferences?.getString("flag", null) == null)
            resetFlag(requireContext())
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val settings = SettingsFragment()
        if (savedInstanceState==null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, settings)
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        val settings = supportFragmentManager.findFragmentById(R.id.settings) as SettingsFragment
        settings.findPreference<Preference>("copy_flag")
            ?.setOnPreferenceClickListener {
                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val flag = PreferenceManager.getDefaultSharedPreferences(this).getString("flag", null)
                val clip = ClipData.newPlainText("Flag", flag)
                clipboard.setPrimaryClip(clip)
                true
            }
        settings.findPreference<Preference>("reset_flag")
            ?.setOnPreferenceClickListener {
                resetFlag(this)
                true
            }

    }

    override fun onResume() {
        super.onResume()
        val settings = supportFragmentManager.findFragmentById(R.id.settings) as SettingsFragment
        val hasPermission = settings.findPreference<SwitchPreferenceCompat>("has_permission")
        val granted = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SECURE_SETTINGS) ==
                PackageManager.PERMISSION_GRANTED
        hasPermission?.isChecked = granted
        hasPermission?.summary = if (granted) "" else getString(R.string.required_permission)
    }
}

