package com.aashutosh.desimall_pro.ui.productScreen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator

@AndroidEntryPoint
class ProductActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel

    @BindView(R.id.tvProductName)
    lateinit var tvProductName: TextView

    @BindView(R.id.tvShipping)
    lateinit var tvShipping: TextView

    @BindView(R.id.tvProductPrice)
    lateinit var tvProductPrice: TextView

    @BindView(R.id.ivBack)
    lateinit var ivBack: ImageView
    lateinit var viewPagerAdapter: ImageSlideAdapter
    lateinit var indicator: CircleIndicator

    @BindView(R.id.viewpager)
    lateinit var viewpager: ViewPager

    @BindView(R.id.ivDefaultImage)
    lateinit var ivDefaultImage: ImageView

    @BindView(R.id.tvMrp)
    lateinit var tvMrp: TextView


    @BindView(R.id.ivPlus)
    lateinit var ivPlus: ImageView

    @BindView(R.id.ivMinus)
    lateinit var ivMinus: ImageView

    @BindView(R.id.btAddToCart)
    lateinit var btAddToCart: AppCompatButton

    @BindView(R.id.tvQuantity)
    lateinit var tvQuantity: TextView

    @BindView(R.id.tvCartNo)
    lateinit var tvCartNo: TextView

    var quantitiy: Int = 1
    lateinit var cartProduct: CartProduct

    @BindView(R.id.clCart)
    lateinit var clCart: ConstraintLayout

    @BindView(R.id.tvDetails)
    lateinit var tvDetails: TextView


    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        ButterKnife.bind(this)
        val id = intent.getIntExtra(Constant.ID, 0)
        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.fetchProductItem(id)

            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.getCartSize()
            }
            mainViewModel.cartSize.observe(this@ProductActivity, Observer {
                if (it == 0) {
                    tvCartNo.visibility = View.INVISIBLE
                } else {
                    tvCartNo.visibility = View.VISIBLE
                    tvCartNo.text = it.toString()
                }
            })

            if (intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble() -
                intent.getStringExtra(Constant.MRP_PRICE)!!.toDouble() == 0.0
            ) {
                tvMrp.visibility = View.GONE
            }



            if (intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble() < 1000) {
                tvShipping.text = "₹ 50"
            } else {
                tvShipping.text = "FREE"
            }

            populateView(
                intent.getStringExtra(Constant.PRODUCT_NAME)!!,
                intent.getStringExtra(Constant.PRODUCT_PRICE)!!,
                intent.getStringExtra(Constant.DESCRIPTION)!!,
                intent.getStringExtra(Constant.MRP_PRICE)!!,
                arrayListOf(intent.getStringExtra(Constant.IMAGE_URL)!!)
            )

            cartProduct = CartProduct(
                intent.getIntExtra(Constant.ID, 0),
                intent.getStringExtra(Constant.PRODUCT_NAME)!!,
                intent.getStringExtra(Constant.IMAGE_URL)!!,
                intent.getStringExtra(Constant.DESCRIPTION)!!,
                quantitiy,
                intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble(),
                intent.getStringExtra(Constant.MRP_PRICE)!!.toDouble()
            )

        }
        initView()
        clCart.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ProductActivity, CartActivity::class.java))
        })
        mainViewModel = ViewModelProvider(this@ProductActivity)[StoreViewModel::class.java]

        ivBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        ivPlus.setOnClickListener(View.OnClickListener {

            if (quantitiy < 100) {
                quantitiy++

                if (intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble()*quantitiy < 1000) {
                    tvShipping.text = "₹ 50"
                } else {
                    tvShipping.text = "FREE"
                }

                tvQuantity.text = quantitiy.toString()
                tvMrp.text = "₹ ${
                    Constant.roundUpString(
                        (intent.getStringExtra(Constant.MRP_PRICE)!!.toDouble() * quantitiy).toString()
                    )
                }"
                tvProductPrice.text = "₹ ${
                    Constant.roundUpString(
                        (intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble() * quantitiy).toString()
                    )
                }"
            }

        })

        ivMinus.setOnClickListener(View.OnClickListener {

            if (quantitiy > 1) {
                quantitiy--

                if (intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble()*quantitiy < 1000) {
                    tvShipping.text = "₹ 50"
                } else {
                    tvShipping.text = "FREE"
                }

                tvQuantity.text = quantitiy.toString()
                tvMrp.text = "₹ ${
                    Constant.roundUpDecimal(
                        intent.getStringExtra(Constant.MRP_PRICE)!!.toDouble() * quantitiy
                    )
                }"
                tvProductPrice.text = "₹ ${
                    Constant.roundUpDecimal(
                        intent.getStringExtra(Constant.PRODUCT_PRICE)!!.toDouble() * quantitiy
                    )
                }"

            }
        })

    }

    private fun initView() {
        tvMrp.paintFlags = tvMrp.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
    }


    @OptIn(DelicateCoroutinesApi::class)
    @OnClick(R.id.btAddToCart)
    fun btAddToCartClicked() {
        GlobalScope.launch(Dispatchers.Main) {
            cartProduct.quantity = quantitiy
            mainViewModel.insertToCart(cartProduct)
        }
        this.finish()
        Toast.makeText(this, "${cartProduct.name} added to cart", Toast.LENGTH_SHORT).show()
    }

    private fun populateView(
        productName: String,
        productPrice: String,
        productDescription: String,
        mrp: String,
        images: ArrayList<String>
    ) {

        if (images.isEmpty()) {
            ivDefaultImage.visibility = View.VISIBLE
        } else {
            ivDefaultImage.visibility = View.INVISIBLE
            images.let {
                viewPagerAdapter = ImageSlideAdapter(this, it)
                viewpager.adapter = viewPagerAdapter
                indicator = findViewById<CircleIndicator>(R.id.indicator)
                indicator.setViewPager(viewpager)
            }
        }
        tvDetails.text = productDescription
        tvProductName.text = productName
        tvProductPrice.text = "₹ ${Constant.roundUpString(productPrice)}"
        tvMrp.text = "₹ ${Constant.roundUpString(mrp)}"

    }
}