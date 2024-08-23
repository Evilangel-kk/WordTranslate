package com.example.translate

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.translate.Letter.word
import com.example.translate.fragment.SearchPageFragment
import java.lang.Integer.parseInt

class MyDataBaseHelper(context: Context, name: String, version: Int) :
    SQLiteOpenHelper(context, name, null, version) {
    // 已添加单词表-词典
    private val createDictionaryTable = ("create table DictionaryTable ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "word TEXT NOT NULL UNIQUE"
            +");")
    // 已单词词义表-单词
    private val createWordTable = ("create table WordTable ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "word TEXT NOT NULL,"
            + "dictionary_id INTEGER,"
            + "definition TEXT NOT NULL,"
            + "FOREIGN KEY (dictionary_id) REFERENCES DictionaryTable (id)"
            +");")
    // 学习记录表-日期
    private val createLearningRecordTable = ("create table LearningRecordTable ("
            + "date TEXT PRIMARY KEY,"
            + "word_count INTEGER NOT NULL"
            +");")

    // 学习记录表-日期
    private val createNumListRecordTable = ("create table NumListRecordTable ("
            + "num INTEGER PRIMARY KEY,"
            + "date TEXT"
            +");")

    override fun onCreate(p0: SQLiteDatabase?) {
        if (p0 != null) {
            p0.execSQL(createDictionaryTable)
            Log.d("DataBase", "createDictionaryTable Created")
            p0.execSQL(createWordTable)
            Log.d("DataBase", "createWordTable Created")
            p0.execSQL(createLearningRecordTable)
            Log.d("DataBase", "createLearningRecordTable Created")
            p0.execSQL(createNumListRecordTable)
            Log.d("DataBase", "createNumListRecordTable Created")
        }
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    // 插入词典
    fun insertDictionaryItem(letter: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("word", letter)
        }
        val newRowId = db.insert("DictionaryTable", null, values)
        if (newRowId == -1L) {
            Log.e("DataBase", "Error inserting item into DictionaryTable")
            return -1
        } else {
            Log.d("DataBase", "Item inserted into DictionaryTable with row id: $newRowId")
            return newRowId
        }
    }

    // 插入单词表
    fun insertWord(word: String, dictionaryId: Long, definition: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("word", word)
            put("dictionary_id", dictionaryId)
            put("definition", definition)
        }
        val newRowId = db.insert("WordTable", null, values)
        if (newRowId == -1L) {
            Log.e("DataBase", "Error inserting item into WordTable")
        } else {
            Log.d("DataBase", "Item inserted into WordTable with row id: $newRowId")
        }
    }

    // 插入学习记录
    fun insertRecord(date:String, num:Int){
        val db = writableDatabase
        val values = ContentValues().apply {
            put("num", num)
            put("date", date)
        }
        val newRowId = db.insert("NumListRecordTable", null, values)
        if (newRowId == -1L) {
            Log.e("DataBase", "Error inserting item into NumListRecordTable")
        } else {
            Log.d("DataBase", "Item inserted into NumListRecordTable with row id: $newRowId")
        }
    }

    // 查询记录是否存在
    @SuppressLint("Range")
    fun doesRecordExistToday(num:Int):Boolean{
        val db: SQLiteDatabase = readableDatabase
        val query = "SELECT COUNT(*) AS count FROM NumListRecordTable WHERE num = ?"
        val cursor = db.rawQuery(query, arrayOf(num.toString()))

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(cursor.getColumnIndex("count")) > 0
        }
        cursor.close() // 关闭 cursor
        return exists // 返回是否包含该单词
    }

    // 查询当日所有记录
    fun getTodayRecord(date:String,callback: (result:Int) -> Unit){
        val db = readableDatabase
        val query = "SELECT definition FROM NumListRecordTable WHERE date = ?"
        val cursor = db.rawQuery(query, arrayOf(date))
        NumList.clearAll()
        if (cursor.moveToFirst()) {
            do {
                val num = cursor.getInt(cursor.getColumnIndexOrThrow("num"))
                NumList.addNumber(num)
            } while (cursor.moveToNext())
        }
        cursor.close()
        callback(1)
    }

    // 查询当日记录数量
    @SuppressLint("Range")
    fun getTodayRecordCount(date:String,callback: (result:Int) -> Unit):Int{
        val db = readableDatabase
        val query = "SELECT COUNT(*) AS num FROM NumListRecordTable WHERE date = ?"
        val cursor = db.rawQuery(query, arrayOf(date))

        var wordCount = 0
        if (cursor.moveToFirst()) {
            wordCount = cursor.getInt(cursor.getColumnIndex("num"))
        }
        cursor.close() // 关闭 cursor
        return wordCount // 返回单词总数
    }

    // 清空当日记录表
    fun clearTodayRecord(){
        val db = writableDatabase
        db.execSQL("delete from NumListRecordTable") // 数据库清空，再次插入就是最新的数据
        // 重置自增主键计数器
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='NumListRecordTable'")
    }

    // 查询单词在单词表中的所有释义
    fun getDefinitionsForWord(word: String, callback: (result:Int) -> Unit) {
        val db = readableDatabase
        val definitions = mutableListOf<String>()

        val query = "SELECT definition FROM WordTable WHERE word = ?"
        val cursor = db.rawQuery(query, arrayOf(word))

        if (cursor.moveToFirst()) {
            do {
                val definition = cursor.getString(cursor.getColumnIndexOrThrow("definition"))
                definitions.add(definition)
            } while (cursor.moveToNext())
        }

        cursor.close()
        Letter.changeWord(word)
        Letter.meanings=definitions
        callback(1)
    }

    // 查询单词在单词表中的所有释义
    fun getDefinitionsForWordById(Id: Int, callback: (result:Int) -> Unit) {
        val db = readableDatabase
        val definitions = mutableListOf<String>()

        val query = "SELECT definition FROM WordTable WHERE dictionary_id = ?"
        val cursor = db.rawQuery(query, arrayOf(Id.toString()))

        if (cursor.moveToFirst()) {
            do {
                val definition = cursor.getString(cursor.getColumnIndexOrThrow("definition"))
                definitions.add(definition)
            } while (cursor.moveToNext())
        }

        cursor.close()
        Letter.changeWord(word)
        Letter.meanings=definitions
        callback(1)
    }

    // 获得单词ID
    fun getWordId(word: String): Int? {
        val db: SQLiteDatabase = readableDatabase
        val projection = arrayOf("id") // 只需要 id 列
        val selection = "word = ?" // 查询条件
        val selectionArgs = arrayOf(word) // 查询参数

        // 执行查询
        val cursor: Cursor = db.query(
            "DictionaryTable", // 表名
            projection, // 需要的列
            selection, // 查询条件
            selectionArgs, // 查询参数
            null, // 分组
            null, // 筛选
            null // 排序
        )

        var wordId: Int? = null
        if (cursor.moveToFirst()) {
            // 获取 id 列的索引
            val idIndex = cursor.getColumnIndex("id")
            if (idIndex != -1) {
                // 从 cursor 中获取 id 值
                wordId = cursor.getInt(idIndex)
            }
        }
        cursor.close() // 关闭 cursor
        return wordId // 返回查询结果
    }

    // 根据Id获得单词
    fun getWordById(Id:Int): String? {
        val db: SQLiteDatabase = readableDatabase
        val projection = arrayOf("word")
        val selection = "id = ?" // 查询条件
        val selectionArgs = arrayOf(Id.toString()) // 查询参数

        // 执行查询
        val cursor: Cursor = db.query(
            "DictionaryTable", // 表名
            projection, // 需要的列
            selection, // 查询条件
            selectionArgs, // 查询参数
            null, // 无分组
            null, // 无筛选
            null // 排序方式
        )

        var wordFound: String? = null
        if (cursor.moveToFirst()) {
            // 获取 id 列的索引
            val wordIndex = cursor.getColumnIndex("word")
            if (wordIndex != -1) {
                // 从 cursor 中获取 id 值
                wordFound = cursor.getString(wordIndex)
            }
        }
        cursor.close() // 关闭 cursor
        return wordFound // 返回查询结果
    }

    // 查询所有单词
    fun getAllWordsOrderedById(): List<Pair<Int, String>> {
        val db: SQLiteDatabase = readableDatabase
        val projection = arrayOf("id", "word") // 查询 id 和 word 列
        val sortOrder = "id ASC" // 按 id 升序排序

        // 执行查询
        val cursor: Cursor = db.query(
            "DictionaryTable", // 表名
            projection, // 需要的列
            null, // 无查询条件
            null, // 无查询参数
            null, // 无分组
            null, // 无筛选
            sortOrder // 排序方式
        )

        val wordsList = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            // 获取 id 和 word 列的索引
            val idIndex = cursor.getColumnIndex("id")
            val wordIndex = cursor.getColumnIndex("word")
            if (idIndex != -1 && wordIndex != -1) {
                // 从 cursor 中获取 id 和 word 值
                val id = cursor.getInt(idIndex)
                val word = cursor.getString(wordIndex)
                wordsList.add(id to word)
            }
        }
        cursor.close() // 关闭 cursor
        return wordsList // 返回按 ID 排序的单词列表
    }

    // 查询大于ID的所有单词
    fun getWordsWithIdGreaterThan(givenId: Int): List<Pair<Int, String>> {
        val db: SQLiteDatabase = readableDatabase
        val projection = arrayOf("id", "word")
        val selection = "id > ?"
        val selectionArgs = arrayOf(givenId.toString())
        val sortOrder = "id ASC"

        // 执行查询
        val cursor: Cursor = db.query(
            "DictionaryTable",
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        val wordsList = mutableListOf<Pair<Int, String>>()
        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex("id")
            val wordIndex = cursor.getColumnIndex("word")
            if (idIndex != -1 && wordIndex != -1) {
                val id = cursor.getInt(idIndex)
                val word = cursor.getString(wordIndex)
                wordsList.add(id to word)
            }
        }
        cursor.close() // 关闭 cursor
        return wordsList // 返回 ID 大于给定值的单词列表
    }

    // 统计单词数量
    @SuppressLint("Range")
    fun getTotalWordCount(): Int {
        val db: SQLiteDatabase = readableDatabase
        val query = "SELECT COUNT(*) AS wordCount FROM DictionaryTable"
        val cursor = db.rawQuery(query, null)

        var wordCount = 0
        if (cursor.moveToFirst()) {
            wordCount = cursor.getInt(cursor.getColumnIndex("wordCount"))
        }
        cursor.close() // 关闭 cursor
        return wordCount // 返回单词总数
    }

    // 查询单词是否存在
    @SuppressLint("Range")
    fun containsWord(word: String): Boolean {
        val db: SQLiteDatabase = readableDatabase
        val query = "SELECT COUNT(*) AS count FROM DictionaryTable WHERE word = ?"
        val cursor = db.rawQuery(query, arrayOf(word))

        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(cursor.getColumnIndex("count")) > 0
        }
        cursor.close() // 关闭 cursor
        return exists // 返回是否包含该单词
    }

    // 插入时间记录
    fun insertOrUpdateRecord(date: String, wordCount: Int) {
        val db = writableDatabase

        // 检查记录是否存在
        val existingRecord = getRecord(date)

        if (existingRecord >= 0) {
            // 记录存在，更新记录的单词数量
            val newWordCount = existingRecord + wordCount
            val values = ContentValues().apply {
                put("word_count", newWordCount)
            }

            db.update(
                "LearningRecordTable",
                values,
                "date = ?",
                arrayOf(date)
            )
        } else {
            // 记录不存在，插入新记录
            val values = ContentValues().apply {
                put("date", date)
                put("word_count", wordCount)
            }

            db.insert("LearningRecordTable", null, values)
        }
    }

    fun getRecord(date: String): Int {
        val db = readableDatabase

        val cursor = db.query(
            "LearningRecordTable",
            arrayOf("date", "word_count"),
            "date = ?",
            arrayOf(date),
            null,
            null,
            null
        )

        val record = if (cursor.moveToFirst()) {
            val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
            val wordCount = cursor.getInt(cursor.getColumnIndexOrThrow("word_count"))
            Pair(date, wordCount)
        } else {
            null
        }

        cursor.close()
        if (record != null) {
            return record.second
        }else{
            return -1
        }
    }

    fun doesRecordExist(date: String): Boolean {
        val db = readableDatabase

        val cursor = db.query(
            "LearningRecordTable",
            arrayOf("date"), // 只需要查询 "date" 列
            "date = ?", // 查询条件
            arrayOf(date), // 查询参数
            null,
            null,
            null
        )

        // 检查 cursor 是否有数据
        val exists = cursor.moveToFirst()

        cursor.close()
        return exists
    }

    // 重置表
    fun reSet(){
        val db=writableDatabase
        db.execSQL("delete from WordTable") // 数据库清空，再次插入就是最新的数据
        db.execSQL("delete from DictionaryTable") // 数据库清空，再次插入就是最新的数据
        db.execSQL("delete from LearningRecordTable") // 数据库清空，再次插入就是最新的数据
        // 重置自增主键计数器
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='WordTable'")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='DictionaryTable'")
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='LearningRecordTable'")
    }

}