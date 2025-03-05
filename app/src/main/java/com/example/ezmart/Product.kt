package com.example.ezmart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Product(
    val name: String,
    val price: String,
    val imageResId: Int,
    var isSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(price)
        parcel.writeInt(imageResId)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }

        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }

        fun serializeList(products: List<Product>): String {
            return Gson().toJson(products)
        }

        fun deserializeList(json: String): List<Product> {
            val type = object : TypeToken<List<Product>>() {}.type
            return Gson().fromJson(json, type)
        }
    }
}
