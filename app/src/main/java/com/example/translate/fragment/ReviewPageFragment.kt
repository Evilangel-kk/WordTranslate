package com.example.translate.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.translate.LetterMeaningsListFragment
import com.example.translate.MyDataBaseHelper
import com.example.translate.NumList
import com.example.translate.R
import com.example.translate.databinding.FragmentReviewPageBinding
import com.example.translate.publicFunction.Companion.getCurrentDate
import java.lang.Math.abs


/**
 * A simple [Fragment] subclass.
 * Use the [ReviewPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ReviewPageFragment : Fragment() {
    private lateinit var binding: FragmentReviewPageBinding
    private lateinit var dbHelper: MyDataBaseHelper
    private lateinit var sharedPreferences: SharedPreferences
    private val PREFS_NAME = "MyPrefs"
    private val CURRENT_NUM_KEY = "currentNum"
    private var currentNum: Int = 0

    private lateinit var fragment : LetterMeaningsListFragment


    private var isForget:Int = 0
    private val verticalMinistance = 100 // 水平最小识别距离
    private val minVelocity = 10 // 最小识别速度


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentReviewPageBinding.inflate(inflater,container,false)

        // 创建 GestureDetector 对象
        val gestureDetector = GestureDetector(requireContext(), LearnGestureListener())

        // 为 fragment 的根视图添加 OnTouchListener 监听器
        binding.root.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
        }
        return binding.root
    }

    // 设置手势识别监听器
    inner class LearnGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
            when {
                e1.x - e2.x > verticalMinistance && Math.abs(velocityX) > minVelocity -> nextWord()
                e2.x - e1.x > verticalMinistance && Math.abs(velocityX) > minVelocity -> preWord()
            }
            return false
        }

        // 此方法必须重写且返回真，否则 onFling 不起效
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

    private fun nextWord(){
        currentNum++
        updateCurrentNum()
    }

    private fun preWord(){
        Log.d("currentNum",currentNum.toString())
        currentNum--
        updateCurrentNum()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper=MyDataBaseHelper(requireContext(), "letterdata.db", 1)

        binding.checkBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_forget))
        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Retrieve the stored currentNum or default to 0 if not set
        currentNum = sharedPreferences.getInt(CURRENT_NUM_KEY, -1)
        if (currentNum <= 0) {
            // If no value is found, set a default initial value
            if(dbHelper.getTotalWordCount()>0){
                currentNum = 1
                binding.theWord.text=dbHelper.getWordById(currentNum)
            }else{
                currentNum = 0
                binding.theWord.text="表里空空滴"
            }

            // Save this initial value to SharedPreferences
            val editor = sharedPreferences.edit()
            editor.putInt(CURRENT_NUM_KEY, currentNum)
            editor.apply()
        }else{
            binding.theWord.text=dbHelper.getWordById(currentNum)
        }

        Log.d("currentNum",currentNum.toString())

        // 创建 LetterMeaningsListFragment 实例
        fragment = LetterMeaningsListFragment()
        // 动态添加 Fragment
        childFragmentManager.beginTransaction()
            .replace(binding.letterMeaningsListFrag.id, fragment)
            .commit()

        binding.letterMeaningsListFrag.visibility= View.INVISIBLE

        binding.checkBtn.setOnClickListener {
            // 数据库中查找单词释义
            dbHelper.getDefinitionsForWordById(currentNum){result ->
                // 找到后回调更新UI
                if(result==1){
                    fragment.setMeanings(1)
                    Log.d("getTranslate","更新Fragment")
                }
            }
            binding.letterMeaningsListFrag.visibility= View.VISIBLE

            if(isForget==0){
                isForget=1
                binding.checkBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_ok))
                binding.letterMeaningsListFrag.visibility= View.VISIBLE
            }else{
                isForget=0
                binding.checkBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_forget))
                binding.letterMeaningsListFrag.visibility= View.INVISIBLE
            }
        }
    }
    private fun updateCurrentNum() {
        isForget=0
        binding.checkBtn.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_forget))
        // Save the updated currentNum to SharedPreferences
        val count=dbHelper.getTotalWordCount()
        if(count==0){
            currentNum=0
            val editor = sharedPreferences.edit()
            editor.putInt(CURRENT_NUM_KEY, currentNum)
            editor.apply()
            return
        }
        Log.d("count0",count.toString())
        Log.d("currentNum0",currentNum.toString())
        if(currentNum>count){
            if(count==1){
                currentNum=1
            }else{
                currentNum = currentNum % count
            }
            Log.d("currentNum",currentNum.toString())
        }else if(currentNum<=0){
            currentNum = count-currentNum
        }

        val editor = sharedPreferences.edit()
        editor.putInt(CURRENT_NUM_KEY, currentNum)
        editor.apply()
        updateLetterMsg(fragment)
        if(!dbHelper.doesRecordExist(getCurrentDate())){
            NumList.clearAll()
            dbHelper.clearTodayRecord()
        }
        if(!dbHelper.doesRecordExistToday(currentNum)){
            NumList.addNumber(currentNum)
            dbHelper.insertRecord(getCurrentDate(),currentNum)
            dbHelper.insertOrUpdateRecord(getCurrentDate(),1)
        }
    }

    private fun updateLetterMsg(fragment: LetterMeaningsListFragment) {
        binding.theWord.text=dbHelper.getWordById(currentNum)
        binding.letterMeaningsListFrag.visibility= View.INVISIBLE
    }
}