package com.checkin.app.checkin.payment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.payment.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@UseExperimental(ExperimentalCoroutinesApi::class)
class PaymentViewModel(application: Application) : BaseViewModel(application) {
    private val repository = PaymentRepository.getInstance(application)

    lateinit var transactionData: NewPaytmTransactionModel

    private val payRequestChannel = ConflatedBroadcastChannel<PaymentRequest>()
    private val mUPIPushOptions = createNetworkLiveData<List<UPIPushPaymentOptionModel>>()
    private val mUPICollectOptions = createNetworkLiveData<List<UPICollectPaymentOptionModel>>()
    private val mNetBankingOptions = createNetworkLiveData<List<NetBankingPaymentOptionModel>>()

    val upiOptions: LiveData<Resource<List<UPIPaymentOptionModel>>> = MediatorLiveData<Resource<List<UPIPaymentOptionModel>>>().apply {
        addSource(mUPIPushOptions) {
            it?.let { listResource ->
                if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                    value = value?.data?.filterIsInstance<UPICollectPaymentOptionModel>()?.run { Resource.success(listResource.data + this) }
                            ?: listResource
            }
        }
        addSource(mUPICollectOptions) {
            it?.let { listResource ->
                if (listResource.status == Resource.Status.SUCCESS && listResource.data != null)
                    value = value?.data?.filterIsInstance<UPIPushPaymentOptionModel>()?.run { Resource.success(this + listResource.data) }
                            ?: listResource
            }
        }
    }
    val netBankingOptions: LiveData<Resource<List<NetBankingPaymentOptionModel>>> = mNetBankingOptions

    fun init(transactionModel: NewPaytmTransactionModel) {
        transactionData = transactionModel
    }

    fun fetchUPIOptions() {
        mUPIPushOptions.addSource(repository.UPIAppOptions, mUPIPushOptions::setValue)
        mUPICollectOptions.addSource(repository.UPIIdOptions, mUPICollectOptions::setValue)
    }

    fun fetchNetBankingOptions() {
        mNetBankingOptions.addSource(repository.netBankingOptions, mNetBankingOptions::setValue)
    }

    fun payUsing(paymentOptionModel: PaymentOptionModel, saveOption: Boolean = true) {
        payRequestChannel.offer(PaymentRequest(paymentOptionModel, saveOption))
    }

    fun onRequestPay(action: (PaymentOptionModel) -> Unit) = viewModelScope.launch {
        payRequestChannel.openSubscription().consumeEach { action(it.optionModel) }
    }

    override fun updateResults() {

    }
}

class PaymentRequest(val optionModel: PaymentOptionModel, val saveOption: Boolean)