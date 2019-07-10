package com.checkin.app.checkin.session.activesession;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.checkin.app.checkin.Misc.BaseFragment;
import com.checkin.app.checkin.R;
import com.checkin.app.checkin.Shop.ShopModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;
import butterknife.OnClick;

public class ActiveSessionPaymentOptionsFragment extends BaseFragment {

    @BindView(R.id.tv_as_payment_options_amount)
    TextView tvAmount;
    private OnSelectPaymentMode mListener;

    public static final String KEY_SESSION_AMOUNT = "session.total_amount";
    public static final String KEY_PAYMENT_MODE_RESULT = "payment_mode.result";

    public ActiveSessionPaymentOptionsFragment() {
    }

    public static ActiveSessionPaymentOptionsFragment newInstance(OnSelectPaymentMode listener) {
        ActiveSessionPaymentOptionsFragment fragment = new ActiveSessionPaymentOptionsFragment();
        fragment.mListener = listener;
        return fragment;
    }

    @Override
    protected int getRootLayout() {
        return R.layout.fragment_active_session_payment_options;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up));
        tvAmount.setText(getArguments().getString(KEY_SESSION_AMOUNT));
    }

    @OnClick(R.id.cash_container)
    public void onCashClick(){
        mListener.paymentSelect(ShopModel.PAYMENT_MODE.CASH);
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }

    @OnClick(R.id.paytm_container)
    public void onPaytmClick(){
        mListener.paymentSelect(ShopModel.PAYMENT_MODE.PAYTM);
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }


    public interface OnSelectPaymentMode {
        void paymentSelect(ShopModel.PAYMENT_MODE s);
    }

    @OnClick(R.id.gpay_container)
    public void onGPayClick(){
//        Intent data = new Intent();
//        data.putExtra(KEY_PAYMENT_MODE_RESULT, SessionBillModel.PAYMENT_MODES.GOOGLE_PAY);
//        setResult(RESULT_OK, data);
//        finish();
    }

    @OnClick(R.id.im_session_payment_options_back)
    public void backClick(){
        if (getFragmentManager() != null) {
            getFragmentManager().popBackStack();
        }
    }


}
