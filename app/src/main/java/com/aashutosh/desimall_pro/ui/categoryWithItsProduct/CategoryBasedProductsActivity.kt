package com.aashutosh.desimall_pro.ui.categoryWithItsProduct

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CategorizedProductPagingAdapter
import com.aashutosh.desimall_pro.databinding.ActivityCategoryBasedProductsBinding
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
class CategoryBasedProductsActivity : AppCompatActivity(), CategoryBasedProductView {
    lateinit var mainViewModel: StoreViewModel
    lateinit var binding: ActivityCategoryBasedProductsBinding
    lateinit var pagingAdapter: CategorizedProductPagingAdapter
    private var handlerCallbacksRemoved = false

    private lateinit var handler: Handler
    private var currentIndex = 0
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBasedProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()


        mainViewModel =
            ViewModelProvider(this@CategoryBasedProductsActivity)[StoreViewModel::class.java]
        intRecyclerView()


        mainViewModel.allProductsLiveData.observe(this, Observer { productList ->
            if (productList.isNullOrEmpty()) {
                // Data is not present in the database, show the loading screen
                // Fetch data from the API and save it to the database
               showLoading()
            } else {
                // Data is present in the database, hide the loading screen if needed
                // Display the data from the database
                mainViewModel.getKeyValueBasedProduct(
                    query = intent.getStringExtra(Constant.QUERY)!!.toString(),
                )
                    .observe(this, androidx.lifecycle.Observer {
                        pagingAdapter.submitData(lifecycle, it)
                    })
                hideLoading()
            }
        })




        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.getCartSize()
        }
        mainViewModel.cartSize.observe(this@CategoryBasedProductsActivity, Observer {
            if (it == 0) {
                binding.tbMain.tvCartNo.visibility = View.INVISIBLE
            } else {
                binding.tbMain.tvCartNo.visibility = View.VISIBLE
                binding.tbMain.tvCartNo.text = it.toString()
            }
        })


    }


   private fun showLoading(){
       handlerCallbacksRemoved = true
       handler = Handler(Looper.getMainLooper())
       updateViews(binding.loadingScreen.tv2, binding.loadingScreen.ivImg, handler, currentIndex)
    }

    private  fun hideLoading(){
        binding.rvMain.visibility = View.VISIBLE
        binding.loadingScreen.clMain.visibility = View.GONE
        if (handlerCallbacksRemoved){
            handler.removeCallbacksAndMessages(null)}
        handlerCallbacksRemoved = false

    }


    override fun onDestroy() {
        super.onDestroy()
       if (handlerCallbacksRemoved){
        handler.removeCallbacksAndMessages(null)}
    }
    private fun intRecyclerView() {
        binding.rvMain.layoutManager = GridLayoutManager(this, 2)
        pagingAdapter = CategorizedProductPagingAdapter(
            this@CategoryBasedProductsActivity,
            this@CategoryBasedProductsActivity
        )
        binding.rvMain.adapter = pagingAdapter
    }
    private fun updateViews(tv2: TextView, ivImg: ImageView, handler: Handler, currentIndex: Int) {
        val dataList: List<Pair<String, Int>> = listOf(
            "Live Desi Mall in house products are pure." to R.drawable.one,
            "A2 ghee has higher nutritional values" to R.drawable.two,
            "Pure Honey is an anti-inflammatory, antioxidant and antibacterial agent" to R.drawable.three,
            "Cinnamon is loaded with antioxidants and reduces inflammation" to R.drawable.four
            // Add more pairs as needed
        )

        val (text, imageResId) = dataList[currentIndex]
        tv2.text = text
        ivImg.setImageResource(imageResId)

        // Move to the next index
        val nextIndex = (currentIndex + 1) % dataList.size

        // Schedule the next update after 10 seconds
        handler.postDelayed({ updateViews(tv2, ivImg, handler, nextIndex) }, 5000)
    }

    private fun initView() {
        if (intent.getStringExtra(Constant.CATEGORY_NAME)!!.toString().length <= 1) {
            binding.tbMain.tvToolbarTitle.text = "Products"
        } else {
            binding.tbMain.tvToolbarTitle.text = intent.getStringExtra(Constant.CATEGORY_NAME)
        }
        binding.tbMain.clSearch.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@CategoryBasedProductsActivity, SearchActivity::class.java))
        })
        binding.tbMain.ivBack.setOnClickListener(View.OnClickListener { finish() })
        binding.tbMain.clCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@CategoryBasedProductsActivity, CartActivity::class.java)
            startActivity(intent)
        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.getCartSize()
        }
    }

    override fun getItemClicked(productItem: DesiDataResponseSubListItem) {
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
        intent.putExtra(Constant.PRODUCT_PUBLISHED,productItem.Published)
        intent.putExtra(Constant.PRODUCT_SERVER_QTY,productItem.product_quantity.toString())
        intent.putExtra(Constant.MRP_PRICE, productItem.variant_mrp.toString())
        intent.putExtra(Constant.DESCRIPTION, productItem.sku_description)
        startActivity(intent)
    }


    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun addToCart(productItem: DesiDataResponseSubListItem, quantity: Int) {

        if (mainViewModel.insertToCart(
                CartProduct(
                    productItem.sku.toInt(),
                    productItem.sku_name,
                    (if (productItem.sku == "") " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"),
                    productItem.sku_description,
                    quantity,
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


