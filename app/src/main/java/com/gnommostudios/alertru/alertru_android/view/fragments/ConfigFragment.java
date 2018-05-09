package com.gnommostudios.alertru.alertru_android.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gnommostudios.alertru.alertru_android.R;

public class ConfigFragment extends Fragment {

    static SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        prefs = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        FragmentManager fm = getActivity().getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_config, new MyPreferenceFragment());
        ft.commit();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_config, container, false);
    }

    public void choiceRingote(String option) {
        switch (option) {
            case "nu":
                Toast.makeText(getActivity(), "Nuclear", Toast.LENGTH_SHORT).show();
                break;
            case "be":
                Toast.makeText(getActivity(), "Beep", Toast.LENGTH_SHORT).show();
                break;
            case "a1":
                Toast.makeText(getActivity(), "Alarm1", Toast.LENGTH_SHORT).show();
                break;
            case "a2":
                Toast.makeText(getActivity(), "Alarm2", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(getActivity(), "Default", Toast.LENGTH_SHORT).show();
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragment {

        static ListPreference ringote;
        static CheckBoxPreference activate;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.configuration);

            final SharedPreferences.Editor editor = prefs.edit();

            ringote = (ListPreference) findPreference("ringote");
            activate = (CheckBoxPreference) findPreference("activateAlert");

            if (ringote.getValue() == null)
                ringote.setValueIndex(0);

            ringote.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (activate.isChecked()) {
                        //Toast.makeText(getActivity(), "Checked", Toast.LENGTH_SHORT).show();
                        editor.putString("ringote", (String) newValue);
                        editor.putBoolean("activateAlert", activate.isChecked());
                        editor.commit();
                    }

                    //choiceRingote((String) newValue);

                    return true;
                }
            });

            activate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((boolean) newValue) {
                        //Toast.makeText(getActivity(), "Check", Toast.LENGTH_SHORT).show();
                        editor.putString("ringote", ringote.getValue());
                    }else {
                        //Toast.makeText(getActivity(), "Uncheck", Toast.LENGTH_SHORT).show();
                        editor.putString("ringote", "def");
                    }
                    editor.putBoolean("activateAlert", (boolean) newValue);
                    editor.commit();

                    return true;
                }
            });

        }

        private void choiceRingote(String option) {
            switch (option) {
                case "nu":
                    Toast.makeText(getActivity(), "Nuclear", Toast.LENGTH_SHORT).show();
                    break;
                case "be":
                    Toast.makeText(getActivity(), "Beep", Toast.LENGTH_SHORT).show();
                    break;
                case "a1":
                    Toast.makeText(getActivity(), "Alarm1", Toast.LENGTH_SHORT).show();
                    break;
                case "a2":
                    Toast.makeText(getActivity(), "Alarm2", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(getActivity(), "Default", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
