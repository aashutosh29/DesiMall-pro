package com.aashutosh.simplestore.ui.cartActivity

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
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.adapter.CartAdapter
import com.aashutosh.simplestore.models.CartProduct
import com.aashutosh.simplestore.models.DeliveryDetails
import com.aashutosh.simplestore.ui.CartInterface
import com.aashutosh.simplestore.ui.HomeActivity
import com.aashutosh.simplestore.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.simplestore.ui.proceedToCheckOut.ProceedToCheckOutActivity
import com.aashutosh.simplestore.viewModels.StoreViewModel
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


    @BindView(R.id.llEmpty)
    lateinit var llEmpty: LinearLayout

    var deliveryDetails: List<DeliveryDetails> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        ButterKnife.bind(this)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        checkDB()
        initRecyclerView()

    }

    @OptIn(DelicateCoroutinesApi::class)
    fun checkDB() {
        GlobalScope.launch {
            deliveryDetails = mainViewModel.getProfileDetails()
        }
    }


    private fun roundUpDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.CEILING
        return df.format(number).toDouble()
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
            tvTotal.text = "₹ ${roundUpDecimal(total)}"
            tvSubTotal.text = "₹ ${roundUpDecimal(subTotal)}"
            tvDiscount.text = "₹ ${roundUpDecimal((subTotal - total))}"

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