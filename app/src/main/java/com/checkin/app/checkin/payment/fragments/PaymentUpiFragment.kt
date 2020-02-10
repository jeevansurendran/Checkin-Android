package com.checkin.app.checkin.payment.fragments


import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.misc.fragments.BaseBottomSheetFragment
import com.checkin.app.checkin.payment.model.PaymentUPIModel
import com.razorpay.Razorpay
import com.razorpay.ValidateVpaCallback


class PaymentUpiFragment : BaseBottomSheetFragment() {
    override val rootLayout = R.layout.fragment_payment_upi
    val args: PaymentUpiFragmentArgs by navArgs()

    val razorpay by lazy {
        Razorpay(activity)
    }

    @BindView(R.id.et_payment_upi_vpa)
    internal lateinit var etVpa: EditText

    @BindView(R.id.cb_payment_add_vpa)
    internal lateinit var cbAddVpa: CheckBox

    @BindView(R.id.btn_payment_upi)

    internal lateinit var btnPay: Button

    @OnClick(R.id.btn_payment_upi)
    internal fun ValidateVpa() {
        if (etVpa.text.isEmpty()) {
            etVpa.error = "Please Enter UPI ID"
            return
        }
        val vpa = etVpa.text.toString()

        if (cbAddVpa.isChecked) {
            //TODO add code later to save vpa
        }

        razorpay.isValidVpa(vpa, object : ValidateVpaCallback {
            override fun onResponse(b: Boolean) {
                if (b) {
                    startPayment(vpa)
                } else {
                    etVpa.error = "Invalid UPI ID"
                }
            }

            override fun onFailure() {
                etVpa.error = "Invalid UPI ID"
            }
        })
    }

    fun startPayment(vpa: String) {
        val model = PaymentUPIModel(
                "jvns67@gmail.com",
                "+918073298546",
                vpa)
        val action = PaymentUpiFragmentDirections
                .actionPaymentUpiFragmentToPaymentSubmitFragment(model, args.amount, PaymentSubmitFragment.PAYMENT_TYPE.UPI)
        findNavController().navigate(action)
    }

}