package com.example.firebaseauth.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.firebaseauth.R

class FailureActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_failure)
        showProgressDialog(resources.getString(R.string.please_wait))
        startActivity(Intent(this@FailureActivity, CheckoutActivity::class.java))
    }
}