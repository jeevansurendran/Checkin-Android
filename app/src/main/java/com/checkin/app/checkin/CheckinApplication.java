package com.checkin.app.checkin;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;
import android.provider.FontsContract;
import android.text.style.TypefaceSpan;

import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.iconics.Iconics;
import com.mikepenz.iconics.typeface.ITypeface;
//import com.mikepenz.iconics.typeface.library.materialdesigniconic.MaterialDesignIconic;
//import com.mikepenz.itemanimators.SlideDownAlphaAnimator;
import com.rd.animation.type.SlideAnimation;

import androidx.multidex.MultiDex;

public class CheckinApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

//        //register our font
//        Iconics.registerFont(ITypeface.DefaultImpls.getRawTypeface(Typeface.BOLD));

    }
}
