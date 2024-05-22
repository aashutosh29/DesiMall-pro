package com.aashutosh.desimall_pro.ui.categoryActivity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CategoryAdapter
import com.aashutosh.desimall_pro.adapter.ProductCategoryAdapter
import com.aashutosh.desimall_pro.databinding.ActivityCategoryBinding
import com.aashutosh.desimall_pro.models.Raw
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryActivity : AppCompatActivity(), CategoryView {
    lateinit var mainViewModel: StoreViewModel
    lateinit var binding: ActivityCategoryBinding
    private val catF: ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        categoryFetch()
        initRecyclerViewForCategory(
            Constant.alphas(),
            intent.getStringExtra(Constant.CATEGORY_NAME)!!
        )
        binding.tbMain.clSearch.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@CategoryActivity, SearchActivity::class.java))
        })
        binding.tbMain.ivBack.setOnClickListener(View.OnClickListener { finish() })

        mainViewModel.categoryItem.observe(this, Observer {
            it?.let { initCategoryRecyclerView(it) }

        })
    }
    

    private fun initRecyclerViewForCategory(categoryResponse: List<String>, clickedItem: String) {
        val recyclerview = findViewById<RecyclerView>(R.id.rvCategory)
        // this creates a vertical layout Manager
        recyclerview?.layoutManager =
            LinearLayoutManager(
                this@CategoryActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        val adapter = CategoryAdapter(
            categoryResponse,
            this@CategoryActivity,
            clickedItem
        )
        // Setting the Adapter with the recyclerview
        recyclerview?.adapter = adapter
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun categoryFetch() {

        GlobalScope.launch(Dispatchers.Main) {
            val catL = mainViewModel.allCategory()
            for (cat in catL) {
                catF.add(cat.name)
            }
            intent.getStringExtra(Constant.CATEGORY_NAME)?.let {
                getCategoryClicked2(
                    it,
                    binding.tvTest
                )
            }
        }
    }

    private fun initCategoryRecyclerView(categoryResponse: List<String>) {

        // this creates a vertical layout Manager
        binding.rvMain.layoutManager = LinearLayoutManager(this)
        val adapter =
            ProductCategoryAdapter(categoryResponse, this, this@CategoryActivity)
        // Setting the Adapter with the rvMain
        binding.rvMain.adapter = adapter
    }

    override fun getCategoryClicked(categoryItem: String) {
        val intent = Intent(this, CategoryBasedProductsActivity::class.java)


        intent.putExtra(
            Constant.CATEGORY_NAME, categoryItem
        )
        intent.putExtra(
            Constant.QUERY, "SELECT * FROM product WHERE subcategory_name = '$categoryItem'"
        )

        startActivity(intent)

    }

    override fun getCategoryClicked2(categoryItem: String, tvLogo: TextView) {


        val cat: ArrayList<String> = arrayListOf()
        for (category in catF) {
            if (category == "") {
                if (categoryItem == "A") {
                    cat.add(category)
                }
            } else {
                if (category[0].toString() == categoryItem) {
                    cat.add(category)
                }
            }
        }
        initCategoryRecyclerView(cat)


    }

    override fun getAdsClicked(ads: Raw) {
        TODO("Not yet implemented")
    }


}