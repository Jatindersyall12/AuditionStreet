package com.megamind.razorpay

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject


class RazorPayActivity : AppCompatActivity(), PaymentResultListener {
    private var name = ""
    private var email = ""
    private var phNo = ""
    private var amount = ""
    private var currency = ""

    private var checkout: Checkout? = null
    private var razorpayKey: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent.getStringExtra(resources.getString(R.string.name)) != null) {
            name = intent.getStringExtra(resources.getString(R.string.name))!!
        }else{
            callBackToApp("Please provide name", Activity.RESULT_CANCELED)
            return
        }
        if (intent.getStringExtra(resources.getString(R.string.email)) != null) {
            email = intent.getStringExtra(resources.getString(R.string.email))!!
        }else{
            callBackToApp("Please provide email", Activity.RESULT_CANCELED)
            return
        }
        if (intent.getStringExtra(resources.getString(R.string.phone)) != null) {
            phNo = intent.getStringExtra(resources.getString(R.string.phone))!!
        }else{
            callBackToApp("Please provide phone number", Activity.RESULT_CANCELED)
            return
        }
        if (intent.getStringExtra(resources.getString(R.string.amount)) != null) {
            amount = intent.getStringExtra(resources.getString(R.string.amount))!!
        }else{
            callBackToApp("Please provide amount", Activity.RESULT_CANCELED)
            return
        }
        if (intent.getStringExtra(resources.getString(R.string.currency)) != null) {
            currency = intent.getStringExtra(resources.getString(R.string.currency))!!
        }else{
            callBackToApp("Please provide currency type", Activity.RESULT_CANCELED)
            return
        }

        val convertedAmount = (amount.toInt() * 100).toString()
        rezorpayCall(
            name,
            email,
            phNo,
            convertedAmount
        )

    }

    private fun rezorpayCall(name: String?, email: String?, phNo: String?, convertedAmount: String?) {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        razorpayKey =
            resources.getString(R.string.razor_pay_key) //Generate your razorpay key from Settings-> API Keys-> copy Key Id
        checkout = Checkout()
        checkout!!.setKeyID(razorpayKey)
        try {
            val options = JSONObject()
            options.put("name", name)
            options.put("description", "Auditionstreet Solutions Llp")
            options.put("currency", currency)
            options.put("amount", convertedAmount)
            val preFill = JSONObject()
            preFill.put("email", email)
            preFill.put("contact", phNo)
            options.put("prefill", preFill)
            checkout!!.open(this@RazorPayActivity, options)
        } catch (e: Exception) {
            Toast.makeText(this@RazorPayActivity, "Error in payment: " + e.message, Toast.LENGTH_LONG)
                .show()
            callBackToApp("Error in payment: " + e.message, Activity.RESULT_CANCELED)
            return
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Toast.makeText(this, "Transaction Successful: " + p0, Toast.LENGTH_SHORT).show()
        callBackToApp(p0!!, Activity.RESULT_OK)
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Toast.makeText(this, "Transaction unsuccessful: "+ p1 , Toast.LENGTH_SHORT).show()
        callBackToApp(p1!!, Activity.RESULT_CANCELED)
    }

    private fun callBackToApp(message: String, statusCode: Int){
        val intent = Intent()
        if (statusCode == Activity.RESULT_OK){
            intent.putExtra(resources.getString(R.string.transaction_id), message)
        }else if(statusCode == Activity.RESULT_CANCELED) {
            intent.putExtra(resources.getString(R.string.error_message), message)
        }
        setResult(statusCode, intent)
        finish()
    }
}