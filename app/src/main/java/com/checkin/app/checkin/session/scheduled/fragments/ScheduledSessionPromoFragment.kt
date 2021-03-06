package com.checkin.app.checkin.session.scheduled.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.misc.fragments.BaseFragment
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter
import com.checkin.app.checkin.session.activesession.ActiveSessionPromoAdapter.onPromoCodeItemListener
import com.checkin.app.checkin.session.models.PromoDetailModel
import com.checkin.app.checkin.session.scheduled.viewmodels.NewScheduledSessionViewModel
import com.checkin.app.checkin.utility.Utils


class ScheduledSessionPromoFragment : BaseFragment(), onPromoCodeItemListener {
    override val rootLayout: Int = R.layout.activity_active_session_promo

    @BindView(R.id.rv_available_promos)
    internal lateinit var rvPromos: RecyclerView
    @BindView(R.id.ed_promo_code)
    internal lateinit var edPromoCode: EditText
    @BindView(R.id.tv_session_promo_heading)
    internal lateinit var tvHeading: TextView

    private val mViewModel: NewScheduledSessionViewModel by activityViewModels()
    private lateinit var mAdapter: ActiveSessionPromoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.setOnTouchListener { _, _ -> true }
        rvPromos.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mAdapter = ActiveSessionPromoAdapter(null, this)
        rvPromos.adapter = mAdapter
        setupObservers()
    }

    private fun setupObservers() {
        mViewModel.promoCodes.observe(this, Observer {
            it?.let { listResource ->
                if (listResource.status === Resource.Status.SUCCESS && listResource.data != null) {
                    tvHeading.setText(if (listResource.data.isNotEmpty()) R.string.heading_available_promo else R.string.heading_no_promo)
                    mAdapter.setData(listResource.data)
                }
                else if (listResource.status == Resource.Status.ERROR_FORBIDDEN) parentFragmentManager.popBackStack()
                else if (listResource.status !== Resource.Status.LOADING) Utils.toast(requireContext(), listResource.message)
            }

        })
        mViewModel.observableData.observe(this, Observer {
            it?.let { objectNodeResource ->
                var msg = objectNodeResource.message
                if (objectNodeResource.status === Resource.Status.SUCCESS) {
                    mViewModel.resetObservableData()
                    parentFragmentManager.popBackStack()
                    if (objectNodeResource.data != null && objectNodeResource.data.has("code")) {
                        msg = "Successfully applied " + objectNodeResource.data["code"]
                    }
                } else if (objectNodeResource.status === Resource.Status.ERROR_NOT_FOUND) {
                    msg = "Invalid Promo code."
                }
                Utils.toast(requireContext(), msg)
            }
        })

        mViewModel.fetchPromoCodes(arguments?.getLong(KEY_RESTAURANT_ID) ?: 0L)
    }

    @OnClick(R.id.tv_apply_promo)
    fun onPromoCodeApplyClick() {
        val promoCode = edPromoCode.text.toString()
        if (!TextUtils.isEmpty(promoCode)) {
            mViewModel.availPromoCode(promoCode)
        }
    }

    override fun onPromoApply(promoModel: PromoDetailModel) {
        mViewModel.availPromoCode(promoModel.code)
    }

    companion object {
        private val KEY_RESTAURANT_ID = "scheduled.promos.restaurant_id"
        const val FRAGMENT_TAG = "scheduled.promo"

        fun newInstance(restaurantId: Long) = ScheduledSessionPromoFragment().apply {
            arguments = bundleOf(KEY_RESTAURANT_ID to restaurantId)
        }
    }
}
