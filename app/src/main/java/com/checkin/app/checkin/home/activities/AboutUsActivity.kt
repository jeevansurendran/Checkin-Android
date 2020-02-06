package com.checkin.app.checkin.home.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import butterknife.BindView
import butterknife.ButterKnife
import com.checkin.app.checkin.R
import com.checkin.app.checkin.data.config.RemoteConfig
import com.checkin.app.checkin.payment.activities.PaymentActivity
import com.checkin.app.checkin.utility.Constants.PLAY_STORE_URI
import com.checkin.app.checkin.utility.Utils
import com.checkin.app.checkin.utility.toast


class AboutUsActivity : AppCompatActivity() {
    @BindView(R.id.im_aboutus_googleplay)
    lateinit var imGooglePlay: ImageView
    @BindView(R.id.im_aboutus_contact)
    lateinit var imContact: ImageView
    @BindView(R.id.im_aboutus_facebook)
    lateinit var imFacebook: ImageView
    @BindView(R.id.im_aboutus_gmail)
    lateinit var imGmail: ImageView
    @BindView(R.id.im_aboutus_youtube)
    lateinit var imYoutube: ImageView
    @BindView(R.id.im_aboutus_instagram)
    lateinit var imInstagram: ImageView
    @BindView(R.id.tv_aboutus_version)
    lateinit var tvVersion: TextView
    @BindView(R.id.tv_aboutus_tc)
    internal lateinit var tvTC: TextView
    @BindView(R.id.tv_about_refund)
    internal lateinit var tvRefund: TextView

    private val contactDialog by lazy {
        val dialogView = layoutInflater.inflate(R.layout.incl_contact_alert, null)

        AlertDialog.Builder(this)
                .setView(dialogView)
                .setTitle("Contact Us")
                .setOnCancelListener { it.dismiss() }
                .create()
    }
    private val supportPhone by lazy { RemoteConfig[RemoteConfig.Constants.SUPPORT_PHONE_NO].asString() }
    private val supportEmail by lazy { RemoteConfig[RemoteConfig.Constants.SUPPORT_EMAIL_ADDRESS].asString() }
    private val supportPhoneUri by lazy { Uri.parse("tel:${supportPhone}") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        ButterKnife.bind(this)

        supportActionBar?.apply {
            title = "About Us"
            setDisplayHomeAsUpEnabled(true)
        }

        setupUi()
        setupVersion()
    }

    private fun setupVersion() {
        tvVersion.text = "App Version:  ${packageManager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES).versionName}"
    }

    private fun setupUi() {
        tvTC.apply {
            text = Utils.fromHtml(getString(R.string.app_terms_and_condition_link))
            setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_app_terms_conditions).toUri())) }
        }
        tvRefund.apply {
            text = Utils.fromHtml(getString(R.string.app_refund_policy_link))
            setOnClickListener { startActivity(Intent(Intent.ACTION_VIEW, getString(R.string.url_app_refund).toUri())) }
        }
        imGooglePlay.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, PLAY_STORE_URI))
        }

        imContact.setOnClickListener {
            contactDialog.show()

            val mail = contactDialog.findViewById<TextView>(R.id.tv_contact_mail)
            val number = contactDialog.findViewById<TextView>(R.id.tv_contact_number)

            mail?.apply {
                text = supportEmail
                setOnClickListener { emailSupport() }
            }

            number?.apply {
                text = supportPhone
                setOnClickListener {
                    val intent = PaymentActivity.startPaymentIntent(context, 1234.45f)
                    startActivityForResult(intent, PAYMENT_REQUEST_CODE)
                    /*callSupport()*/
                }
            }
        }

        imFacebook.setOnClickListener {
            val facebookIntent = Intent(Intent.ACTION_VIEW)
            facebookIntent.data = Uri.parse(getFacebookPageURL())
            startActivity(facebookIntent)
        }

        imGmail.setOnClickListener { emailSupport() }

        imYoutube.setOnClickListener {
            val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/channel/${RemoteConfig[RemoteConfig.Constants.SUPPORT_YOUTUBE_CHANNEL_ID].asString()}")
            )
            startActivity(intent)
        }

        imInstagram.setOnClickListener {
            val instagramId = RemoteConfig[RemoteConfig.Constants.SUPPORT_INSTAGRAM_PAGE_ID].asString()
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/_u/$instagramId"))

            intent.setPackage("com.instagram.android")

            try {
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://instagram.com/$instagramId")))
            }
        }
    }

    private fun callSupport() {
        val intent = Intent(Intent.ACTION_CALL).apply { data = supportPhoneUri }
        if (ContextCompat.checkSelfPermission(this@AboutUsActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@AboutUsActivity, arrayOf(Manifest.permission.CALL_PHONE), RC_CALL_PERMISSION)
        } else {
            startActivity(intent)
        }
    }

    private fun emailSupport() {
        val intent = Intent(Intent.ACTION_SEND, Uri.fromParts("mailto", supportEmail, null)).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            RC_CALL_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val intent = Intent(Intent.ACTION_CALL).apply { data = supportPhoneUri }
                    startActivity(intent)
                }
            }
        }
    }

    private fun getFacebookPageURL(): String? {
        val facebookPageId = RemoteConfig[RemoteConfig.Constants.SUPPORT_FACEBOOK_PAGE_ID].asString()
        val facebookUrl = "https://www.facebook.com/$facebookPageId"
        return try {
            val versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            if (versionCode >= 3002850) {
                "fb://facewebmodal/f?href=$facebookUrl"
            } else {
                "fb://page/$facebookPageId"
            }
        } catch (e: PackageManager.NameNotFoundException) {
            facebookUrl
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                PaymentActivity.PAYMENT_SUCESSFULL -> toast("SucessFUll")
                PaymentActivity.PAYMENT_CANCELLED -> toast("Cancelled")
                PaymentActivity.PAYMENT_FAILED -> toast("Failed")
            }
        }
    }

    companion object {
        private const val RC_CALL_PERMISSION = 100
        private const val PAYMENT_REQUEST_CODE = 300
    }
}
