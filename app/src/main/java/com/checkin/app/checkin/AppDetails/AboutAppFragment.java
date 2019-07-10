package com.checkin.app.checkin.AppDetails;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import butterknife.BindView;
import butterknife.OnClick;

public class AboutAppFragment extends BaseFragment {
    @BindView(R.id.tv_about_us)
    TextView tvAboutUS;
    @BindView(R.id.tv_checkin_link)
    TextView tvCheckin;
    @BindView(R.id.tv_tc_link)
    TextView tvTC;
    @BindView(R.id.tv_pp_link)
    TextView tvPP;
    @BindView(R.id.tv_refund_policy_link)
    TextView tvRP;
    @BindView(R.id.tv_app_version)
    TextView tvVersion;
    ProgressBar progressBar;

    public AboutAppFragment() {
    }

    public static AboutAppFragment newInstance() {
        return new AboutAppFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_about_app;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvAboutUS.setText(Html.fromHtml(getString(R.string.about_us), Html.FROM_HTML_MODE_COMPACT));
            tvCheckin.setText(Html.fromHtml(getString(R.string.app_website_link), Html.FROM_HTML_MODE_COMPACT));
            tvTC.setText(Html.fromHtml(getString(R.string.app_terms_and_condition_link), Html.FROM_HTML_MODE_COMPACT));
            tvPP.setText(Html.fromHtml(getString(R.string.app_privacy_policy_link), Html.FROM_HTML_MODE_COMPACT));
            tvRP.setText(Html.fromHtml(getString(R.string.app_refund_policy_link), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvAboutUS.setText(Html.fromHtml(getString(R.string.about_us)));
            tvCheckin.setText(Html.fromHtml(getString(R.string.app_website_link)));
            tvTC.setText(Html.fromHtml(getString(R.string.app_terms_and_condition_link)));
            tvPP.setText(Html.fromHtml(getString(R.string.app_privacy_policy_link)));
            tvRP.setText(Html.fromHtml(getString(R.string.app_refund_policy_link)));
        }

        try {
            PackageManager manager = requireActivity().getPackageManager();
            PackageInfo info = manager.getPackageInfo(requireActivity().getPackageName(), 0);
            String version = info.versionName;
            tvVersion.setText("App Version:  "+version);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.tv_pp_link, R.id.tv_checkin_link, R.id.tv_refund_policy_link, R.id.tv_tc_link})
    public void onLinkClick(TextView v) {
        switch (v.getId()) {
            case R.id.tv_checkin_link:
                openWebView("https://check-in.in");
                break;
            case R.id.tv_tc_link:
                openWebView("https://check-in.in/terms");
                break;
            case R.id.tv_pp_link:
                openWebView("https://check-in.in/privacy");
                break;
            case R.id.tv_refund_policy_link:
                openWebView("https://check-in.in/refund");
                break;
        }
    }

    @SuppressLint({"SetJavaScriptEnabled"})
    private void openWebView(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        builder.setToolbarColor(getResources().getColor(R.color.primary_red));
        customTabsIntent.launchUrl(requireActivity(), Uri.parse(url));

    }

}
