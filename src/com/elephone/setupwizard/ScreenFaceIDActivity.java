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

import static com.elephone.setupwizard.SetupWizardApp.ACTION_SETUP_LOCKSCREEN;
import static com.elephone.setupwizard.SetupWizardApp.EXTRA_DETAILS;
import static com.elephone.setupwizard.SetupWizardApp.EXTRA_TITLE;
import static com.elephone.setupwizard.SetupWizardApp.INTENT_EXTRA1;
import static com.elephone.setupwizard.SetupWizardApp.INTENT_EXTRA2;
import static com.elephone.setupwizard.SetupWizardApp.INTENT_EXTRA3;
import static com.elephone.setupwizard.SetupWizardApp.INPUT_OR_CONFORM_PWD_ID;
import static com.elephone.setupwizard.SetupWizardApp.REQUEST_CODE_SETUP_LOCKSCREEN;

import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.ComponentName;
import android.view.View;

public class ScreenFaceIDActivity extends SubBaseActivity {

    public static final String TAG = ScreenFaceIDActivity.class.getSimpleName();


    @Override
    protected void onStartSubactivity() {
        setNextAllowed(true);
        findViewById(R.id.set_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchLockscreenSetup();
            }
        });

        findViewById(R.id.skip_lock).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateNext();
            }
        });

        findViewById(R.id.view_unagree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateBack();
            }
        });

        findViewById(R.id.view_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNavigateNext();
            }
        });
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_SLIDE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_face_screen;
    }

    @Override
    protected int getTitleResId() {
        return R.string.face_setup_title;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_face_id;
    }

    private void launchLockscreenSetup() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.settings",
                    "com.android.settings.ChooseLockGeneric"));
        intent.putExtra(EXTRA_TITLE,
                getString(R.string.settings_lockscreen_setup_title));
        intent.putExtra(EXTRA_DETAILS,
                getString(R.string.settings_lockscreen_setup_details));
        intent.putExtra(SetupWizardApp.EXTRA_ALLOW_SKIP, true);

        intent.putExtra(INTENT_EXTRA1, true);
        intent.putExtra(INTENT_EXTRA2, DevicePolicyManager.PASSWORD_QUALITY_SOMETHING);
        intent.putExtra(INTENT_EXTRA3, true);
        startSubactivity(intent, INPUT_OR_CONFORM_PWD_ID);
    }

    @Override
    protected int getSubactivityNextTransition() {
        return TRANSITION_ID_SLIDE;
    }

}
