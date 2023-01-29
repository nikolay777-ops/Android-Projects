package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnDataPass {

    private lateinit var calculatorFragment: Fragment
    private lateinit var numPadFragment: Fragment
    private lateinit var binding: ActivityMainBinding
    var results = ArrayList<Pair<String, String>>()

    override fun onDataPass(data: Pair<String, String>) {
        if(data.first == "User"){
            supportFragmentManager
                .beginTransaction()
                .apply {
                    replace(R.id.fragment_numPad, NumPadFragment.newInstance())
                }
                .commit()
            return
        }
        else if(data.first == "Science"){
            supportFragmentManager
                .beginTransaction()
                .apply {
                    replace(R.id.fragment_numPad, ScientificNumPadFragment.newInstance())
                }
                .commit()
            return
        }

        results.add(data)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportFragmentManager.fragments.size == 0){
            calculatorFragment = CalculatorFragment.newInstance()
            numPadFragment = NumPadFragment.newInstance()
        }
        else{
            calculatorFragment = supportFragmentManager.fragments[0]
            numPadFragment = supportFragmentManager.fragments[1]
        }
        binding.historyButton?.setOnClickListener{
            val builder = AlertDialog.Builder(this)

            val newList: ArrayList<String> = ArrayList()
            for (item in results){
                newList.add(item.first + "\n\n" + "Expr: " + item.second)
            }

            with(builder)
            {
                setTitle("History of results")
                setItems(newList.toTypedArray()) { dialog, which ->
                    val bundle = Bundle()
                    bundle.putString("res", results[which].first)
                    bundle.putString("expr", results[which].second)

                    var calcFragment = CalculatorFragment.newInstance()
                    calcFragment.arguments = bundle

                    supportFragmentManager
                        .beginTransaction()
                        .apply {
                            remove(calculatorFragment)
                        }
                        .commit()

                    supportFragmentManager
                        .beginTransaction()
                        .apply {
                            add(R.id.fragment_calculator, calcFragment)
                        }
                        .commit()

                    //Toast.makeText(applicationContext, results[which].first + " is clicked", Toast.LENGTH_SHORT).show()
                    return@setItems
                }
                show()
            }
        }
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragment_calculator, calculatorFragment)
            }
            .commit()

        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragment_numPad, numPadFragment)
            }
            .commit()
    }
}