package com.example.ezmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class ModalBottomSheet : AppCompatActivity() {

    private lateinit var actionAddToCartBtn: Button
    private lateinit var actionBuyNowBtn: Button
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actionproduct_activity)


        actionAddToCartBtn = findViewById(R.id.action_addToCartBtn)
        actionBuyNowBtn = findViewById(R.id.action_BuyBtn)


        actionAddToCartBtn.setOnClickListener {
            showBottomSheet(R.layout.bottomsheet_addtocart)
        }

        actionBuyNowBtn.setOnClickListener {
            showBottomSheet(R.layout.bottomsheet_buy)
        }
    }

    private fun showBottomSheet(layoutResId: Int) {
        val dialogView: View = LayoutInflater.from(this).inflate(layoutResId, null)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }
}
