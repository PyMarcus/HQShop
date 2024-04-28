package com.example.hqshop.dialog

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.hqshop.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog



fun Fragment.setupBottomSheetDialog(onSendClick: (String) -> Unit){
    val dialog = BottomSheetDialog(requireContext())
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val email = view.findViewById<EditText>(R.id.input_email)
    val cancel = view.findViewById<Button>(R.id.cancel)
    val send = view.findViewById<Button>(R.id.ok)

    send.setOnClickListener{
        val emailTxt = email.text.toString().trim()
        onSendClick(emailTxt)
        dialog.dismiss()
    }

    cancel.setOnClickListener{
        dialog.dismiss()
    }
}

