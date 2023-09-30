package com.github.trueddd.data.items

interface WithCharges<out T : WheelItem> {

    val maxCharges: Int

    val chargesLeft: Int

    fun useCharge(): WithCharges<T>
}

fun <T: WheelItem.Effect> WithCharges<T>.charge(): WheelItem.Effect? {
    return useCharge().let { if (it.chargesLeft <= 0) null else it } as? WheelItem.Effect
}
