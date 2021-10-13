package com.example.firebaseauth.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firebaseauth.R

class SuccessActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success)
        showProgressDialog(resources.getString(R.string.please_wait))
        Toast.makeText(this@SuccessActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()
        startActivity(Intent(this@SuccessActivity, DashboardActivity::class.java))

    }
}
