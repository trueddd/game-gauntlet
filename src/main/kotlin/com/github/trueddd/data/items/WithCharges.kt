package com.github.trueddd.data.items

interface WithCharges {

    val maxCharges: Int

    val chargesLeft: Int

    fun useCharge(): WithCharges
}
