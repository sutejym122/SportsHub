package com.example.firebaseauth.ui.activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.firebaseauth.R
import com.example.firebaseauth.firestore.FirestoreClass
import com.example.firebaseauth.models.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*


@Suppress("DEPRECATION")
class  RegisterActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        tv_login.setOnClickListener {
           /* val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)

            */
            onBackPressed()
        }

        btn_register.setOnClickListener {
            when {
                TextUtils.isEmpty(et_first_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter first name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_last_name.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter last name.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_email.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(et_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(et_confirm_password.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please confirm password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                et_password.text.toString()
                    .trim { it <= 8.toChar() } != et_confirm_password.text.toString()
                    .trim { it <= ' ' } -> {
                    showErrorSnackBar(
                        resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                        true
                    )
                    false
                }

                !cb_terms_and_condition.isChecked -> {
                    showErrorSnackBar(
                        resources.getString(R.string.err_msg_agree_terms_and_condition),
                        true
                    )
                    false
                }
                else -> {
                    showProgressDialog(resources.getString(R.string.please_wait))

                    val email: String = et_email.text.toString().trim { it <= ' ' }
                    val password: String = et_password.text.toString().trim { it <= ' ' }
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> { task ->
                                hideProgressDialog()

                                if (task.isSuccessful) {


                                    val verification = Firebase.auth.currentUser

                                    verification!!.sendEmailVerification()
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                Log.d(TAG, "Email sent,please verify your email to register.")
                                            }
                                        }


                                    val firebaseUser: FirebaseUser = task.result!!.user!!
                                    val user = User(
                                        firebaseUser.uid,
                                        et_first_name.text.toString().trim { it<= ' '},
                                        et_last_name.text.toString().trim { it<= ' '},
                                        et_email.text.toString().trim { it<= ' '}
                                    )
                                    FirestoreClass().registerUser(this@RegisterActivity ,user)

                                   /* Toast.makeText(
                                        this@RegisterActivity,
                                        "You are registered successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    */


                                    val intent =
                                       startActivity( Intent(this@RegisterActivity, LoginActivity::class.java))
                                        finish()
                                    /*intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("user_id", firebaseUser.uid)
                                    intent.putExtra("email_id", email)
                                    startActivity(intent)
                                    finish()

                                     */
                                } else {
                                    hideProgressDialog()
                                    showErrorSnackBar(task.exception!!.message.toString(),true)
                                    // If the registering is not successful then show error message.
                                    /*Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()

                                     */
                                }
                            })
                }
            }
          /*  fun userRegistrationSuccess() {
                hideProgressDialog()
                Toast.makeText(
                    this@RegisterActivity,
                    resources.getString(R.string.register_success),
                    Toast.LENGTH_SHORT
                ).show()
            }

           */
        }
        tv_login.setOnClickListener {

            onBackPressed()
        }


        fun validate(){
            onBackPressed()
        }


    }

    fun userRegistrationSuccess() {
        //hideProgressDialog()
        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()
    }


}

