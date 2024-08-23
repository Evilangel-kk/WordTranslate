package com.example.translate

interface HttpCallbackListener {
    fun onFinish(response: String)
    fun onError(e: Exception)
}