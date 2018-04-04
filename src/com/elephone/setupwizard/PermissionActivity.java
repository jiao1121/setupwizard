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

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class PermissionActivity extends BaseSetupWizardActivity {

    private Button mAngree, mUnAngree;
    private WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View decorView = getWindow().getDecorView();
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

        mAngree = (Button) findViewById(R.id.view_agree);
        mAngree.setOnClickListener(onClickListener);

        mUnAngree = (Button) findViewById(R.id.view_unagree);
        mUnAngree.setOnClickListener(onClickListener);

        mWeb = (WebView) findViewById(R.id.dialog_text);
        mWeb.loadUrl("file:///android_asset/index.html");
        mWeb.setBackgroundColor(0);
        mWeb.getSettings().setSupportZoom(true);
        mWeb.getSettings().setBuiltInZoomControls(true);
        mWeb.getSettings().setDisplayZoomControls(false);
        mWeb.setHorizontalScrollBarEnabled(false);
        mWeb.setVerticalScrollBarEnabled(false);
    }

    OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.view_agree) {
                onNavigateNext();
            } else if (v.getId() == R.id.view_unagree) {
                onNavigateBack();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_NONE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_permission;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_permission;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.setup_permission;
    }
}
