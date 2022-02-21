package com.w36495.senty.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.w36495.senty.databinding.DialogProgressBinding

class ProgressDialog(context: Context) : Dialog(context) {

    private lateinit var binding: DialogProgressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogProgressBinding.inflate(layoutInflater)
        val view = binding.root

        window!!.setBackgroundDrawableResource(android.R.color.transparent)
        setContentView(view)
    }

}