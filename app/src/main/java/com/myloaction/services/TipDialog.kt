package com.myloaction.services

import android.content.Context
import androidx.appcompat.app.AlertDialog

class TipDialog(context: Context) : AlertDialog(context) {
    interface OnDialogBtnClickListener {
        fun onLeftBtnClicked(paramTipDialog: TipDialog?)
        fun onRightBtnClicked(paramTipDialog: TipDialog?)
    }
}