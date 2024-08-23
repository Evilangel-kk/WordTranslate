package com.example.translate

import org.json.JSONArray

object Letter {
    var word : String = ""
    var meanings=mutableListOf<String>()

    fun add(meaning:String){
        this.meanings.add(meaning)
    }

    fun changeWord(word : String){
        if(!this.word.equals(word)){
            this.word=word
            this.meanings.clear()
        }
    }
}
