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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.AlphaAnimation;
import android.view.Window;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;

import com.elephone.setupwizard.util.EnableAccessibilityController;

public class WelcomeActivity extends BaseSetupWizardActivity {

    public static final String TAG = WelcomeActivity.class.getSimpleName();

    private View mRootView;
    private ImageView mLogoView;
    private EnableAccessibilityController mEnableAccessibilityController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRootView = findViewById(R.id.root);
        setBackText(R.string.emergency_call);
        setBackDrawable(null);
        setButtonGone();
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
        initImage();
        mEnableAccessibilityController =
                EnableAccessibilityController.getInstance(getApplicationContext());
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mEnableAccessibilityController.onTouchEvent(event);
            }
        });
    }

    private void initImage() {
        mLogoView = (ImageView) findViewById(R.id.brand_logo);
        AlphaAnimation alphaAnimation = (AlphaAnimation) AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.alpha);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                onNavigateNext();
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        mLogoView.startAnimation(alphaAnimation);
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onNavigateBack() {}

    @Override
    protected int getTransition() {
        return TRANSITION_ID_LOCAL;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.welcome_activity;
    }
}
