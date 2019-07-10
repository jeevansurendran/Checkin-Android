package com.checkin.app.checkin.AppDetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.mikepenz.aboutlibraries.LibTaskCallback;
import com.mikepenz.aboutlibraries.Libs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.LibsConfiguration;
import com.mikepenz.aboutlibraries.entity.Library;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import org.jetbrains.annotations.NotNull;


public class LicenseAppFragment extends BaseFragment {

    public LicenseAppFragment() {
    }

    public static LicenseAppFragment newInstance() {
        return new LicenseAppFragment();
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_license_app;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LibTaskCallback taskCallback = new LibTaskCallback() {
            @Override
            public void onLibTaskStarted() {

            }

            @Override
            public void onLibTaskFinished(@NotNull ItemAdapter<?> itemAdapter) {

            }
        };

        LibsConfiguration.LibsUIListener libsUIListener = new LibsConfiguration.LibsUIListener() {
            @NotNull
            @Override
            public View preOnCreateView(@NotNull View view) {
                return view;
            }

            @NotNull
            @Override
            public View postOnCreateView(@NotNull View view) {
                return view;
            }
        };

        LibsConfiguration.LibsListener libsListener = new LibsConfiguration.LibsListener() {
            @Override
            public void onIconClicked(@NotNull View view) {

            }

            @Override
            public boolean onLibraryAuthorClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }

            @Override
            public boolean onExtraClicked(@NotNull View view, @NotNull Libs.SpecialButton specialButton) {
                return false;
            }

            @Override
            public boolean onIconLongClicked(@NotNull View view) {
                return false;
            }

            @Override
            public boolean onLibraryAuthorLongClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }

            @Override
            public boolean onLibraryContentLongClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }

            @Override
            public boolean onLibraryBottomLongClicked(@NotNull View view, @NotNull Library library) {
                return false;
            }
        };


        new LibsBuilder()
                .withVersionShown(false)
                .withLicenseShown(true)
                .withLibraries()
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .supportFragment();


    }



}
