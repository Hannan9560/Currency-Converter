package com.betechme.eucurrencyconverter

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.betechme.currencyconverter.Inteface.RetrofitInterface
import com.betechme.currencyconverter.ModelClass.CurrencyModelClass
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    private val TAG = "Retrofit"
    private var retrofitInterface: RetrofitInterface? = null
    private var currencyStatus: RadioGroup? = null
    private var inputAmountET: EditText? = null

    private var originalAmoount: TextView? = null
    private var taxAmount: TextView? = null
    private var totalAmount: TextView? = null

    private var mSpinner: Spinner? = null
    private val spinnerArray = ArrayList<String>()
    private val ratesList = ArrayList<CurrencyModelClass.Rate>()

    private var radioButton_1: RadioButton? = null
    private var radioButton_2: RadioButton? = null
    private var radioButton_3: RadioButton? = null
    private var radioButton_4: RadioButton? = null
    private var radioButton_5: RadioButton? = null


    private var currencyModelClass: CurrencyModelClass? = null
    private var selectedRates: CurrencyModelClass.Rates? = null
    private var selectedTaxMethod = -1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //urlString = String.format("0/0");
        currencyStatus = findViewById(R.id.currentStatusRB)
        inputAmountET = findViewById(R.id.inputET)
        radioButton_1 = findViewById(R.id.radioButton_1)
        radioButton_2 = findViewById(R.id.radioButton_2)
        radioButton_3 = findViewById(R.id.radioButton_3)
        radioButton_4 = findViewById(R.id.radioButton_4)
        radioButton_5 = findViewById(R.id.radioButton_5)


        originalAmoount = findViewById(R.id.tv_original_amount)
        taxAmount = findViewById(R.id.tv_tax)
        totalAmount = findViewById(R.id.tv_totalamount)

        mSpinner = findViewById(R.id.spinner)
        mSpinner!!.onItemSelectedListener = this
        /*Radio Group*/
        radioMathod()

        /*JSON PARSING*/

        var retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        // retrofitInterface = retrofit!!.create<RetrofitInterface>(RetrofitInterface::class.java!!)
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
        sendRequestToServer()
    }

    private fun setUpRadioButtonWith(rate: CurrencyModelClass.Rate) {

        radioButton_1!!.visibility = View.GONE
        radioButton_2!!.visibility = View.GONE
        radioButton_3!!.visibility = View.GONE
        radioButton_4!!.visibility = View.GONE
        radioButton_5!!.visibility = View.GONE

        val rates = rate.periods!![0].rates
        selectedRates = rates

        if (rates!!.standard != null && rates.standard!! > 0.0) {
            radioButton_1!!.visibility = View.VISIBLE
            radioButton_1!!.text = "Standard(" + rates.standard + "%)"
            radioButton_1!!.isChecked = true
            selectedTaxMethod = rates.standard!!
        }

        if (rates.parking != null && rates.parking!! > 0.0) {
            radioButton_2!!.visibility = View.VISIBLE
            radioButton_2!!.text = "parking(" + rates.parking + "%)"
        }

        if (rates.reduced1 != null && rates.reduced1!! > 0.0) {
            radioButton_3!!.visibility = View.VISIBLE
            radioButton_3!!.text = "Reduced1(" + rates.reduced1 + "%)"
        }

        if (rates.reduced2 != null && rates.standard!! > 0.0) {
            radioButton_4!!.visibility = View.VISIBLE
            radioButton_4!!.text = "Reduced2(" + rates.reduced2 + "%)"
        }

        if (rates.superReduced != null && rates.standard!! > 0.0) {
            radioButton_5!!.visibility = View.VISIBLE
            radioButton_5!!.text = "Super Reduced(" + rates.superReduced + "%)"
        }
    }

    private fun radioMathod() {
        currencyStatus!!.setOnCheckedChangeListener { radioGroup, id ->
            when (id) {
                -1 -> Log.d(TAG, "Choices cleared!!")
                R.id.radioButton_1 -> selectedTaxMethod = selectedRates!!.standard!!
                R.id.radioButton_2 -> selectedTaxMethod = selectedRates!!.parking!!
                R.id.radioButton_3 -> selectedTaxMethod = selectedRates!!.reduced1!!
                R.id.radioButton_4 -> selectedTaxMethod = selectedRates!!.reduced2!!
                R.id.radioButton_5 -> selectedTaxMethod = selectedRates!!.superReduced!!
            }
            getValueAndCalculate()
        }

    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

        val rate = currencyModelClass!!.rates!![position]
        selectedTaxMethod = -1.0
        setUpRadioButtonWith(rate)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {

    }

    fun calculateAndSetValue(tax: Double) {


        val original_amount = java.lang.Double.parseDouble(inputAmountET!!.text.toString())
        val total_amount = original_amount + tax

        originalAmoount!!.text = original_amount.toString()
        taxAmount!!.text = tax.toString()
        totalAmount!!.text = total_amount.toString()

    }


    override fun onClick(v: View) {
        when (Integer.parseInt(v.tag.toString())) {
            1 -> sendRequestToServer()
        }
    }

    private fun getValueAndCalculate() {
        if (inputAmountET!!.text.toString().length == 0)
            Toast.makeText(this@MainActivity, "Please Input Ammount First!!!!", Toast.LENGTH_SHORT).show()
        else {
            val capital = java.lang.Double.parseDouble(inputAmountET!!.text.toString())
            val totalTax = capital * selectedTaxMethod / 100.0
            calculateAndSetValue(totalTax)
        }
    }

    fun sendRequestToServer() {
        val getCurrencyModelClassCall = retrofitInterface!!.getCurrency("0/0")

        getCurrencyModelClassCall.enqueue(object : Callback<CurrencyModelClass> {
            override fun onResponse(call: Call<CurrencyModelClass>, response: Response<CurrencyModelClass>) {
                Toast.makeText(this@MainActivity, "Connected", Toast.LENGTH_SHORT).show()

                currencyModelClass = response.body()
                val currencyRate = currencyModelClass!!.rates

                val name = ArrayList<String>()
                for (rate in currencyRate!!) {
                    val countryName = rate.name
                    spinnerArray.add(countryName!!)
                }

                val spinnerArrayAdapter = ArrayAdapter(applicationContext,
                        android.R.layout.simple_spinner_item,
                        spinnerArray)

                mSpinner!!.adapter = spinnerArrayAdapter
                mSpinner!!.setSelection(0)
            }

            override fun onFailure(call: Call<CurrencyModelClass>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Please Check Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {

        private val BASE_URL = "https://jsonvat.com/"

        fun isInternetAvailable(context: Context): Boolean {
            val info = (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo as NetworkInfo

            if (info == null) {
                Log.d("Main", "no internet connection")
                return false
            } else {
                if (info.isConnected) {
                    Log.d("Main", " internet connection available...")
                    return true
                } else {
                    Log.d("Main", " internet connection")
                    return true
                }

            }
        }
    }


}