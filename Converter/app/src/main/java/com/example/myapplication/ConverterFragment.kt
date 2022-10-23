package com.example.myapplication

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues.TAG
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnKeyListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import com.example.myapplication.databinding.FragmentConverterBinding
import org.w3c.dom.Text
import java.math.BigDecimal

class ConverterFragment : Fragment() {

    private lateinit var binding: FragmentConverterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Fragment onCreate")
    }

    fun countResult(res: Int = 1){
        if (res != 1){
            return
        }

        if(binding.inputText.text.isEmpty()){
            binding.outputText.text.clear()
            return
        }
        var firstMetric: Metric = binding.inputSpinner.selectedItem as Metric
        var secondMetric: Metric = binding.outputSpinner.selectedItem as Metric
        var number: BigDecimal = (firstMetric.number / secondMetric.number).toBigDecimal()
        var inputNumber = binding.inputText.text.toString().toBigDecimal()


        var text = (inputNumber.multiply(number)).toPlainString()
        var parsedText = TextParsers.zeroEndParser(text)
//        var parsedText: String = TextParsers.zeroEndParser(text)
        parsedText = TextParsers.findPeriod(parsedText)
        if(parsedText.length > 20){
            parsedText = parsedText.slice(0..21)
        }
        binding.outputText.setText(parsedText)

    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)

        var clipBoard : ClipboardManager? = null
        var res : Int = 1
        clipBoard = activity?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        binding = FragmentConverterBinding.inflate(inflater, container, false)
        var adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, info)
        binding.inputSpinner.adapter = adapter
        binding.outputSpinner.adapter = adapter
        adapter.notifyDataSetChanged()

        binding.inputText.setOnKeyListener(object : OnKeyListener{
            override fun onKey(p0: View?, p1: Int, event: KeyEvent?): Boolean {
                if (event != null) {
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

        binding.outputText.setOnKeyListener(object : OnKeyListener{
            override fun onKey(p0: View?, p1: Int, event: KeyEvent?): Boolean {
                if (event != null) {
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

        setFragmentResultListener("requestKey"){ key, bundle ->
            val numPad = bundle.getString("bundleKey")
            var inputText = StringBuilder(binding.inputText.text.toString())
            var position = binding.inputText.selectionEnd

            if (numPad == "clear" || numPad == "delete_long"){
                binding.inputText.text.clear()
                binding.outputText.text.clear()
                return@setFragmentResultListener
            }
            else if (numPad == "delete"){
                if(binding.inputText.text.isEmpty()){
                    binding.outputText.text.clear()
                    return@setFragmentResultListener
                }
                else if(binding.inputText.text[0] == '.'){
                    binding.inputText.setText("0${binding.inputText.text}")
                    binding.inputText.setSelection(position)
                }
                else if(binding.inputText.text.length == 2 && binding.inputText.text.contains('.')){
                    if(position == 1){
                        binding.inputText.text.clear()
                        binding.outputText.text.clear()
                        return@setFragmentResultListener
                    }
                }
                if(position != 0){
                    inputText = inputText.deleteAt(position - 1)
                    binding.inputText.setText(inputText)
                    binding.inputText.setSelection(position-1)
                }
            }
            else if (numPad == "."){
                if(!binding.inputText.text.contains(numPad)){
                    inputText = inputText.apply { insert(position, numPad) }
                    if(inputText[0].toString() == numPad){
                        inputText = inputText.apply { insert(0, "0") }
                        binding.inputText.setText(inputText)
                        binding.inputText.setSelection(position+2)
                        countResult()
                        return@setFragmentResultListener
                    }
                    binding.inputText.setText(inputText)
                    binding.inputText.setSelection(position+1)
                }
                else {
                    Toast.makeText(activity, "Sorry, you can put only one . in the number", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                if (inputText.length > 20){
                    Toast.makeText(activity, "Sorry, you can input only 20 symbols", Toast.LENGTH_SHORT).show()
                    return@setFragmentResultListener
                }
                inputText = inputText.apply{insert(position, numPad)}
                if(TextParsers.zeroParser(inputText.toString())){
                    if(numPad == "00"){
                        if(inputText.length == 2){
                            inputText = StringBuilder(inputText[0].toString())
                            binding.inputText.setText(inputText)
                            binding.inputText.setSelection(position+1)
                            binding.outputText.setText(inputText)
                            return@setFragmentResultListener
                        }
                        binding.inputText.setText(inputText)
                        binding.inputText.setSelection(position + 2)
                        return@setFragmentResultListener
                    }

                    binding.inputText.setText(inputText)
                    binding.inputText.setSelection(position + 1)
                }
                else{
                    Toast.makeText(activity, "Sorry, you put number in incorret format", Toast.LENGTH_SHORT).show()
                }

            }
            countResult()

        }

        binding.inputCopy.setOnClickListener {
            var myClip = ClipData.newPlainText("text", binding.inputText.text)
            clipBoard?.setPrimaryClip(myClip)
            Toast.makeText(activity, "Text Copied", Toast.LENGTH_SHORT).show()
        }

        binding.inputPaste.setOnClickListener {
            val clipArray = clipBoard?.primaryClip
            val item = clipArray?.getItemAt(0)
            try {
                val check = item?.text.toString().toBigDecimal()
                binding.inputText.setText(item?.text.toString())
                countResult()
                binding.inputText.setSelection(binding.inputText.text.length)
                binding.outputText.setSelection(binding.outputText.text.length)
            }
            catch (e : Exception) {
                Toast.makeText(activity, e.message.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        binding.outputCopy.setOnClickListener {
            val myClip = ClipData.newPlainText("text", binding.outputText.text)
            clipBoard?.setPrimaryClip(myClip)
            Toast.makeText(activity, "Text Copied", Toast.LENGTH_SHORT).show()
        }

        binding.swapButton.setOnClickListener{
            val inputText = binding.inputText.text.toString()
            binding.inputText.text = binding.outputText.text
            binding.inputText.setSelection(binding.outputText.text.length)
            countResult()
//            binding.outputText.setText(inputText)
        }

        binding.infoMetric.setOnClickListener{
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, info)
            binding.inputSpinner.adapter = adapter
            binding.outputSpinner.adapter = adapter
            countResult()
            adapter.notifyDataSetChanged()
        }

        binding.volumeMetric.setOnClickListener{
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, volume)
            binding.inputSpinner.adapter = adapter
            binding.outputSpinner.adapter = adapter
        }

        binding.speedMetric.setOnClickListener{
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, speed)
            binding.inputSpinner.adapter = adapter
            binding.outputSpinner.adapter = adapter
        }

        binding.inputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                countResult()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.outputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                countResult(res)
                res = 1
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        binding.inputText.setHorizontallyScrolling(true)
        binding.inputText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.inputText.showSoftInputOnFocus = false
        binding.outputText.setHorizontallyScrolling(true)
        binding.outputText.setRawInputType(0)
//        binding.outputText.setRawInputType(InputType.TYPE_CLASS_NUMBER)
        binding.outputText.showSoftInputOnFocus = false
        return binding.root


    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("INPUT_TEXT", binding.inputText.text.toString())
        super.onSaveInstanceState(outState)
        Log.d(TAG, "Fragment onSaveInstanceState")
    }

    override fun onDestroy() {
        super.onDestroy()
        val bundle = Bundle()
        bundle.putString("INPUT_TEXT", binding.inputText.text.toString())
        Log.d(TAG, "Fragment onDestroy")
    }

    companion object{
        @JvmStatic
        fun newInstance() = ConverterFragment()
    }
}


