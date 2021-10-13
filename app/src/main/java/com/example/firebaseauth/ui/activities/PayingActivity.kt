package com.example.firebaseauth.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.firebaseauth.R
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import kotlinx.android.synthetic.main.activity_payment.*
import org.json.JSONObject
import org.w3c.dom.Text

class PayingActivity : AppCompatActivity(), PaymentResultListener {
    var amountTotal = 0.0
    val TAG:String = com.example.firebaseauth.ui.activities.PayingActivity::class.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val intent = getIntent()
        val cost = intent.getStringExtra("cost")

        Checkout.preload(applicationContext)

        amount.text = cost

        val amountGiven = findViewById<TextView>(R.id.amount)
        val buttonPay = findViewById<Button>(R.id.payBtn)



        buttonPay.setOnClickListener {
            var amount = amountGiven.text.toString()
            if(amount != "") {
                amountTotal = amount.toDouble()
                startPayment()
            }
        }
    }
    private fun startPayment() {
        val activity: Activity = this
        val co = Checkout()

        try {
            val options = JSONObject()
            options.put("name","Sports Hub")
            options.put("description","SportsHub Payment")
           // options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("currency","INR")
            options.put("amount",amountTotal * 100)

            val prefill = JSONObject()
            prefill.put("email","sutejym122@gmail.com")
            prefill.put("contact","9739838471")

            options.put("prefill",prefill)
            co.open(activity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message,Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        try{
            startActivity(Intent(this,FailureActivity::class.java))
            finish()
            //Toast.makeText(this,"Payment failed $errorCode \n $response",Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(TAG,"Exception in onPaymentSuccess", e)
        }
    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        try{
            startActivity(Intent(this,SuccessActivity::class.java))
            finish()
           // Toast.makeText(this,"Payment Successful $razorpayPaymentId",Toast.LENGTH_LONG).show()
        }catch (e: Exception){
            Log.e(TAG,"Exception in onPaymentSuccess", e)
        }
    }
}