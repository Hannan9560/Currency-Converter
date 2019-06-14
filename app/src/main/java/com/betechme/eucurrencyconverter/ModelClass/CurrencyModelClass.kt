package com.betechme.currencyconverter.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CurrencyModelClass {

    @SerializedName("details")
    @Expose
    var details: String? = null
    @SerializedName("version")
    @Expose
    var version: Any? = null
    @SerializedName("rates")
    @Expose
    var rates: List<Rate>? = null

    inner class Period {

        @SerializedName("effective_from")
        @Expose
        var effectiveFrom: String? = null
        @SerializedName("rates")
        @Expose
        var rates: Rates? = null

    }

    inner class Rate {

        @SerializedName("name")
        @Expose
        var name: String? = null
        @SerializedName("code")
        @Expose
        var code: String? = null
        @SerializedName("country_code")
        @Expose
        var countryCode: String? = null
        @SerializedName("periods")
        @Expose
        var periods: List<Period>? = null

    }

    inner class Rates {

        @SerializedName("super_reduced")
        @Expose
        var superReduced: Double? = null
        @SerializedName("reduced")
        @Expose
        var reduced: Double? = null
        @SerializedName("standard")
        @Expose
        var standard: Double? = null
        @SerializedName("reduced1")
        @Expose
        var reduced1: Double? = null
        @SerializedName("reduced2")
        @Expose
        var reduced2: Double? = null
        @SerializedName("parking")
        @Expose
        var parking: Double? = null

    }
}
