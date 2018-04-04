/*
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


import static com.elephone.setupwizard.SetupWizardApp.LOGV;

import android.annotation.Nullable;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.elephone.setupwizard.util.SetupWizardUtils;

public class SetupWizardExitService extends IntentService {

    private static final String TAG = "SUWExitService";

    public SetupWizardExitService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (LOGV) {
            Log.v(TAG, "onHandleIntent intent=" + intent.toString());
        }
        SetupWizardUtils.finishSetupWizard(this);
    }
}
