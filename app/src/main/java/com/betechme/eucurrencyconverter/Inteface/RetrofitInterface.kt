package com.betechme.currencyconverter.Inteface

import com.betechme.currencyconverter.ModelClass.CurrencyModelClass

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RetrofitInterface {
    @GET
    fun getCurrency(@Url stringUrl: String): Call<CurrencyModelClass>
}