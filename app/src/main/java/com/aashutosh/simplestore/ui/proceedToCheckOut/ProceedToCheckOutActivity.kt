package com.aashutosh.simplestore.ui.proceedToCheckOut

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
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
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.models.CartProduct
import com.aashutosh.simplestore.models.DeliveryDetails
import com.aashutosh.simplestore.models.makeOrder.Billing
import com.aashutosh.simplestore.models.makeOrder.LineItem
import com.aashutosh.simplestore.models.makeOrder.Order
import com.aashutosh.simplestore.models.makeOrder.Shipping
import com.aashutosh.simplestore.ui.CartInterface
import com.aashutosh.simplestore.ui.HomeActivity
import com.aashutosh.simplestore.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.simplestore.utils.Constant
import com.aashutosh.simplestore.utils.Constant.Companion.roundUpDecimal
import com.aashutosh.simplestore.viewModels.StoreViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class ProceedToCheckOutActivity : AppCompatActivity(), CartInterface {

    lateinit var tvAddress: TextView
    lateinit var tvName: TextView
    lateinit var progressDialog: AlertDialog
    lateinit var cartAdapter: CartAdapter

    @BindView(R.id.tvSubTotal)
    lateinit var tvSubTotal: TextView

    @BindView(R.id.tvDiscount)
    lateinit var tvDiscount: TextView

    @BindView(R.id.tvTotal)
    lateinit var tvTotal: TextView

    @BindView(R.id.llDetails)
    lateinit var llDetails: LinearLayoutCompat

    @BindView(R.id.btOrder)
    lateinit var btOrder: Button


    @BindView(R.id.recyclerview)
    lateinit var recyclerView: RecyclerView

    @BindView(R.id.acbShopNow)
    lateinit var acbShopNow: AppCompatButton


    @BindView(R.id.llEmpty)
    lateinit var llEmpty: LinearLayout

    @BindView(R.id.btEditPayment)
    lateinit var btEditPayment: AppCompatButton

    @BindView(R.id.tvContact)
    lateinit var tvContact: TextView

    var deliveryDetails: List<DeliveryDetails> = arrayListOf()
    var cartProductList: List<CartProduct> = arrayListOf()

    lateinit var sharePreHelper: SharedPrefHelper


    private lateinit var mainViewModel: StoreViewModel


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proceed_to_check_out)
        ButterKnife.bind(this)
        sharePreHelper = SharedPrefHelper
        sharePreHelper.init(this@ProceedToCheckOutActivity)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]

        initView()
        initRecyclerView()
        GlobalScope.launch(Dispatchers.Main) {
            deliveryDetails = mainViewModel.getProfileDetails()
            if (deliveryDetails.isNotEmpty()) {
                tvAddress.text = deliveryDetails[0].address
                tvName.text = deliveryDetails[0].name
                tvContact.text = deliveryDetails[0].mobileNum

            }
        }


    }

    @OnClick(R.id.ivBack)
    fun ivBackClicked() {
        this.finish()
    }

    @OnClick(R.id.btEditPayment)
    fun btEditPaymentClicked() {
        Toast.makeText(
            this@ProceedToCheckOutActivity,
            "Other method will be available soon",
            Toast.LENGTH_SHORT
        ).show()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun initRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            cartProductList = mainViewModel.getDummyCart()
            recyclerView.layoutManager = LinearLayoutManager(this@ProceedToCheckOutActivity)
            recyclerView.isNestedScrollingEnabled = false
            cartAdapter = CartAdapter(
                cartProductList, this@ProceedToCheckOutActivity, this@ProceedToCheckOutActivity
            )
            recyclerView.adapter = cartAdapter
            if (cartProductList.isEmpty()) {
                llDetails.visibility = View.INVISIBLE
                btOrder.visibility = View.INVISIBLE
                recyclerView.visibility = View.INVISIBLE
                llEmpty.visibility = View.VISIBLE
            } else {
                llDetails.visibility = View.VISIBLE
                btOrder.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                llEmpty.visibility = View.INVISIBLE
            }
            var total = 0.0
            var subTotal = 0.0
            for (product in cartProductList) {
                total = (total + (product.price * product.quantity))
                subTotal = (subTotal + (product.mrp * product.quantity))
            }
            tvTotal.text = "₹ ${roundUpDecimal(total)}"
            tvSubTotal.text = "₹ ${roundUpDecimal(subTotal)}"
            tvDiscount.text = "₹ ${roundUpDecimal((subTotal - total))}"

        }

    }


    private fun initView() {
        tvAddress = findViewById(R.id.tvAddress)
        tvName = findViewById(R.id.tvName)
        btOrder = findViewById(R.id.btOrder)
        btOrder.setOnClickListener(View.OnClickListener {
            btOrder()
        })
    }


    @OnClick(R.id.acbShopNow)
    fun acbClicked() {
        startActivity(Intent(this@ProceedToCheckOutActivity, HomeActivity::class.java))
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

    @OnClick(R.id.btEdit)
    fun btEditClicked() {
        startActivity(
            Intent(
                this@ProceedToCheckOutActivity, DeliveryAddressActivity::class.java
            )
        )
    }

    private fun setProgressDialog(context: Context, message: String): AlertDialog {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.START
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.START
        ll.layoutParams = llParam

        val progressBar = ProgressBar(context)
        progressBar.isIndeterminate = true
        progressBar.setPadding(0, 0, llPadding, 0)
        progressBar.layoutParams = llParam

        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = message
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 16.toFloat()
        tvText.layoutParams = llParam

        ll.addView(progressBar)
        ll.addView(tvText)

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        builder.setView(ll)

        val dialog = builder.create()
        val window = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window?.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window?.attributes = layoutParams
        }
        return dialog
    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = setProgressDialog(this, "Processing checkout..")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }


    private fun btOrder() {
        initProgressDialog().show()
        val billing = Billing(
            address_1 = deliveryDetails[0].address,
            address_2 = deliveryDetails[0].landMark,
            city = "",
            country = "India",
            email = "ldmjaipur@gmail.com",
            first_name = deliveryDetails[0].name.split(" ")[0],
            last_name = deliveryDetails[0].name.split(" ")[1],
            phone = deliveryDetails[0].mobileNum,
            postcode = deliveryDetails[0].zipCode,
            state = ""
        )

        val shipping = Shipping(
            address_1 = deliveryDetails[0].address,
            address_2 = deliveryDetails[0].landMark,
            city = "",
            country = "India",
            first_name = deliveryDetails[0].name.split(" ")[0],
            last_name = deliveryDetails[0].name.split(" ")[1],
            postcode = deliveryDetails[0].zipCode,
            state = ""
        )
        val lineItemList = arrayListOf<LineItem>()
        val itemString = arrayListOf<String>()
        for (product in cartProductList) {
            val lineItem = LineItem(product.productId, product.quantity)
            lineItemList.add(lineItem)
            itemString.add("${product.productId}++${product.name}++${product.image}++${product.quantity}++${product.price}++${product.mrp}")
        }
        val itemList = arrayListOf<HashMap<String, String>>();

        for (product in lineItemList) {
            val item = hashMapOf(
                "productId" to product.product_id.toString(),
                "productQty" to product.quantity.toString()
            )
            itemList.add(item)

        }
        val order = com.aashutosh.simplestore.models.makeOrder.Order(
            billing = billing,
            line_items = lineItemList,
            payment_method = "COD",
            payment_method_title = "COD",
            set_paid = false,
            shipping = shipping
        )
        val dateFormat: DateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()

        val store = hashMapOf(
            "name" to "${order.billing.first_name} ${order.billing.last_name}",
            "address" to order.billing.address_1 + " Near by " + order.billing.address_2,
            "phone" to order.billing.phone,
            "zip" to order.billing.postcode,
            "branchCode" to sharePreHelper[Constant.BRANCH_CODE],
            "date" to dateFormat.format(date),
            "status" to "0"
        )
        addStoreToFireBase(store, itemString, order)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun addStoreToFireBase(
        order: HashMap<String, String>,
        itemString: ArrayList<String>,
        order1: Order
    ) {
        GlobalScope.launch(Dispatchers.Default) {

            val db = Firebase.firestore
            db.collection("order").add(order).addOnSuccessListener { documentReference ->

                val order = db.collection("order").document(documentReference.id)
                order.update("product", itemString).addOnSuccessListener { value ->
                    Log.d(TAG, "value: $value")
                    progressDialog.dismiss()
                    GlobalScope.launch {
                        Log.d(
                            TAG, "addStoreToFireBase: " + mainViewModel.sendNotification(
                                "store",
                                order1.billing.first_name + " " + order1.billing.last_name,
                                       order1.billing.address_1,
                                "0"
                            ).string()
                        )

                        /*Toast.makeText(
                            this@ProceedToCheckOutActivity,
                            "Notification sent",
                            Toast.LENGTH_SHORT
                        ).show()*/


                    }
                    Toast.makeText(
                        this@ProceedToCheckOutActivity,
                        "Order Placed Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    GlobalScope.launch {
                        mainViewModel.deleteAllCart()
                        startActivity(
                            Intent(
                                this@ProceedToCheckOutActivity,
                                HomeActivity::class.java
                            )
                        )
                    }

                }.addOnFailureListener { e ->
                    Toast.makeText(
                        this@ProceedToCheckOutActivity, "error -> $e", Toast.LENGTH_SHORT
                    ).show()
                }

                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }.addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    this@ProceedToCheckOutActivity, "error -> $e", Toast.LENGTH_SHORT
                ).show()

                Log.w(TAG, "Error adding document", e)
            }
        }


    }
}



