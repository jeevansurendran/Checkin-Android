package com.checkin.app.checkin.Notifications;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.ApiClient;
import com.checkin.app.checkin.Data.ApiResponse;
import com.checkin.app.checkin.Data.AppDatabase;
import com.checkin.app.checkin.Data.BaseRepository;
import com.checkin.app.checkin.Data.NetworkBoundResource;
import com.checkin.app.checkin.Data.Resource;
import com.checkin.app.checkin.Data.RetrofitLiveData;
import com.checkin.app.checkin.Data.WebApiService;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.android.ObjectBoxLiveData;
import io.objectbox.query.QueryBuilder;

/**
 * Created by Bhavik Patel on 22/08/2018.
 */

public class NotifRepository extends BaseRepository {

    private final WebApiService mWebService;
    private Box<NotificationModel> mNotifModel;
    private static NotifRepository INSTANCE;

    private NotifRepository(Context context) {
        mWebService = ApiClient.getApiService(context);
        mNotifModel = AppDatabase.getNotifModel(context);
    }

    public static NotifRepository getInstance(Application application) {
        if (INSTANCE == null) {
            synchronized (NotifRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NotifRepository(application.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public LiveData<Resource<List<NotificationModel>>> getNotifModel(int notifId) {
        return new NetworkBoundResource<List<NotificationModel>,List<NotificationModel>>(){

            @Override
            protected boolean shouldUseLocalDb() {
                return true;
            }

            @Override
            protected LiveData<List<NotificationModel>> loadFromDb() {
                return new ObjectBoxLiveData<>(mNotifModel.query().order(NotificationModel_.time, QueryBuilder.DESCENDING).build());
            }

            @Override
            protected boolean shouldFetch(List<NotificationModel> data) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<NotificationModel>>> createCall() {
                return new RetrofitLiveData<>(mWebService.getNotif(notifId));
            }

            @Override
            protected void saveCallResult(List<NotificationModel> data) {

            }
        }.getAsLiveData();

    }
}
