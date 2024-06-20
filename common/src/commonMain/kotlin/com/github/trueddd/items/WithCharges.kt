package com.github.trueddd.items

interface WithCharges<out T : WheelItem> {

    val maxCharges: Int

    val chargesLeft: Int

    fun useCharge(): WithCharges<T>
}

inline fun <reified T> T.charge(): T? where T : WheelItem, T : WithCharges<T> {
    return useCharge().let { if (it.chargesLeft <= 0) null else it as? T }
}
