package com.example.translate.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.translate.MyDataBaseHelper
import com.example.translate.databinding.FragmentRecordPageBinding
import com.example.translate.publicFunction.Companion.getCurrentDate

/**
 * A simple [Fragment] subclass.
 * Use the [RecordPageFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordPageFragment : Fragment() {

    private lateinit var binding: FragmentRecordPageBinding
    private lateinit var dbHelper: MyDataBaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentRecordPageBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper=MyDataBaseHelper(requireContext(), "letterdata.db", 1)

        binding.totalNum.text= dbHelper.getTotalWordCount().toString()

        val count=dbHelper.getRecord(getCurrentDate())
        if(count<0){
            // 新的日期
            dbHelper.insertOrUpdateRecord(getCurrentDate(),0)
            binding.todayNum.text="0"
        }else{
            binding.todayNum.text=count.toString()
        }
    }
}