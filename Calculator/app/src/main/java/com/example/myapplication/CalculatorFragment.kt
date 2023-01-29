package com.example.myapplication

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnKeyListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.databinding.FragmentCalculatorBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.async
import java.math.BigDecimal
import java.security.Key




class CalculatorFragment : Fragment() {
    private lateinit var binding : FragmentCalculatorBinding
    lateinit var dataPasser: OnDataPass

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        dataPasser = context as OnDataPass
        binding = FragmentCalculatorBinding.inflate(inflater, container, false)

        binding.textResult.setOnKeyListener(object: OnKeyListener{
            override fun onKey(p0: View?, p1: Int, event: KeyEvent?): Boolean {
                if(event != null){
                    if(event.action == KeyEvent.ACTION_DOWN){
                        return true
                    }
                    if(event.action == KeyEvent.ACTION_UP){
                        return true
                    }
                }
                return false
            }
        })

        binding.textExpression.setOnClickListener {
            val tempText = binding.textResult.text
            binding.textResult.setText(binding.textExpression.text)
            binding.textExpression.text = tempText
        }

        if(arguments != null){
            val bundle = arguments
            val result = bundle!!.getString("res")
            val expr = bundle!!.getString("expr")
            binding.textResult.setText(result)
            binding.textExpression.text = expr
        }

        setFragmentResultListener("requestKey"){ key, bundle ->
            val numPad = bundle.getString("bundleKey")
            var inputExpression = StringBuilder(binding.textResult.text.toString())
            var position = binding.textResult.selectionEnd
            var text = StringBuilder(binding.textResult.text)

            if (numPad != null){
                if (numPad == "clearAll"){
                    binding.textResult.text.clear()
                    binding.textExpression.text = ""
                    return@setFragmentResultListener
                }
                else if(numPad == "clear"){
                    if (position != 0){
                        if (ExpressionParser.isLetter(text[position-1])){
                            text = StringBuilder(ExpressionParser.deleteFun(text.toString(), position))
                            binding.textResult.setText(text)
                            binding.textResult.setSelection(text.length)
                        }
                        else {
                            text = text.deleteAt(position-1)
                            binding.textResult.setText(text)
                            binding.textResult.setSelection(position-1)
                        }
                    }
//                    binding.textResult.setText(text)
//                    binding.textExpression.text = text.slice(0..text.length-2)
                }
                else if(numPad == "equal"){
                    val expr: String = inputExpression.toString()
                    var a = ""
                    var b = 0
                    val time = 5000L
                    var startTime = System.currentTimeMillis()

                    if(!ExpressionParser.checkBrackets(text.toString())){
                        Toast.makeText(activity, "You have incorrect brackets balance",
                            Toast.LENGTH_SHORT).show()
                        return@setFragmentResultListener
                    }

                    if (expr.isNotEmpty()){
                        val job = CoroutineScope(SupervisorJob()).launch { withContext(Dispatchers.IO) {
                            a = RPLCalc.calculate(expr).toString()
                            Log.d("RESULT: ", a)
                        } }

                        runBlocking {
                            launch { withContext(Dispatchers.IO){
                                while(job.isActive){
                                    if(System.currentTimeMillis() - startTime > time){
                                        job.cancel()
                                        Log.d("JOB is killed!", a)
                                        b = 1
                                    }
                                }
                            }
                            } }
                        if(a.isEmpty() && b == 1){
                            Toast.makeText(activity,
                                "Sorry, your expression was counted more than 5 seconds",
                                Toast.LENGTH_SHORT).show()
                            return@setFragmentResultListener
                        }
//                    CoroutineScope(SupervisorJob()).launch { withContext(Dispatchers.IO) {
//                        while(job.isActive){
//                            if(System.currentTimeMillis() - startTime > time){
//                                job.cancel()
//                                Log.d("JOB is killed!", a)
//                                Toast.makeText(activity,"Sorry, your expression was counted more than 2 seconds", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    } }
                        if(a.toBigDecimal() != (4799.145421).toBigDecimal()){
                            binding.textExpression.text = binding.textResult.text
                            binding.textResult.setText(a)
                            binding.textResult.setSelection(a.length)

                            val newPair = Pair(
                                binding.textResult.text.toString(),
                                binding.textExpression.text.toString()
                            )

                            passData(newPair)
                        }
                        else{
                            Toast.makeText(activity, "You can't divide by zero", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(activity,"Sorry, you try to calculate empty expression", Toast.LENGTH_SHORT).show()
                    }
                }
//                else if (numPad.contains("(")){
//                    text = text.insert(position, numPad)
//                    binding.textResult.setText(text)
//                    binding.textResult.setSelection(position+numPad.length-1)
//                }
                else{
                    if(RPLCalc.isSymbol(numPad)){
                        if (text.isNotEmpty() &&
                            !ExpressionParser.checkSymbols(text[position-1].toString(), numPad)){
                            Toast.makeText(activity, "You can't enter more than one symbol",
                                Toast.LENGTH_SHORT).show()
                            return@setFragmentResultListener
                        }
                        else if(text.isEmpty() && ExpressionParser.checkBrackets(numPad)){
                            Toast.makeText(activity, "You can't enter symbol without number", Toast.LENGTH_SHORT).show()
                            return@setFragmentResultListener
                        }
                    }
                    else if (numPad == "."){
                        if (!ExpressionParser.dotParser(ExpressionParser.getNum(text.toString()))){
                            Toast.makeText(activity, "You can't enter more than one dot in number",
                                Toast.LENGTH_SHORT).show()
                            return@setFragmentResultListener
                        }
                        else if (text.isNotEmpty() &&
                            RPLCalc.isSymbol(text[position-1].toString())){
                            Toast.makeText(activity, "You can't enter dot after symbol operator",
                            Toast.LENGTH_SHORT).show()
                            return@setFragmentResultListener
                        }
                    }
                        else if (numPad.contains("(")){
                        text = text.insert(position, numPad)
                        binding.textResult.setText(text)
                        binding.textResult.setSelection(position+numPad.length-1)
                        return@setFragmentResultListener
                }
                    if(text.isNotEmpty() && ExpressionParser.isLetter(text[position-1])){
                        position = text.length
                    }
                    text = text.insert(position, numPad)
                    binding.textResult.setText(text)
                    binding.textResult.setSelection(position+1)


//                    binding.textResult.append(numPad)
//                    inputExpression = inputExpression.append(numPad)
//                    binding.textExpression.text = inputExpression
                }
            }
        }

        binding.textResult.setHorizontallyScrolling(true)
        binding.textResult.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.textResult.showSoftInputOnFocus = false
        return binding.root
    }

    fun passData(data: Pair<String, String>){
        dataPasser.onDataPass(data)
    }

    companion object {

        @JvmStatic
        fun newInstance() = CalculatorFragment()

    }
}