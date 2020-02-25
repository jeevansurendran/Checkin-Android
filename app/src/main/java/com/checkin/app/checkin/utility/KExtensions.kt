package com.checkin.app.checkin.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.checkin.app.checkin.misc.models.GeolocationModel
import com.checkin.app.checkin.misc.models.LocationModel
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.sign

/*
    Collections
 */
inline fun <T> Collection<T>?.isNotEmpty(): Boolean = this != null && !this.isEmpty()

inline fun <T> Collection<T>?.hasAtleastSize(minSize: Int): Boolean = this != null && this.size >= minSize

/*
    Views
 */
fun TabLayout.setTabBackground(@ColorInt color: Int) = (0 until tabCount).forEach {
    getTabAt(it)?.view?.setBackgroundColor(color)
}

fun Context.toast(msg: String?) = Utils.toast(this, msg)
fun Context.toast(@StringRes msgRes: Int) = Utils.toast(this, msgRes)

fun Activity.snack(msg: String) = Utils.snack(this, msg)
fun View.snack(@StringRes msgRes: Int) = Utils.snack(this, msgRes)
fun Activity.errorSnack(msg: String) = Utils.errorSnack(this, msg)

fun Context.navigateBackToHome() = Utils.navigateBackToHome(this)

/*
    Data Utils
 */
internal fun internalNavigateToLocation(context: Context, latitude: Double, longitude: Double) {
    val gmmIntentUri = Uri.parse("google.navigation:q=${latitude},${longitude}")
    Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        `package` = "com.google.android.apps.maps"
        ContextCompat.startActivity(context, this, null)
    }
}

fun LocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)

fun GeolocationModel.navigateToLocation(context: Context) = internalNavigateToLocation(context, latitude, longitude)

fun String.callPhoneNumber(context: Context) {
    val phoneIntent = Uri.parse("tel:$this")
    ContextCompat.startActivity(context, Intent(Intent.ACTION_DIAL, phoneIntent), null)
}

/*
    Datetime
 */
// Number of days between two Calendar instances
operator fun Calendar.minus(other: Calendar): Int {
    val end = Calendar.getInstance().apply {
        timeInMillis = this@minus.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val start = Calendar.getInstance().apply {
        timeInMillis = other.timeInMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val duration = end.timeInMillis - start.timeInMillis
    return (sign(duration.toDouble()) * TimeUnit.MILLISECONDS.toDays(abs(duration))).toInt()
}

fun Date.toCalendar(): Calendar = Calendar.getInstance().apply { time = this@toCalendar }

/*
    Lifecycle
 */
val LifecycleOwner.coroutineLifecycleScope: LifecycleCoroutineScope
    get() = lifecycleScope

@ExperimentalCoroutinesApi
fun <T> LiveData<T>.asFlow() = channelFlow {
    offer(value)
    val observer = Observer<T> { t -> offer(t) }
    observeForever(observer)
    invokeOnClose {
        removeObserver(observer)
    }
}

/*
    Permissions
 */
val Context.hasLocationPermission: Boolean
    get() = isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.isPermissionGranted(permission: String) = ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

val Context.isLocationEnabled: Boolean
    get() = LocationManagerCompat.isLocationEnabled(getSystemService(Context.LOCATION_SERVICE) as LocationManager)