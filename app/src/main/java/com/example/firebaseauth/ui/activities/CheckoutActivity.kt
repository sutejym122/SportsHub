package com.example.firebaseauth.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.firebaseauth.R
import com.example.firebaseauth.firestore.FirestoreClass
import com.example.firebaseauth.models.Address
import com.example.firebaseauth.models.CartItem
import com.example.firebaseauth.models.Order
import com.example.firebaseauth.models.Product
import com.example.firebaseauth.ui.adapters.CartItemsListAdapter
import com.example.firebaseauth.utils.Constants
import kotlinx.android.synthetic.main.activity_cart_list.*
import kotlinx.android.synthetic.main.activity_checkout.*
import kotlinx.android.synthetic.main.activity_settings.*


open class
CheckoutActivity : BaseActivity() {


    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order
    //var id = "id"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }


    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }

    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }
    private fun placeAnOrder() {

        showProgressDialog(resources.getString(R.string.please_wait))
        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "100.0",
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )
        FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
    }


    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()



        val cost = tv_checkout_total_amount.text.toString()

        val intent = Intent(this@CheckoutActivity, PayingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("cost", cost)
        startActivity(intent)
        finish()
    }

    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this,mCartItemsList, mOrderDetails)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {


        hideProgressDialog()


        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }


        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)


        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for(item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()
            if (availableQuantity >0 ){
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }


        /*var subTotal: Double = 0.0

         */
        tv_checkout_sub_total.text = "$mSubTotal"
        var shippingCharge = 0
        if(mSubTotal > 1000.00){
            tv_checkout_shipping_charge.text = "Free delivery"
        } else {
            shippingCharge = 100
            tv_checkout_shipping_charge.text = "100.00"
        }


        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + shippingCharge
            tv_checkout_total_amount.text = "${mTotalAmount}"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }
}