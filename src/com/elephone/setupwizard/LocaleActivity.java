/*
 * Copyright (C) 2016 The CyanogenMod Project
 * Copyright (C) 2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elephone.setupwizard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.Settings;

import com.android.internal.telephony.MccTable;
import com.android.internal.telephony.TelephonyIntents;
import com.android.setupwizardlib.util.WizardManagerHelper;

import com.elephone.setupwizard.R;
import com.elephone.setupwizard.widget.LocalePicker;

import java.util.List;
import java.util.Locale;

public class LocaleActivity extends BaseSetupWizardActivity {

    public static final String TAG = LocaleActivity.class.getSimpleName();

    private ArrayAdapter<com.android.internal.app.LocalePicker.LocaleInfo> mLocaleAdapter;
    private Locale mCurrentLocale;
    private int[] mAdapterIndices;
    //private LocalePicker mLanguagePicker;
    private FetchUpdateSimLocaleTask mFetchUpdateSimLocaleTask;
    private final Handler mHandler = new Handler();
    private boolean mPendingLocaleUpdate;
    private boolean mPaused = true;
    private ImageView mNext;
    private TextView mCall, mEyes, mLocal, mCancel;
    private ListView mLocalList;

    private final Runnable mUpdateLocale = new Runnable() {
        public void run() {
            if (mCurrentLocale != null) {
                //mLanguagePicker.setEnabled(false);
                com.android.internal.app.LocalePicker.updateLocale(mCurrentLocale);
            }
        }
    };

    private final BroadcastReceiver mSimChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TelephonyIntents.ACTION_SIM_STATE_CHANGED)) {
                fetchAndUpdateSimLocale();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mLanguagePicker = (LocalePicker) findViewById(R.id.locale_list);
        loadLanguages();

    }

    OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.view_next) {
                onNavigateNext();
            } else if (v.getId() == R.id.view_call) {
                startEmergencyDialer();
            } else if (v.getId() == R.id.view_local) {
                showChooseLocaleDialog();
            } else {
                startSettings();
            }
        }
    };

    private void showChooseLocaleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(LocaleActivity.this, R.style.DialogStyles);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(LocaleActivity.this)
                .inflate(R.layout.local_dialog, null);
        mLocalList = (ListView) layout.findViewById(R.id.local_list);
        mLocalList.setAdapter(mLocaleAdapter);
        mCancel = (TextView) layout.findViewById(R.id.local_cancel);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.getDecorView().setPadding(0, 0, 0, 0);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = LocaleActivity.this.getResources().getInteger(R.integer.dialog_local_width);
        window.setAttributes(params);
        final View decorView = dialog.getWindow().getDecorView();
        final int uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

        decorView.setSystemUiVisibility(uiOption);

        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void  onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(uiOption);
                }
            }
        });
        dialog.show();
        mLocalList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                TextView locale = (TextView) arg1.findViewById(R.id.locale);
                locale.setEnabled(true);
                ((SetupWizardApp)getApplication()).setIgnoreSimLocale(true);
                final com.android.internal.app.LocalePicker.LocaleInfo localLocaleInfo = mLocaleAdapter.getItem(arg2);
                onLocaleChanged(localLocaleInfo.getLocale());
                mLocal.setText(localLocaleInfo.getLabel());
                dialog.dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void init() {
        mNext = (ImageView) findViewById(R.id.view_next);
        mNext.setOnClickListener(onClickListener);

        mCall = (TextView) findViewById(R.id.view_call);
        mCall.setOnClickListener(onClickListener);

        mEyes = (TextView) findViewById(R.id.view_eyes);
        mEyes.setOnClickListener(onClickListener);

        mLocal = (TextView) findViewById(R.id.view_local);
        mLocal.setOnClickListener(onClickListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPaused = true;
        unregisterReceiver(mSimChangedReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPaused = false;
        registerReceiver(mSimChangedReceiver,
                new IntentFilter(TelephonyIntents.ACTION_SIM_STATE_CHANGED));
        //if (mLanguagePicker != null) {
        //    mLanguagePicker.setEnabled(true);
        //}
        if (mPendingLocaleUpdate) {
            mPendingLocaleUpdate = false;
            fetchAndUpdateSimLocale();
        }
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_NONE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_locale;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_locale;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_locale;
    }

    public static ArrayAdapter<com.android.internal.app.LocalePicker.LocaleInfo> constructAdapter(Context context,
            final int layoutId, final int fieldId) {
        boolean isInDeveloperMode = Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
        final List<com.android.internal.app.LocalePicker.LocaleInfo> localeInfos =
                com.android.internal.app.LocalePicker.getAllAssetLocales(context, isInDeveloperMode);

        final LayoutInflater inflater =
            (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new ArrayAdapter<com.android.internal.app.LocalePicker.LocaleInfo>(context, layoutId, fieldId, localeInfos) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view;
                TextView text;
                convertView = inflater.inflate(layoutId, parent, false);
                text = (TextView) convertView.findViewById(fieldId);
                com.android.internal.app.LocalePicker.LocaleInfo item = getItem(position);
                final Locale currLocale = Locale.getDefault();
                if (item.getLocale().equals(currLocale)) {
                    text.setCompoundDrawablesWithIntrinsicBounds(context.getResources()
                            .getDrawable(R.drawable.ic_check), null, null, null);
                }
                text.setText(item.toString());
                text.setTextLocale(item.getLocale());

                return convertView;
            }
        };
    }

    private void loadLanguages() {
        mLocaleAdapter = constructAdapter(this, R.layout.locale_dialog_item, R.id.locale);
        init();
        mCurrentLocale = Locale.getDefault();
        fetchAndUpdateSimLocale();
        mAdapterIndices = new int[mLocaleAdapter.getCount()];
        int currentLocaleIndex = 0;
        String name = this.getResources().getString(R.string.setup_locale);
        String [] labels = new String[mLocaleAdapter.getCount()];
        for (int i = 0; i < mAdapterIndices.length; i++) {
            com.android.internal.app.LocalePicker.LocaleInfo localLocaleInfo =
                    mLocaleAdapter.getItem(i);
            Locale localLocale = localLocaleInfo.getLocale();
            if (localLocale.equals(mCurrentLocale)) {
                currentLocaleIndex = i;
                name = mLocaleAdapter.getItem(i).toString();
            }
            mAdapterIndices[i] = i;
            labels[i] = localLocaleInfo.getLabel();
        }
        mLocal.setText(name);
        //mLanguagePicker.setDisplayedValues(labels);
        //mLanguagePicker.setMaxValue(labels.length - 1);
        //mLanguagePicker.setValue(currentLocaleIndex);
        //mLanguagePicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //mLanguagePicker.setOnValueChangedListener(new LocalePicker.OnValueChangeListener() {
        //    public void onValueChange(LocalePicker picker, int oldVal, int newVal) {
        //        setLocaleFromPicker();
        //    }
        //});
        //mLanguagePicker.setOnScrollListener(new LocalePicker.OnScrollListener() {
        //    @Override
        //    public void onScrollStateChange(LocalePicker view, int scrollState) {
        //        if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
        //            ((SetupWizardApp)getApplication()).setIgnoreSimLocale(true);
        //        }
        //    }
        //});
    }

    private void startSettings() {
            Intent eyesIntent = new Intent();
            eyesIntent.setComponent(new ComponentName("com.android.settings",
                        "com.android.settings.accessibility.AccessibilitySettingsForSetupWizardActivity"));
            startActivity(eyesIntent);
    }

    //private void setLocaleFromPicker() {
    //    ((SetupWizardApp)getApplication()).setIgnoreSimLocale(true);
    //    int i = mAdapterIndices[mLanguagePicker.getValue()];
    //    final com.android.internal.app.LocalePicker.LocaleInfo localLocaleInfo = mLocaleAdapter.getItem(i);
    //    onLocaleChanged(localLocaleInfo.getLocale());
    //    mLocal.setText(localLocaleInfo.getLabel());
    //}

    private void onLocaleChanged(Locale paramLocale) {
        //mLanguagePicker.setEnabled(true);
        Resources localResources = getResources();
        Configuration localConfiguration1 = localResources.getConfiguration();
        Configuration localConfiguration2 = new Configuration();
        localConfiguration2.locale = paramLocale;
        localResources.updateConfiguration(localConfiguration2, null);
        localResources.updateConfiguration(localConfiguration1, null);
        mHandler.removeCallbacks(mUpdateLocale);
        mCurrentLocale = paramLocale;
        mHandler.postDelayed(mUpdateLocale, 1000);
    }

    private void fetchAndUpdateSimLocale() {
        if (((SetupWizardApp)getApplication()).ignoreSimLocale() || isDestroyed()) {
            return;
        }
        if (mPaused) {
            mPendingLocaleUpdate = true;
            return;
        }
        if (mFetchUpdateSimLocaleTask != null) {
            mFetchUpdateSimLocaleTask.cancel(true);
        }
        mFetchUpdateSimLocaleTask = new FetchUpdateSimLocaleTask();
        mFetchUpdateSimLocaleTask.execute();
    }

    private class FetchUpdateSimLocaleTask extends AsyncTask<Void, Void, Locale> {
        @Override
        protected Locale doInBackground(Void... params) {
            Locale locale = null;
            Activity activity = LocaleActivity.this;
            if (!activity.isFinishing() || !activity.isDestroyed()) {
                // If the sim is currently pin locked, return
                TelephonyManager telephonyManager = (TelephonyManager)
                        activity.getSystemService(Context.TELEPHONY_SERVICE);
                int state = telephonyManager.getSimState();
                if(state == TelephonyManager.SIM_STATE_PIN_REQUIRED ||
                        state == TelephonyManager.SIM_STATE_PUK_REQUIRED) {
                    return null;
                }

                final SubscriptionManager subscriptionManager =
                        SubscriptionManager.from(activity);
                List<SubscriptionInfo> activeSubs =
                        subscriptionManager.getActiveSubscriptionInfoList();
                if (activeSubs == null || activeSubs.isEmpty()) {
                    return null;
                }

                // Fetch locale for active sim's MCC
                int mcc = activeSubs.get(0).getMcc();
                locale = MccTable.getLocaleFromMcc(activity, mcc, null);

                // If that fails, fall back to preferred languages reported
                // by the sim
                if (locale == null) {
                    String localeString = telephonyManager.getLocaleFromDefaultSim();
                    if (localeString != null) {
                        locale = Locale.forLanguageTag(localeString);

                    }
                }
            }
            return locale;
        }

        @Override
        protected void onPostExecute(Locale simLocale) {
            if (simLocale != null && !simLocale.equals(mCurrentLocale)) {
                if (!((SetupWizardApp)getApplication()).ignoreSimLocale() && !isDestroyed()) {
                    String label = getString(R.string.sim_locale_changed,
                            simLocale.getDisplayName());
                    Toast.makeText(LocaleActivity.this, label, Toast.LENGTH_SHORT).show();
                    mLocal.setText(simLocale.getDisplayName());
                    onLocaleChanged(simLocale);
                    ((SetupWizardApp)getApplication()).setIgnoreSimLocale(true);
                }
            }
        }
    }
}
