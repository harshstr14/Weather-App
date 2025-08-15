package com.example.weatherly

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener

class CustomSearchView(context: Context, attributeSet: AttributeSet ?= null):
    LinearLayout(context,attributeSet) {

        interface OnQueryTextListener {
            fun onQueryTextSubmit(query: String): Boolean
            fun onQueryTextChange(newText: String): Boolean
        }

    private var listener: OnQueryTextListener ?= null
    private val editText: EditText
    private val clearIcon: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.custom_search_view,this,true)
        editText = findViewById(R.id.editText)
        clearIcon = findViewById(R.id.clearText)

        editText.addTextChangedListener {
            listener?.onQueryTextChange(it.toString())
            clearIcon.visibility = if (it.isNullOrEmpty()) GONE else VISIBLE
        }

        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                listener?.onQueryTextSubmit(editText.text.toString().trim())
                true
            } else false
        }

        clearIcon.setOnClickListener {
            editText.text.clear()
        }
    }
    fun setOnQueryTextListener(l: OnQueryTextListener) {
        listener = l
    }
}