package com.aashutosh.desimall_pro.new_theme_home



import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityNewHomeBinding
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class NewHomeActivity : AppCompatActivity() {
    private lateinit var mainViewModel: StoreViewModel
    lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ActivityNewHomeBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        mainViewModel = ViewModelProvider(this@NewHomeActivity)[StoreViewModel::class.java]
        GlobalScope.launch(Dispatchers.Main) {
            if (mainViewModel.getDesiProduct(
                    sharedPrefHelper[Constant.BRANCH_CODE, ""].toInt()
                )
            ) {
                if (mainViewModel.getFilteredCategoryFromProduct()) {
                    Toast.makeText(this@NewHomeActivity, "Success", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onCreateView: product updated")
                }
            }

            mainViewModel.getCartSize()

        }







    }






}