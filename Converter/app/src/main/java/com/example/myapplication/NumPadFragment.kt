package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.databinding.FragmentConverterBinding
import com.example.myapplication.databinding.FragmentNumPadBinding

class NumPadFragment : Fragment(R.layout.fragment_num_pad) {

    private lateinit var binding : FragmentNumPadBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNumPadBinding.inflate(inflater, container, false)

        binding.btn0.setOnClickListener{
            val result = "0"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn1.setOnClickListener{
            val result = "1"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn2.setOnClickListener{
            val result = "2"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn3.setOnClickListener{
            val result = "3"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn4.setOnClickListener{
            val result = "4"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn5.setOnClickListener{
            val result = "5"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn6.setOnClickListener{
            val result = "6"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn7.setOnClickListener{
            val result = "7"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn8.setOnClickListener{
            val result = "8"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn9.setOnClickListener{
            val result = "9"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btn00.setOnClickListener{
            val result = "00"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btnClear.setOnClickListener{
            val result = "clear"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        binding.btnDel.setOnClickListener{
            val result = "delete"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.btnDel.setOnLongClickListener{
            val result = "delete_long"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
            return@setOnLongClickListener true
        }
        binding.btnDot.setOnClickListener{
            val result = "."
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = NumPadFragment()
    }
}