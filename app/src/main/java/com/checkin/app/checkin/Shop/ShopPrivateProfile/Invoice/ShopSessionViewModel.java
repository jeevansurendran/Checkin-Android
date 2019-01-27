package com.checkin.app.checkin.Shop.ShopPrivateProfile.Invoice;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;

import com.checkin.app.checkin.Data.BaseViewModel;
import com.checkin.app.checkin.Data.Resource;

import java.util.List;

public class ShopSessionViewModel extends BaseViewModel {
    private ShopInvoiceRepository mRepository;

    private MediatorLiveData<Resource<ShopSessionDetailModel>> mDetailData = new MediatorLiveData<>();
    private MediatorLiveData<Resource<List<ShopSessionFeedbackModel>>> mFeedbackData = new MediatorLiveData<>();

    private long mSessionPk;

    public ShopSessionViewModel(@NonNull Application application) {
        super(application);
        mRepository = ShopInvoiceRepository.getInstance(application);
    }

    public void fetchSessionFeedbacks() {
        mFeedbackData.addSource(mRepository.getShopSessionFeedbacks(mSessionPk), mFeedbackData::setValue);
    }

    public LiveData<Resource<List<ShopSessionFeedbackModel>>> getSessionFeedbacks() {
        return mFeedbackData;
    }

    public void fetchSessionDetail() {
        mDetailData.addSource(mRepository.getShopSessionDetail(mSessionPk), mDetailData::setValue);
    }

    public LiveData<Resource<ShopSessionDetailModel>> getSessionDetail() {
        return mDetailData;
    }

    @Override
    public void updateResults() {
    }

    public void setSessionPk(long sessionPk) {
        this.mSessionPk = sessionPk;
    }
}
