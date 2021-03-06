package com.deathsnacks.wardroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import com.deathsnacks.wardroid.R;
import com.deathsnacks.wardroid.fragments.InvasionFragment;
import com.deathsnacks.wardroid.fragments.SettingsFragment;
import com.deathsnacks.wardroid.utils.PreferenceActivityCallbacks;

public class SettingsActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment(), "settings").commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PreferenceActivityCallbacks callback = null;
        Fragment f = this.getSupportFragmentManager().findFragmentByTag("settings");
        if ( f instanceof PreferenceActivityCallbacks)
            callback = (PreferenceActivityCallbacks)  f;
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, data);
        }
    }
}
//
//import android.app.AlarmManager;
//import android.app.AlertDialog;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.pm.PackageInfo;
//import android.content.pm.PackageManager;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.SystemClock;
//import android.preference.CheckBoxPreference;
//import android.preference.Preference;
//import android.preference.PreferenceActivity;
//import android.preference.PreferenceManager;
//import android.support.v4.app.NotificationCompat;
//
//import com.deathsnacks.wardroid.utils.UnifiedPreferenceUtils;
//
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.Window;
//import android.widget.Toast;
//
//import com.deathsnacks.wardroid.Constants;
//import com.deathsnacks.wardroid.R;
//import com.deathsnacks.wardroid.services.NotificationsUpdateReceiver;
//import com.deathsnacks.wardroid.services.PollingAlarmReceiver;
//import com.deathsnacks.wardroid.utils.Http;
//import com.deathsnacks.wardroid.utils.PreferenceUtils;
//import com.deathsnacks.wardroid.utils.preferences.MultiSelectListPreference;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//
//import java.io.IOException;
//
///**
// * Created by Admin on 30/01/14.
// */
//public class SettingsActivity extends PreferenceActivity {
//    private static final String TAG = "SettingsActivity";
//    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
//    private SharedPreferences mPreferences;
//    private static boolean mForceChangingPush;
//    private SharedPreferences.Editor mEditor;
//
//    @Override
//    @SuppressWarnings("deprecation")
//    public void onCreate(Bundle paramBundle) {
//        super.onCreate(paramBundle);
//        mForceChangingPush = false;
//        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        mEditor = mPreferences.edit();
//        mPreferences.registerOnSharedPreferenceChangeListener(prefChanged);
//        addPreferencesFromResource(R.xml.preference);
//
//        if (mPreferences.getInt("reward_version", 0) > getResources().getInteger(R.integer.reward_version)) {
//            Log.d(TAG, "Updating pref entries.");
//            try {
//                MultiSelectListPreference aura = (MultiSelectListPreference) findPreference(Constants.PREF_AURA_FILTERS);
//                aura.setEntryValues(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("aura_entries", "")));
//                aura.setEntries((PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("aura_entries", ""))));
//                MultiSelectListPreference bp = (MultiSelectListPreference) findPreference(Constants.PREF_BP_FILTERS);
//                bp.setEntryValues(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("bp_entries", "")));
//                bp.setEntries((PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("bp_entries", ""))));
//                MultiSelectListPreference mod = (MultiSelectListPreference) findPreference(Constants.PREF_MOD_FILTERS);
//                mod.setEntryValues(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("mod_entries", "")));
//                mod.setEntries((PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("mod_entries", ""))));
//                MultiSelectListPreference misc = (MultiSelectListPreference) findPreference(Constants.PREF_RESOURCE_FILTERS);
//                misc.setEntryValues(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("misc_entries", "")));
//                misc.setEntries((PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("misc_entries", ""))));
//                MultiSelectListPreference planet = (MultiSelectListPreference) findPreference(Constants.PREF_PLANET_FILTERS);
//                planet.setEntryValues(PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("planet_entries", "")));
//                planet.setEntries((PreferenceUtils.fromPersistedPreferenceValue(mPreferences.getString("planet_entries", ""))));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        Preference pushPref = findPreference(Constants.PREF_PUSH);
//        if (pushPref != null) {
//            pushPref.setOnPreferenceChangeListener(pushPrefListener);
//            if (!checkPlayServices()) {
//                pushPref.setEnabled(false);
//                pushPref.setSummary(getString(R.string.push_disabled_summary));
//            }
//        }
//        Preference customPref = findPreference("custom_button");
//        if (customPref != null) {
//            customPref.setIntent(new Intent(getApplicationContext(), CustomFilterActivity.class));
//        }
//        Preference clearDataPref = findPreference("clear_data");
//        if (clearDataPref != null) {
//            clearDataPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                @Override
//                public boolean onPreferenceClick(Preference preference) {
//                    new AlertDialog.Builder(getApplicationContext())
//                            .setTitle(getString(R.string.clear_data_title))
//                            .setMessage(getString(R.string.clear_data_message))
//                            .setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    SharedPreferences.Editor edit = mPreferences.edit();
//                                    edit.clear();
//                                    edit.commit();
//                                    Intent intent = getIntent();
//                                    overridePendingTransition(0, 0);
//                                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                                    finish();
//                                    overridePendingTransition(0, 0);
//                                    startActivity(intent);
//                                }
//                            })
//                            .setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.cancel();
//                                }
//                            }).show();
//                    return false;
//                }
//            });
//        }
//        UnifiedPreferenceUtils.bindAllPreferenceSummariesToValues(getPreferenceScreen());
//    }
//
//    Preference.OnPreferenceChangeListener pushPrefListener = new Preference.OnPreferenceChangeListener() {
//        @Override
//        public boolean onPreferenceChange(Preference pushPref, Object o) {
//            Log.d(TAG, "push force:" + mForceChangingPush);
//            boolean newpref = (Boolean) o;
//            if (mForceChangingPush) {
//                Log.d(TAG, "push pref force change, ignoring.");
//                mForceChangingPush = false;
//                return false;
//            }
//            pushPref.setEnabled(false);
//            if (newpref) {
//                String regid = getRegistrationId(getApplicationContext());
//
//                if (regid.trim().length() == 0) {
//                    registerInBackground();
//                } else {
//                    pushPref.setEnabled(true);
//                }
//            } else {
//                unregisterInBackground();
//            }
//            return true;
//        }
//    };
//
//    @Override
//    public void onDestroy() {
//        mPreferences.unregisterOnSharedPreferenceChangeListener(prefChanged);
//        super.onDestroy();
//    }
//
//    @SuppressWarnings("deprecation")
//    SharedPreferences.OnSharedPreferenceChangeListener prefChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {
//        @Override
//        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
//            Log.d(TAG, "changed pref: " + s);
//            if (s.equals(Constants.PREF_ALERT_ENABLED)) {
//                if (sharedPreferences.getBoolean(Constants.PREF_ALERT_ENABLED, false)) {
//                    Log.d(TAG, "starting alarm since pref was changed");
//                    boolean mDismissible = !sharedPreferences.getBoolean("dismissible", false);
//                    Intent alarmIntent = new Intent(getApplicationContext(), PollingAlarmReceiver.class);
//                    PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
//                            .setSmallIcon(R.drawable.ic_notification)
//                            .setContentTitle(getString(R.string.notification_title))
//                            .setContentText(getString(R.string.notification_starting))
//                            .setOngoing(mDismissible);
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    intent.putExtra("drawer_position", 1);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                    PendingIntent pendingIntent2 = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                    mBuilder.setContentIntent(pendingIntent2);
//                    mNotificationManager.notify(1, mBuilder.build());
//                    try {
//                        (new PollingAlarmReceiver()).onReceive(getApplicationContext(), new Intent().putExtra("force", true));
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    if (!mPreferences.getBoolean(Constants.PREF_PUSH, false)) {
//                        ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
//                                SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
//                    }
//                } else {
//                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                    mNotificationManager.cancel(1);
//                    ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
//                            getApplicationContext(), 0, new Intent(getApplicationContext(), PollingAlarmReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
//                    ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(PendingIntent.getBroadcast(
//                            getApplicationContext(), 0,
//                            new Intent(getApplicationContext(), NotificationsUpdateReceiver.class), PendingIntent.FLAG_UPDATE_CURRENT));
//                }
//            } else if (s.equals("platform") || s.equals(Constants.PREF_PLATFORM_NOTIFICATIONS)) {
//                String persist = mPreferences.getString(s, "pc|ps4");
//                MultiSelectListPreference platform = (MultiSelectListPreference) findPreference(s);
//                if (!persist.contains("pc") && !persist.contains("ps4")) {
//                    Toast.makeText(getApplicationContext(), "You must select at least one platform, defaulting to PC and PS4.", Toast.LENGTH_SHORT).show();
//                    SharedPreferences.Editor editor = mPreferences.edit();
//                    editor.putString(s, "pc|ps4");
//                    editor.commit();
//                    platform.setValue("pc|ps4");
//                }
//            }
//            Preference preference = findPreference(s);
//
//        }
//    };
//
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.home:
//            case R.id.homeAsUp:
//            case android.R.id.home:
//                //NavUtils.navigateUpFromSameTask(this);
//                super.onBackPressed();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Check the device to make sure it has the Google Play Services APK. If
//     * it doesn't, display a dialog that allows users to download the APK from
//     * the Google Play Store or enable it in the device's system settings.
//     */
//    private boolean checkPlayServices() {
//        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            }
//            return false;
//        }
//        return true;
//    }
//
//    private String getRegistrationId(Context context) {
//        return getRegistrationId(context, true);
//    }
//
//    /**
//     * Gets the current registration ID for application on GCM service.
//     * <p/>
//     * If result is empty, the app needs to register.
//     *
//     * @return registration ID, or empty string if there is no existing
//     * registration ID.
//     */
//    private String getRegistrationId(Context context, boolean versionCheck) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        String registrationId = prefs.getString("gcm_reg_id", "");
//        Log.d(TAG, "regId=" + registrationId);
//        if (registrationId.trim().length() == 0) {
//            Log.i(TAG, "Registration not found.");
//            return "";
//        }
//        // Check if app was updated; if so, it must clear the registration ID
//        // since the existing regID is not guaranteed to work with the new
//        // app version.
//        int registeredVersion = prefs.getInt("gcm_app_version", Integer.MIN_VALUE);
//        int currentVersion = getAppVersion(context);
//        if (registeredVersion != currentVersion && versionCheck) {
//            Log.i(TAG, "App version changed.");
//            return "";
//        }
//        return registrationId;
//    }
//
//    /**
//     * @return Application's {@code SharedPreferences}.
//     */
//    private SharedPreferences getGCMPreferences(Context context) {
//        // This sample app persists the registration ID in shared preferences, but
//        // how you store the regID in your app is up to you.
//        return getSharedPreferences("gcm", Context.MODE_PRIVATE);
//    }
//
//    /**
//     * @return Application's version code from the {@code PackageManager}.
//     */
//    private static int getAppVersion(Context context) {
//        try {
//            PackageInfo packageInfo = context.getPackageManager()
//                    .getPackageInfo(context.getPackageName(), 0);
//            return packageInfo.versionCode;
//        } catch (PackageManager.NameNotFoundException e) {
//            // should never happen
//            throw new RuntimeException("Could not get package name: " + e);
//        }
//    }
//
//    private void registerInBackground() {
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                String msg = "";
//                try {
//                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                    String regid = gcm.register("338009375920");
//                    msg = "Device registered, registration ID=" + regid;
//
//                    sendRegistrationIdToBackend(regid);
//
//                    storeRegistrationId(getApplicationContext(), regid);
//                } catch (IOException ex) {
//                    //if we fail and attempt to register again, we get the same id, so no problem.
//                    msg = "Error :" + ex.getMessage();
//                    ex.printStackTrace();
//                    Log.e(TAG, ex.getMessage());
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                Log.d(TAG, "register msg:" + msg);
//                CheckBoxPreference pushPref = (CheckBoxPreference) findPreference(Constants.PREF_PUSH);
//                pushPref.setEnabled(true);
//                if (msg.startsWith("Error :") && !msg.contains("already exists")) {
//                    Toast.makeText(getApplicationContext(), "Error occurred while trying to register for push notifications.", Toast.LENGTH_LONG).show();
//                    pushPref.setChecked(false);
//                    mForceChangingPush = true;
//                    pushPref.setOnPreferenceChangeListener(null);
//                    SharedPreferences.Editor editor = mPreferences.edit();
//                    editor.putBoolean(Constants.PREF_PUSH, false);
//                    editor.commit();
//                }
//                pushPref.setOnPreferenceChangeListener(pushPrefListener);
//            }
//        }.execute();
//    }
//
//    private void unregisterInBackground() {
//        new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                String msg = "";
//                try {
//                    String regid = getRegistrationId(getApplicationContext(), false);
//                    removeRegistrationIdFromBackend(regid);
//                    removeRegistrationId(getApplicationContext());
//                } catch (IOException ex) {
//                    msg = "Error :" + ex.getMessage();
//                    ex.printStackTrace();
//                    Log.e(TAG, ex.getMessage());
//                }
//                return msg;
//            }
//
//            @Override
//            protected void onPostExecute(String msg) {
//                Log.d(TAG, "unregister msg:" + msg);
//                CheckBoxPreference pushPref = (CheckBoxPreference) findPreference(Constants.PREF_PUSH);
//                pushPref.setEnabled(true);
//                if (msg.startsWith("Error :")) {
//                    Toast.makeText(getApplicationContext(), "Error occurred while trying to unregister from push notifications.", Toast.LENGTH_LONG).show();
//                    pushPref.setChecked(true);
//                    pushPref.setOnPreferenceChangeListener(null);
//                    mForceChangingPush = true;
//                    SharedPreferences.Editor editor = mPreferences.edit();
//                    editor.putBoolean(Constants.PREF_PUSH, true);
//                    editor.commit();
//                }
//                pushPref.setOnPreferenceChangeListener(pushPrefListener);
//            }
//        }.execute();
//    }
//
//    private void storeRegistrationId(Context context, String regId) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        int appVersion = getAppVersion(context);
//        Log.i(TAG, "Saving regId on app version " + appVersion);
//        Log.i(TAG, "regId=" + regId);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString("gcm_reg_id", regId);
//        editor.putInt("gcm_app_version", appVersion);
//        editor.commit();
//    }
//
//    private void removeRegistrationId(Context context) {
//        final SharedPreferences prefs = getGCMPreferences(context);
//        SharedPreferences.Editor editor = prefs.edit();
//        Log.i(TAG, "removing reg id.");
//        editor.remove("gcm_reg_id");
//        editor.remove("gcm_app_version");
//        editor.commit();
//    }
//
//    /**
//     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP
//     * to send messages to your app.
//     */
//    private void sendRegistrationIdToBackend(String id) throws IOException {
//        String response = Http.get("http://deathsnacks.com/api/wardroid/registerPush.php?id=" + id);
//        if (!response.contains("success:") && !response.contains("already exists"))
//            throw new IOException("Failed to send gcm id back to server. " + response);
//    }
//
//    /**
//     * Removes the registration ID to your server over HTTP.
//     */
//    private void removeRegistrationIdFromBackend(String id) throws IOException {
//        String response = Http.get("http://deathsnacks.com/api/wardroid/unregisterPush.php?id=" + id);
//        if (!response.contains("removed:") && !response.contains("doesn't exist"))
//            throw new IOException("Failed to remove gcm id from the server. " + response);
//    }
//
//    /*public static class GeneralFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle paramBundle) {
//            super.onCreate(paramBundle);
//            addPreferencesFromResource(R.xml.pref_general);
//        }
//    }
//
//    public static class NotificationFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle paramBundle) {
//            super.onCreate(paramBundle);
//            addPreferencesFromResource(R.xml.pref_notification_settings);
//        }
//    }
//
//    public static class FilterFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle paramBundle) {
//            super.onCreate(paramBundle);
//            addPreferencesFromResource(R.xml.pref_filters);
//        }
//    }
//
//    public static class NotificationOptionsFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle paramBundle) {
//            super.onCreate(paramBundle);
//            addPreferencesFromResource(R.xml.pref_notification_options);
//        }
//    }
//
//    public static class AppDataFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle paramBundle) {
//            super.onCreate(paramBundle);
//            addPreferencesFromResource(R.xml.pref_appdata);
//        }
//    }*/
//}
