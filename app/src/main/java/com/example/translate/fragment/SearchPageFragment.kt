package com.example.translate.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.translate.GetTranslate
import com.example.translate.Letter
import com.example.translate.LetterMeaningsListFragment
import com.example.translate.MyDataBaseHelper
import com.example.translate.databinding.FragmentSearchPageBinding
import com.example.translate.setupEditText


/**
 * A simple [Fragment] subclass.
 * Use the [SearchPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchPageFragment : Fragment() {

    private lateinit var binding: FragmentSearchPageBinding
    private lateinit var dbHelper: MyDataBaseHelper
    private lateinit var fragment: LetterMeaningsListFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentSearchPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("FragmentBackPressedCallback")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEditText(binding.myEditText)
        dbHelper=MyDataBaseHelper(requireContext(), "letterdata.db", 1)

        // 创建 LetterMeaningsListFragment 实例
        fragment = LetterMeaningsListFragment()
        // 动态添加 Fragment
        childFragmentManager.beginTransaction()
            .replace(binding.letterMeaningsListFrag.id, fragment)
            .commit()
        binding.letterMeaningsListFrag.visibility=View.INVISIBLE

        binding.myEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // 处理回车事件
                Log.d("actionId","EditorInfo.IME_ACTION_DONE")
                sendRequest()
                true
            } else {
                false
            }
        }

        binding.mySearchBtn.setOnClickListener {
            sendRequest()
        }
    }

    fun getTranslate(callback: (result: Int) -> Unit) {
        GetTranslate.Search(Letter.word, dbHelper) { result ->
            if(result==1){
                Log.d("meaningsList", "" + Letter.meanings)
                callback(1)
            }else{
                callback(0)
            }
        }
    }

    fun sendRequest(){
        if(binding.myEditText.text.toString().trim().isNotEmpty()){
            // 获取输入的文本
            Letter.changeWord(binding.myEditText.text.toString())
            Log.d("Letter.changeWord", "KeyWord:"+binding.myEditText.text.toString())
            Log.d("getTranslate", "等待更新Fragment")
            // 获取翻译并更新 Fragment
            getTranslate { result ->
                if (result == 1) {
                    fragment.setMeanings(1)
                    Log.d("getTranslate", "更新到Fragment")
                    //                    dbHelper.insertOrUpdateRecord(getCurrentDate(), 1)
                    requireActivity().runOnUiThread{
                        binding.theWord.text = Letter.word
                        binding.letterMeaningsListFrag.visibility=View.VISIBLE
                    }
                }else{
                    requireActivity().runOnUiThread{
                        binding.theWord.text = "无结果"
                        binding.letterMeaningsListFrag.visibility=View.INVISIBLE
                    }
                }
            }
        }
    }
}