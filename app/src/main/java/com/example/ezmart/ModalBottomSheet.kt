package com.example.ezmart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class ModalBottomSheet : AppCompatActivity() {

    private lateinit var actionAddToCartBtn: Button
    private lateinit var bottomSheetDialog: BottomSheetDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actionproduct_activity)

        actionAddToCartBtn = findViewById(R.id.action_addToCartBtn)
        actionAddToCartBtn.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val dialogView: View = LayoutInflater.from(this).inflate(R.layout.bottomsheet_addtocart, null)
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetDialog.setContentView(dialogView)
        bottomSheetDialog.show()
    }
}
