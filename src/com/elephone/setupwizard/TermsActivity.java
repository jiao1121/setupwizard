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

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.webkit.WebView;

public class TermsActivity extends BaseSetupWizardActivity {

    private Button mAngree, mUnAngree;
    private TextView mTerms, mSecrets, mCancel, mShowText;
    private WebView mWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAngree = (Button) findViewById(R.id.view_agree);
        mAngree.setOnClickListener(onClickListener);

        mUnAngree = (Button) findViewById(R.id.view_unagree);
        mUnAngree.setOnClickListener(onClickListener);

        mTerms = (TextView) findViewById(R.id.view_terms);
        mTerms.setOnClickListener(onClickListener);

        mSecrets = (TextView) findViewById(R.id.view_secrets);
        mSecrets.setOnClickListener(onClickListener);

    }

    OnClickListener onClickListener = new OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.view_agree) {
                onNavigateNext();
            } else if (v.getId() == R.id.view_unagree) {
                onNavigateBack();
            } else if (v.getId() == R.id.view_terms) {
                showChooseLocaleDialog(R.layout.terms_dialog, 0);
            } else if (v.getId() == R.id.view_secrets) {
                showChooseLocaleDialog(R.layout.secerts_dialog, 1);
            }
        }
    };

    private void showChooseLocaleDialog(int resID, int tags) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TermsActivity.this, R.style.DialogStyles);
        LinearLayout layout = (LinearLayout) LayoutInflater.from(TermsActivity.this)
                .inflate(resID, null);
        mCancel = (TextView) layout.findViewById(R.id.dlalog_cancel);
        mWeb = (WebView) layout.findViewById(R.id.dialog_text);
        if (tags == 0) {
            mWeb.loadUrl("file:///android_asset/index.html");
        } else if (tags == 1) {
            mWeb.loadUrl("file:///android_asset/index1.html");
        }
        mWeb.getSettings().setSupportZoom(true);
        mWeb.getSettings().setBuiltInZoomControls(true);
        mWeb.getSettings().setDisplayZoomControls(false);
        mWeb.setHorizontalScrollBarEnabled(false);
        mWeb.setVerticalScrollBarEnabled(false);
        builder.setView(layout);
        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
        window.getDecorView().setPadding(0, 0, 0, 0);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = TermsActivity.this.getResources().getInteger(R.integer.dialog_local_width);
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

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

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
        return R.layout.setup_terms;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_trems;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.setup_tmers;
    }

}
