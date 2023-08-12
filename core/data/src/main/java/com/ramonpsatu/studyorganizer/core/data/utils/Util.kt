package com.ramonpsatu.studyorganizer.core.data.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast


fun toastMessageShort(context: Context, string: String){
    Toast.makeText(context,string,Toast.LENGTH_SHORT).show()
}

fun toastMessageLong(context: Context, string: String){
    Toast.makeText(context,string,Toast.LENGTH_LONG).show()
}
fun hideVirtualKeyboard(context: Context,view:View) {

    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    if (imm.isAcceptingText) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }
}

