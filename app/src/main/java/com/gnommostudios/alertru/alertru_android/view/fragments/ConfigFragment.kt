package com.gnommostudios.alertru.alertru_android.view.fragments

import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import android.preference.CheckBoxPreference
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.gnommostudios.alertru.alertru_android.R

class ConfigFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        prefs = activity.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        val fm = activity.fragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.fragment_config, MyPreferenceFragment())
        ft.commit()

        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_config, container, false)
    }

    class MyPreferenceFragment : PreferenceFragment() {
        private var mp: MediaPlayer? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.configuration)

            val editor = prefs!!.edit()

            ringote = findPreference("ringote") as ListPreference
            activate = findPreference("activateAlert") as CheckBoxPreference

            if (ringote!!.value == null)
                ringote!!.setValueIndex(0)

            when (ringote!!.value) {
                "nu" -> ringote!!.summary = "Nuclear"
                "be" -> ringote!!.summary = "Beep"
                "a1" -> ringote!!.summary = "Alarm1"
                "a2" -> ringote!!.summary = "Alarm2"
                else -> ringote!!.summary = "Default"
            }

            ringote!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                if (activate!!.isChecked) {
                    //Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();
                    editor.putString("ringote", newValue as String)
                    editor.putBoolean("activateAlert", activate!!.isChecked)
                    editor.commit()
                }

                choiceRingote(newValue as String)
                true
            }

            activate!!.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { preference, newValue ->
                if (newValue as Boolean) {
                    //Toast.makeText(getActivity(), "Check", Toast.LENGTH_SHORT).show();
                    editor.putString("ringote", ringote!!.value)
                } else {
                    //Toast.makeText(getActivity(), "Uncheck", Toast.LENGTH_SHORT).show();
                    editor.putString("ringote", "def")
                }
                editor.putBoolean("activateAlert", newValue)
                editor.commit()

                true
            }

        }

        private fun choiceRingote(option: String) {
            if (mp != null) {
                mp!!.stop()
            }

            when (option) {
                "nu" -> {
                    mp = MediaPlayer.create(activity, R.raw.nuclear)
                    ringote!!.summary = "Nuclear"
                }
                "be" -> {
                    mp = MediaPlayer.create(activity, R.raw.beep)
                    ringote!!.summary = "Beep"
                }
                "a1" -> {
                    mp = MediaPlayer.create(activity, R.raw.alarm1)
                    ringote!!.summary = "Alarm1"
                }
                "a2" -> {
                    mp = MediaPlayer.create(activity, R.raw.alarm2)
                    ringote!!.summary = "Alarm2"
                }
                else -> {
                    mp = MediaPlayer.create(activity, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    ringote!!.summary = "Default"
                }
            }

            mp!!.start()
        }

        companion object {

            internal var ringote: ListPreference? = null
            internal var activate: CheckBoxPreference? = null
        }
    }

    companion object {

        internal var prefs: SharedPreferences? = null
    }

}
