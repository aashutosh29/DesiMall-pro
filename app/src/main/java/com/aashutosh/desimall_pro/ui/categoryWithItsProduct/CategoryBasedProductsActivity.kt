package com.aashutosh.desimall_pro.ui.categoryWithItsProduct

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CategorizedProductPagingAdapter
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoryBasedProductsActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel
    lateinit var rvMain: RecyclerView
    lateinit var tvEmpty: TextView
    lateinit var tvToolbar: TextView
    lateinit var ivBack: ImageView
    lateinit var pagingAdapter: CategorizedProductPagingAdapter
    lateinit var tvCartNo: TextView
    lateinit var clCart: ConstraintLayout
    lateinit var clSearch: ConstraintLayout

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_based_products)
        findView()
        mainViewModel =
            ViewModelProvider(this@CategoryBasedProductsActivity)[StoreViewModel::class.java]

        intRecyclerView()
        Log.d(TAG, "categoryname: ${intent.getStringExtra(Constant.CATEGORY_NAME).toString()}")
        mainViewModel.getCatBasedDesiProduct(
            intent.getStringExtra(Constant.CATEGORY_NAME)!!.toString()
        )
            .observe(this, androidx.lifecycle.Observer {
                pagingAdapter.submitData(lifecycle, it)
            })




        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.getCartSize()
        }
        mainViewModel.cartSize.observe(this@CategoryBasedProductsActivity, Observer {
            if (it == 0) {
                tvCartNo.visibility = View.INVISIBLE
            } else {
                tvCartNo.visibility = View.VISIBLE
                tvCartNo.text = it.toString()
            }
        })
        clCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CategoryBasedProductsActivity, CartActivity::class.java)
            startActivity(intent)
        })

    }

    private fun intRecyclerView() {
        rvMain.layoutManager = GridLayoutManager(this, 2)
        pagingAdapter = CategorizedProductPagingAdapter(
            this@CategoryBasedProductsActivity,
            this@CategoryBasedProductsActivity
        )
        rvMain.adapter = pagingAdapter
    }


    private fun findView() {
        clCart = findViewById(R.id.clCart)
        tvCartNo = findViewById(R.id.tvCartNo)
        rvMain = findViewById(R.id.rvMain)
        tvEmpty = findViewById(R.id.tvEmpty)
        tvToolbar = findViewById(R.id.tvToolbarTitle)
        tvToolbar.text = intent.getStringExtra(Constant.CATEGORY_NAME)
        ivBack = findViewById(R.id.ivBack)
        clSearch = findViewById(R.id.clSearch)
        clSearch.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@CategoryBasedProductsActivity, SearchActivity::class.java))
        })
        ivBack.setOnClickListener(View.OnClickListener { finish() })
        /*cvShort.setOnClickListener(View.OnClickListener {
            ShortBottomSheet().show(supportFragmentManager, "short")
        })*/
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.getCartSize()
        }
    }

    fun getItemClicked(productItem: DesiDataResponseSubListItem) {
        val intent = Intent(this@CategoryBasedProductsActivity, ProductActivity::class.java)
        intent.putExtra(
            Constant.IMAGE_URL,
            if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"
        )
        intent.putExtra(
            Constant.PRODUCT_NAME, productItem.sku_name
        )
        intent.putExtra(Constant.ID, productItem.sku.toInt())
        Log.d(ContentValues.TAG, "getItemClicked: ${productItem.sku}")
        intent.putExtra(Constant.PRODUCT_PRICE, productItem.variant_sale_price.toString())
        intent.putExtra(Constant.MRP_PRICE, productItem.variant_mrp.toString())
        intent.putExtra(Constant.DESCRIPTION, productItem.sku_description)
        startActivity(intent)
    }


    @OptIn(DelicateCoroutinesApi::class)
    suspend fun addToCart(productItem: DesiDataResponseSubListItem) {

        if (mainViewModel.insertToCart(
                CartProduct(
                    productItem.sku.toInt(),
                    productItem.sku_name,
                    (if (productItem.sku == "") " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"),
                    productItem.sku_description,
                    1,
                    productItem.variant_sale_price.toDouble(),
                    productItem.variant_mrp.toDouble()
                )
            ) > 1
        ) {

            Toast.makeText(
                this@CategoryBasedProductsActivity,
                "${productItem.sku_name} ADDED TO CART",
                Toast.LENGTH_SHORT
            ).show()
            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.getCartSize()
            }

        } else {
            Toast.makeText(
                this@CategoryBasedProductsActivity,
                "This product is already added please check the cart.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}


