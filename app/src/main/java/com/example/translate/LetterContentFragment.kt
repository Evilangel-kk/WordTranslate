package com.example.translate

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.translate.databinding.FragmentLetterContentBinding
import java.util.Calendar

/**
 * 展示搜索到的单词信息
 */
class LetterContentFragment : Fragment() {
    private lateinit var binding:FragmentLetterContentBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentLetterContentBinding.inflate(inflater,container,false)
        return binding.root
    }
    @SuppressLint("DiscouragedApi")
    fun refresh(meaning: String) {
        binding.meaning.text=meaning
    }
}