package com.aashutosh.desimall_pro.ui.proceedToCheckOut

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import com.aashutosh.desimall_pro.OrderSuccessActivity
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CartAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityProceedToCheckOutBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.DeliveryDetails
import com.aashutosh.desimall_pro.models.makeOrder.Billing
import com.aashutosh.desimall_pro.models.makeOrder.LineItem
import com.aashutosh.desimall_pro.models.makeOrder.OrderPlace
import com.aashutosh.desimall_pro.models.makeOrder.Shipping
import com.aashutosh.desimall_pro.ui.CartInterface
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.roundUpString
import com.aashutosh.desimall_pro.utils.Constant.Companion.setProgressDialog
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
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


    lateinit var progressDialog: AlertDialog
    lateinit var cartAdapter: CartAdapter
    lateinit var binding: ActivityProceedToCheckOutBinding
    var cartProductList: List<CartProduct> = arrayListOf()
    lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var mainViewModel: StoreViewModel
    var total = 0.00


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProceedToCheckOutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this@ProceedToCheckOutActivity)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        initView()
        initRecyclerView()
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun initRecyclerView() {
        GlobalScope.launch(Dispatchers.Main) {
            cartProductList = mainViewModel.getDummyCart()
            binding.recyclerView.layoutManager = LinearLayoutManager(this@ProceedToCheckOutActivity)
            binding.recyclerView.isNestedScrollingEnabled = false
            cartAdapter = CartAdapter(
                cartProductList, this@ProceedToCheckOutActivity, this@ProceedToCheckOutActivity
            )
            binding.recyclerView.adapter = cartAdapter
            if (cartProductList.isEmpty()) {
                binding.llDetails.visibility = View.INVISIBLE
                binding.btOrder.visibility = View.INVISIBLE
                binding.recyclerView.visibility = View.INVISIBLE
                binding.llEmpty.visibility = View.VISIBLE
            } else {
                binding.llDetails.visibility = View.VISIBLE
                binding.btOrder.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.llEmpty.visibility = View.INVISIBLE
            }
            var subTotal = 0.0
            for (product in cartProductList) {
                total = (total + (product.price * product.quantity))
                subTotal = (subTotal + (product.mrp * product.quantity))
            }
            binding.tvTotal.text = "₹ ${roundUpString(total.toString())}"
            binding.tvSubTotal.text = "₹ ${roundUpString(subTotal.toString())}"
            binding.tvDiscount.text = "₹ ${roundUpString((subTotal - total).toString())}"

            if (total < 1000) {
                binding.tvDeliveryCharge.text = "₹ 50"
                total += 50
                binding.tvTotal.text = "₹ ${roundUpString(total.toString())}"
            }else{
                binding.tvDeliveryCharge.text = "free"
                binding.tvTotal.text = "₹ ${roundUpString(total.toString())}"
            }

        }

    }

    private fun initView() {
        binding.ivBack.setOnClickListener(View.OnClickListener {
            this.finish()
        })
        binding.btEdit.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    this@ProceedToCheckOutActivity, DeliveryAddressActivity::class.java
                )
            )
        })
        binding.btEditPayment.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                this@ProceedToCheckOutActivity,
                "Other method will be available soon",
                Toast.LENGTH_SHORT
            ).show()
        })
        binding.btOrder.setOnClickListener(View.OnClickListener {
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


    private fun initProgressDialog(): AlertDialog {
        progressDialog = setProgressDialog(this, "Processing checkout..")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }


    private fun btOrder() {
        initProgressDialog().show()
        val billing = Billing(
            address_1 = sharedPrefHelper[Constant.ADDRESS],
            address_2 = sharedPrefHelper[Constant.ADDRESS_FULL_DETAILS],
            city = "",
            country = "India",
            email = "ldmjaipur@gmail.com",
            first_name = sharedPrefHelper[Constant.NAME,""].split(" ")[0],
            last_name = sharedPrefHelper[Constant.NAME,""].split(" ")[1],
            phone = sharedPrefHelper[Constant.PHONE_NUMBER,""],
            postcode = sharedPrefHelper[Constant.ZIP,""],
            state = ""
        )

        val shipping = Shipping(
            address_1 = sharedPrefHelper[Constant.ADDRESS],
            address_2 = sharedPrefHelper[Constant.ADDRESS_FULL_DETAILS],
            city = "",
            country = "India",
            first_name = sharedPrefHelper[Constant.NAME,""].split(" ")[0],
            last_name = sharedPrefHelper[Constant.NAME,""].split(" ")[1],
            postcode = sharedPrefHelper[Constant.ZIP,""],
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
        val order = OrderPlace(
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
            "branchCode" to sharedPrefHelper[Constant.BRANCH_CODE],
            "date" to dateFormat.format(date),
            "status" to "0",
            "totalPrice" to roundUpString(total.toString()),
            "totalProduct" to "${cartProductList.size}"

        )
        addStoreToFireBase(store, itemString, order)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun addStoreToFireBase(
        order: HashMap<String, String>,
        itemString: ArrayList<String>,
        order1: OrderPlace
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
                                OrderSuccessActivity::class.java
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

                Log.w(TAG, "Error placing order", e)
            }
        }


    }
}



