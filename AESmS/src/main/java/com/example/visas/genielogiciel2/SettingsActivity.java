package com.example.visas.genielogiciel2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    @SuppressLint("ValidFragment")
    public class MainPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener{
        ArrayList<String> items_puces;
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference);

            // gallery EditText change listener
            bindPreferenceSummaryToValue(findPreference("lien_serveur"));

            final ListPreference lp = setListPreferenceData((ListPreference) findPreference("listSim1"), getActivity());

            lp.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(lp, getActivity());
                    return false;
                }
            });
            final ListPreference pref = setListPreferenceData((ListPreference) findPreference("listSim2"), getActivity());

            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(pref, getActivity());
                    return false;
                }
            });
            final ListPreference prefSim = setListPreferenceData((ListPreference) findPreference("listSim3"), getActivity());

            prefSim.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    setListPreferenceData(prefSim, getActivity());
                    return false;
                }
            });
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(lp);
            // notification preference change listener
            bindPreferenceSummaryToValue(findPreference("listSim1"));
            bindPreferenceSummaryToValue(findPreference("listSim2"));
            bindPreferenceSummaryToValue(findPreference("listSim3"));
        }
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
        protected ListPreference setListPreferenceData(ListPreference preference, Activity mActivity) {

            items_puces = new ArrayList<>();
            items_puces.add("aucune");
            SubscriptionManager subs = (SubscriptionManager) getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            for (SubscriptionInfo s : subs.getActiveSubscriptionInfoList()) {
                items_puces.add(String.valueOf(s.getDisplayName()));
            }
            ListPreference lp = (ListPreference) preference;
            CharSequence[] cs = items_puces.toArray(new CharSequence[items_puces.size()]);
            lp.setEntries(cs);
            lp.setEntryValues(cs);
            if (items_puces.size() == 2) {
                lp.setDefaultValue(cs[1]);
            }
            return lp;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("listSim1") || key.equals("listSim2") || key.equals("listSim3") ){
                Preference pref=findPreference(key);
                pref.setSummary(sharedPreferences.getString(key,""));
                Toast.makeText(getContext(),"preference : "+sharedPreferences.getString(key,""),Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }  else if (preference instanceof EditTextPreference) {
                if (preference.getKey().equals("lien_serveur")) {
                    // update the changed gallery name to summary filed
                    preference.setSummary(stringValue);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    };


}
