package com.checkin.app.checkin.Utility;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.checkin.app.checkin.Data.ProblemModel;
import com.checkin.app.checkin.Data.ProblemModel.ERROR_CODE;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.R;

/**
 * Created by shivanshs9 on 27/5/19.
 */

public final class ProblemHandler {
    public static boolean handleDeprecatedAPIUse(Context context, Resource<?> resource) {
        ProblemModel problemModel = resource.getProblem();
        if (problemModel != null) {
            if (problemModel.getErrorCode() == ERROR_CODE.DEPRECATED_VERSION || problemModel.getErrorCode() == ERROR_CODE.INVALID_VERSION) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.app_old_version_dialog_title)
                        .setMessage(R.string.app_old_version_dialog_message)
                        .setPositiveButton("Ok", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                            context.startActivity(new Intent(Intent.ACTION_VIEW, Constants.PLAY_STORE_URI));
                        })
                        .setNegativeButton("Cancel", ((dialogInterface, i) -> dialogInterface.dismiss()))
                        .show();
                return true;
            }
        }
        return false;
    }

    public static boolean handleUnauthenticatedAPIUse(Context context, Resource<?> resource) {
        if (resource.status == Resource.Status.ERROR_UNAUTHORIZED) {
            Utils.logoutFromApp(context);
            Utils.toast(context, R.string.app_force_logged_out);
            return true;
        }
        return false;
    }

    public static boolean handleProblems(Context context, @Nullable Resource<?> resource) {
        if (resource == null)
            return false;
        return handleDeprecatedAPIUse(context, resource) || handleUnauthenticatedAPIUse(context, resource);
    }
}