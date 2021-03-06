package com.checkin.app.checkin.manager.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.checkin.app.checkin.data.BaseViewModel
import com.checkin.app.checkin.data.resource.Resource
import com.checkin.app.checkin.manager.ManagerRepository
import com.checkin.app.checkin.manager.models.ManagerStatsModel
import com.checkin.app.checkin.restaurant.RestaurantRepository
import com.checkin.app.checkin.restaurant.models.RestaurantServiceModel

class ManagerWorkViewModel(application: Application) : BaseViewModel(application) {
    private val managerRepository = ManagerRepository.getInstance(application)
    private val restaurantRepository = RestaurantRepository.getInstance(application)

    private val mRestaurantData = createNetworkLiveData<RestaurantServiceModel>()
    private val mStatsData = createNetworkLiveData<ManagerStatsModel>()

    var shopPk: Long = 0
        private set

    val restaurantStatistics: LiveData<Resource<ManagerStatsModel>> = mStatsData
    val restaurantService: LiveData<Resource<RestaurantServiceModel>> = mRestaurantData
    val activeSessionEvents = MutableLiveData<Int>()
    val qsrEvents = MutableLiveData<Int>()
    val preOrderEvents = MutableLiveData<Int>()

    fun fetchStatistics() {
        mStatsData.addSource(managerRepository.getManagerStats(shopPk), mStatsData::setValue)
    }

    fun fetchRestaurantData(shopPk: Long) {
        this.shopPk = shopPk
        mRestaurantData.addSource(restaurantRepository.getRestaurantServiceData(shopPk), mRestaurantData::setValue)
    }

    override fun updateResults() {
        if (mRestaurantData.value?.data == null)    fetchRestaurantData(shopPk)
    }
}