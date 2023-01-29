package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.myapplication.databinding.FragmentScientificNumPadBinding

class ScientificNumPadFragment : Fragment(), OnDataPass {

    private lateinit var binding: FragmentScientificNumPadBinding
    private lateinit var dataPasser: OnDataPass

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPasser = context as OnDataPass
    }

    override fun onDataPass(data: Pair<String, String>) {
        dataPasser.onDataPass(data)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScientificNumPadBinding.inflate(inflater, container, false)

        binding.userModeBtn.setOnClickListener{
            onDataPass(Pair("User", ""))
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.allClearBtn.setOnClickListener{
            val result = "clear"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.allClearBtn.setOnLongClickListener {
            val result = "clearAll"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
            return@setOnLongClickListener true
        }
        binding.leftBrBtn.setOnClickListener {
            val result = "("
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.rightBrBtn.setOnClickListener {
            val result = ")"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.mulBtn.setOnClickListener {
            val result = "*"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.sevenBtn.setOnClickListener {
            val result = "7"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.eightBtn.setOnClickListener {
            val result = "8"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.nineBtn.setOnClickListener {
            val result = "9"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.delBtn.setOnClickListener {
            val result = "/"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.fourBtn.setOnClickListener {
            val result = "4"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.fiveBtn.setOnClickListener {
            val result = "5"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.sixBtn.setOnClickListener {
            val result = "6"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.minusBtn.setOnClickListener {
            val result = "-"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.oneBtn.setOnClickListener {
            val result = "1"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.twoBtn.setOnClickListener {
            val result = "2"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.threeBtn.setOnClickListener {
            val result = "3"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.plusBtn.setOnClickListener {
            val result = "+"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.zeroBtn.setOnClickListener {
            val result = "0"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.dotBtn.setOnClickListener {
            val result = "."
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.equalBtn.setOnClickListener {
            val result = "equal"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.sinBtn.setOnClickListener {
            val result = "sin()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.cosBtn.setOnClickListener {
            val result = "cos()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.tanBtn.setOnClickListener {
            val result = "tan()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.cotBtn.setOnClickListener {
            val result = "cot()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.squareBtn.setOnClickListener {
            val result = "^2"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.xyBtn.setOnClickListener {
            val result = "^"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.lnBtn.setOnClickListener {
            val result = "ln()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.sqrtBtn.setOnClickListener {
            val result = "sqrt()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
        binding.factBtn.setOnClickListener {
            val result = "fact()"
            setFragmentResult("requestKey", bundleOf("bundleKey" to result))
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() = ScientificNumPadFragment()
    }
}