package com.example.capsule.dialogs

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.capsule.R


class ConfirmationDialog : DialogFragment(), DialogInterface.OnClickListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        lateinit var dialog: Dialog
        return dialog
    }

    override fun onClick(p: DialogInterface?, clickedBtn: Int) {
        if (clickedBtn == DialogInterface.BUTTON_POSITIVE) {
        }
        else {
        }
    }
}