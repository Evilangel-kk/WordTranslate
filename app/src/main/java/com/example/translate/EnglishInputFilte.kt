package com.example.translate

import android.text.InputFilter
import android.text.Spanned
import android.widget.EditText

class EnglishInputFilter : InputFilter {
    override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dstart: Int, dend: Int): CharSequence? {
        // 正则表达式: 只允许英文字符和空格
        val regex = "^[a-zA-Z\\s]*$"
        if (!source.toString().matches(regex.toRegex())) {
            return "" // 过滤掉非英文字符
        }
        return null // 允许输入
    }
}

// 在 Activity 或 Fragment 中设置 EditText
fun setupEditText(editText: EditText) {
    editText.filters = arrayOf(EnglishInputFilter())
}