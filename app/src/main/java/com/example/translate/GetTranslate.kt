package com.example.translate

import android.util.Log
import com.example.translate.Letter.meanings
import org.json.JSONArray
import org.json.JSONObject

object GetTranslate {
    fun Search(word: String, dataBaseHelper: MyDataBaseHelper, callback: (result:Int) -> Unit){
        if(SearchDictionary(word, dataBaseHelper)>=0){
            // 词典中含有该单词
            dataBaseHelper.getDefinitionsForWord(word){
                callback(1)
            }
            Log.d("Letter.meanings", Letter.meanings.toString())

        }else{
            // 词典中没有，尝试网络查询
            SearchNet(word, dataBaseHelper) { result ->
                if(result==1){
                    Log.d("meaningsList",""+Letter.meanings)
                    callback(1)
                }else{
                    callback(0)
                }
            }
        }
    }
    fun SearchDictionary(word: String, dataBaseHelper: MyDataBaseHelper):Int {
        // 查询词典
        val Id=dataBaseHelper.getWordId(word)
        Log.d("WordId",""+Id)

        if(Id!=null){
            // 词典中已有该词
            Log.d("SearchDictionary","exist")
            return Id
        }
        Log.d("SearchDictionary","not exist")
        return -1
    }

    fun SearchNet(word: String, dataBaseHelper: MyDataBaseHelper, callback: (result:Int) -> Unit) {
        // 网络api查询
        val address = "${DictionaryAPI.url}&app_key=${DictionaryAPI.app_key}&sign=${DictionaryAPI.sign}&keyWord=${word}"
        Log.d("address",address)
        HttpUtil.sendHttpRequest(address, object : HttpCallbackListener {
            override fun onFinish(response: String) {
                val begin = response.indexOf("[")
                val end = response.lastIndexOf("]")
                val data = response.substring(begin..end)
                val jsonArray= JSONArray(data)
                var explain:String
                for (i in 0 until jsonArray.length()) {
                    if(jsonArray.getJSONObject(i).getString("word")==word) {
                        Letter.changeWord(word)
                        explain=jsonArray.getJSONObject(i).getString("explain")
                        Log.d("jsonArray[${i}]", explain)
                        val result = parseToJson(explain)
                        val newId=dataBaseHelper.insertDictionaryItem(word)
                        if(newId.toInt() !=-1){
                            // 插入成功
                            for(j in 0 until result.length()){
                                var meaning=result.getJSONObject(j).getString("partOfSpeech")+". "+result.getJSONObject(j).getString("meanings")
                                Log.d("meaning", meaning)
                                dataBaseHelper.insertWord(word,newId,meaning)
                                Letter.add(meaning)
                            }
                        }
                    }
                }
                Log.d("callback", Letter.meanings.toString())
                if(Letter.meanings.isEmpty()){
                    Log.d("callback", "Letter.meanings.toString().trim().isEmpty()")
                    callback(0)
                }else{
                    callback(1)
                }
            }

            override fun onError(e: Exception) {
                e.printStackTrace()
                callback(0)
            }

        })
    }

    fun SearchDataBase(word: String, dataBaseHelper: MyDataBaseHelper, callback: (result:Int) -> Unit){

    }

    fun parseString(input: String): List<Pair<String, String>> {
        val regex = Regex("""([a-z]+\.)([^a-z]+)""")
        return regex.findAll(input).map { matchResult ->
            val (partOfSpeech, meaning) = matchResult.destructured
            partOfSpeech.trimEnd('.') to meaning.trim()
        }.toList()
    }

    fun parseToJson(input: String): JSONArray {
        val regex = Regex("""([a-z]+\.)([^a-z]+?)(?=[a-z]+\.|$)""")
        val jsonArray = JSONArray()
        regex.findAll(input).forEach { matchResult ->
            val (partOfSpeech, meanings) = matchResult.destructured
            val trimmedMeaning = meanings.trim().split(",").map { it.trim() }
            val jsonObject = JSONObject()
            jsonObject.put("partOfSpeech", partOfSpeech.trimEnd('.'))
            jsonObject.put("meanings", JSONArray(trimmedMeaning))
            jsonArray.put(jsonObject)
        }
        return jsonArray
    }
}