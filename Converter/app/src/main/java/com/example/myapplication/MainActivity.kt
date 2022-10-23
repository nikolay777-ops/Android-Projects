package com.example.myapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ConverterFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var conv_frag: Fragment
    private lateinit var num_frag: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Main Activity onCreate")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (supportFragmentManager.fragments.size == 0){
            conv_frag = ConverterFragment.newInstance()
            num_frag = NumPadFragment.newInstance()
        }
        else{
            conv_frag = supportFragmentManager.fragments[0]
            num_frag = supportFragmentManager.fragments[1]
        }
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragment_converter, conv_frag)
            }
            .commit()
        supportFragmentManager
            .beginTransaction()
            .apply {
                replace(R.id.fragment_num_pad, num_frag)
            }
            .commit()
    }
}