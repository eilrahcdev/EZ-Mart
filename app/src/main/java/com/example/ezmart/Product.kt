package com.example.ezmart

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    var stock: Int,
    val image: String,
    val category: String,
    var isSelected: Boolean = false,
    var quantity: Int = 1
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeInt(stock)
        parcel.writeString(image)
        parcel.writeString(category)
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

        // Serialize a list of products to JSON
        fun serializeList(products: List<Product>): String {
            return Gson().toJson(products)
        }

        // Deserialize a JSON string to a list of products
        fun deserializeList(json: String): List<Product> {
            val gson = Gson()
            val listType = object : TypeToken<List<Product>>() {}.type
            return gson.fromJson(json, listType) ?: emptyList()
        }
    }

    // Function to get price as a formatted string with Peso sign
    fun getFormattedPrice(): String {
        return "₱ %.2f".format(price)
    }

    // To string for easy debugging
    override fun toString(): String {
        return "Product(id=$id, name='$name', price=$price, stock=$stock, image='$image', category='$category', isSelected=$isSelected, quantity=$quantity)"
    }
}
