package com.aashutosh.desimall_pro.ui.cartActivity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CartAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails
import com.aashutosh.desimall_pro.ui.CartInterface
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.desimall_pro.ui.proceedToCheckOut.ProceedToCheckOutActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.BRANCH_CODE
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpDecimal
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpString
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.RoundingMode
import java.text.DecimalFormat

@AndroidEntryPoint
class CartActivity : AppCompatActivity(), CartInterface {

    lateinit var mainViewModel: StoreViewModel
    lateinit var cartAdapter: CartAdapter

    @BindView(R.id.tvSubTotal)
    lateinit var tvSubTotal: TextView

    @BindView(R.id.tvDiscount)
    lateinit var tvDiscount: TextView

    @BindView(R.id.tvTotal)
    lateinit var tvTotal: TextView

    @BindView(R.id.llDetails)
    lateinit var llDetails: LinearLayoutCompat

    @BindView(R.id.tbCheckOut)
    lateinit var tbCheckOut: Button


    @BindView(R.id.recyclerview)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.acbShopNow)
    lateinit var acbShopNow: AppCompatButton

    @BindView(R.id.tvDeliveryCharge)
    lateinit var tvDeliveryCharge: TextView


    @BindView(R.id.llEmpty)
    lateinit var llEmpty: LinearLayout
    lateinit var sharedPreferHelper: SharedPrefHelper

    var deliveryDetails: List<DeliveryDetails> = arrayListOf()

    //tbCheckOut
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        ButterKnife.bind(this)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        checkDB()
        initRecyclerView()
        sharedPreferHelper = SharedPrefHelper
        sharedPreferHelper.init(this)

        if (sharedPreferHelper[Constant.BRANCH_NAME, ""] == Constant.BRANCH_NAME){
            tbCheckOut.text = "Delivery not available in your area. "
            tbCheckOut.isEnabled = false
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkDB() {
        GlobalScope.launch {
            deliveryDetails = mainViewModel.getProfileDetails()
        }
    }


    override suspend fun deleteProduct(cartProduct: CartProduct) {
        if (mainViewModel.deleteProduct(cartProduct) == 1) {
            initRecyclerView()
        }

    }

    override suspend fun updateQty(cartItem: CartProduct, quantity: Int) {
        cartItem.quantity = quantity
        if (mainViewModel.updateQty(cartItem) == 1) {
            initRecyclerView()
        }
    }

    @OnClick(R.id.ivBack)
    fun ivBackClicked() {
        this.finish()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            val value = mainViewModel.getDummyCart()
            recyclerView.layoutManager = LinearLayoutManager(this@CartActivity)
            recyclerView.isNestedScrollingEnabled = false
            cartAdapter = CartAdapter(
                value,
                this@CartActivity,
                this@CartActivity
            )
            recyclerView.adapter = cartAdapter
            if (value.isEmpty()) {
                llDetails.visibility = View.INVISIBLE
                tbCheckOut.visibility = View.INVISIBLE
                recyclerView.visibility = View.INVISIBLE
                llEmpty.visibility = View.VISIBLE
            } else {
                llDetails.visibility = View.VISIBLE
                tbCheckOut.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                llEmpty.visibility = View.INVISIBLE
            }
            var total = 0.0
            var subTotal = 0.0
            for (product in value) {
                total = (total + (product.price * product.quantity))
                subTotal = (subTotal + (product.mrp * product.quantity))
            }
            tvTotal.text = "₹ ${roundUpString(total.toString())}"
            tvSubTotal.text = "₹ ${roundUpString(subTotal.toString())}"
            tvDiscount.text = "₹ ${roundUpString((subTotal - total).toString())}"

            if (total < 1000) {
                tvDeliveryCharge.text = "₹ 50"
                total += 50
                tvTotal.text = "₹ ${roundUpString(total.toString())}"
            } else {
                tvDeliveryCharge.text = "free"
                tvTotal.text = "₹ ${roundUpString(total.toString())}"
            }

        }
    }


    @OnClick(R.id.tbCheckOut)
    fun tbCheckOutClicked() {

        if (deliveryDetails.isEmpty()) {
            startActivity(
                Intent(
                    this@CartActivity,
                    DeliveryAddressActivity::class.java
                )
            )
        } else {
            startActivity(Intent(this@CartActivity, ProceedToCheckOutActivity::class.java))

        }
    }

    override fun onResume() {
        super.onResume()
        checkDB()
    }

    @OnClick(R.id.acbShopNow)
    fun acbClicked() {
        startActivity(Intent(this@CartActivity, HomeActivity::class.java))
    }


}