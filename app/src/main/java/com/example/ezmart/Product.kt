package com.example.ezmart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Product(
    val name: String,
    val price: Double,
    val imageResId: Int,
    var isSelected: Boolean = false,
    var quantity: Int = 1
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)  // Ensure price is saved as Double
        parcel.writeInt(imageResId)
        parcel.writeByte(if (isSelected) 1 else 0)
        parcel.writeInt(quantity)
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
            val gson = Gson()
            val listType = object : TypeToken<List<Product>>() {}.type
            return gson.fromJson(json, listType) ?: emptyList() // Keep price as Double
        }
    }

    // Function to get price as a formatted string with Peso sign
    fun getFormattedPrice(): String {
        return "₱ %.2f".format(price) // Formats price with ₱ and two decimal places
    }
}

