package com.example.translate

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.translate.databinding.ActivityMainBinding
import com.example.translate.fragment.RecordPageFragment
import com.example.translate.fragment.ReviewPageFragment
import com.example.translate.fragment.SearchPageFragment
import com.example.translate.publicFunction.Companion.getCurrentDate
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: MyDataBaseHelper
    private var isReadyQuit = 0
    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "MyPrefs"
    private val LAST_DATE_KEY = "lastDate"
    private var lastDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = "Translate"
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 删除名为 "my_database.db" 的数据库
//        deleteDatabase(this, "letterdata.db")

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        dbHelper = MyDataBaseHelper(this, "letterdata.db", 1)

        // 重置
//        dbHelper.reSet()
//        val editor = sharedPreferences.edit()
//        editor.putInt("currentNum", -1)
//        editor.putString(LAST_DATE_KEY, "null")
//        editor.apply()

        // Initialize SharedPreferences

        lastDate = sharedPreferences.getString(LAST_DATE_KEY, "null").toString()
        if(lastDate=="null"){
            lastDate=getCurrentDate()
            val editor = sharedPreferences.edit()
            editor.putString(LAST_DATE_KEY, lastDate)
            editor.apply()
            NumList.clearAll()
        }else if(lastDate!=getCurrentDate()){
            lastDate=getCurrentDate()
            val editor = sharedPreferences.edit()
            editor.putString(LAST_DATE_KEY, lastDate)
            editor.apply()
            dbHelper.clearTodayRecord()
            NumList.clearAll()
        }


        Log.d("DataBase", dbHelper.getTotalWordCount().toString())
        Log.d("fragmentCount",supportFragmentManager.backStackEntryCount.toString())

        loadFragment(SearchPageFragment())
        binding.bottomNavigation.setOnNavigationItemSelectedListener (
            BottomNavigationView.OnNavigationItemSelectedListener{
                when (it.itemId) {
                    R.id.nav_search -> {
                        loadFragment(SearchPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.nav_list -> {
                        loadFragment(ReviewPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }

                    R.id.nav_record -> {
                        loadFragment(RecordPageFragment())
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }
        )
        // Setup the back press callback
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("Activity","back")
                val fragmentCount = supportFragmentManager.backStackEntryCount
                Log.d("fragmentCount",supportFragmentManager.backStackEntryCount.toString())
                if(isReadyQuit==0){
                    isReadyQuit=1
                    supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    // 让BottomNavigationView切换到SearchPageFragment
                    binding.bottomNavigation.selectedItemId = R.id.nav_search
                    Toast.makeText(this@MainActivity,"再按一次退出",Toast.LENGTH_LONG).show()
                }else{
                    finish()
                }
            }
        })
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }

    fun deleteDatabase(context: Context, databaseName: String) {
        context.deleteDatabase(databaseName)
    }
}