package com.example.translate

import android.annotation.SuppressLint
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.translate.Letter.meanings
import com.example.translate.databinding.FragmentLetterContentBinding
import com.example.translate.databinding.FragmentLetterMeaningsListBinding
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [LetterMeaningsListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LetterMeaningsListFragment : Fragment() {

    private lateinit var binding: FragmentLetterMeaningsListBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding= FragmentLetterMeaningsListBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val layoutManager = LinearLayoutManager(activity)
        binding.letterMeaningsListRecyclerView.layoutManager=layoutManager
        // 配置适配器
        val adapter = NewsAdapter(Letter.meanings)
        binding.letterMeaningsListRecyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMeanings(result:Int) {
        if(result==1){
            (binding.letterMeaningsListRecyclerView.adapter as? NewsAdapter)?.updateMeanings(1)
            Log.d("Notify",Letter.meanings.toString())
        }
    }

    inner class NewsAdapter(private var meaningsList: List<String>) :
        RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
        private lateinit var binding: FragmentLetterContentBinding

        @SuppressLint("NotifyDataSetChanged")
        fun updateMeanings(result:Int) {
            if(result==1){
                requireActivity().runOnUiThread{
                    meaningsList=Letter.meanings
                    notifyDataSetChanged()
                    Log.d("notifyDataSetChanged",meaningsList.toString())
                }
            }
        }

        inner class ViewHolder(binding: FragmentLetterContentBinding) : RecyclerView.ViewHolder(binding.root) {
            val meaning=binding.meaning
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            binding=FragmentLetterContentBinding.inflate(LayoutInflater.from(parent.context),parent,false)
            val holder = ViewHolder(binding)
            return holder
        }
        @SuppressLint("DiscouragedApi")
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val meaning = meaningsList[position]
            holder.meaning.text=meaning
        }
        override fun getItemCount() = meaningsList.size
    }
}