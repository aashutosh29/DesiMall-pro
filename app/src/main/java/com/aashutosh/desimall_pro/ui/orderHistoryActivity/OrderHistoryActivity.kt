package com.aashutosh.desimall_pro.ui.orderHistoryActivity

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.aashutosh.desimall_pro.adapter.OrderListAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ActivityOrderHistoryBinding
import com.aashutosh.desimall_pro.models.Order
import com.aashutosh.desimall_pro.utils.Constant
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class OrderHistoryActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    lateinit var binding: ActivityOrderHistoryBinding
    lateinit var sharedPrefHelper: SharedPrefHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this)
        initView()
        callDb()
    }

    private fun initView() {
        binding.srlMain.setOnRefreshListener(this)
        binding.ivBack.setOnClickListener(View.OnClickListener { this.finish() })
    }

    private fun callDb() {
        val db = Firebase.firestore
        var order: Order
        val orderList: ArrayList<Order> = arrayListOf()
        db.collection("order").whereEqualTo("phone", sharedPrefHelper[Constant.PHONE_NUMBER, ""])
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    order = Order(
                        id = document.id,
                        name = document.data["name"].toString(),
                        address = document.data["address"].toString(),
                        phone = document.data["phone"].toString(),
                        zip = document.data["zip"].toString(),
                        branchCode = document.data["branchCode"].toString(),
                        date = document.data["date"].toString(),
                        status = document.data["status"].toString(),
                        products = document.data["product"] as List<String>,
                        totalProduct = document.data["totalProduct"].toString(),
                        totalPrice = document.data["totalPrice"].toString()
                    )
                    orderList.add(order)
                    Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                    Log.d(ContentValues.TAG, "testing order: $order")
                }

                initRecyclerView(orderList)
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Error Loading", Toast.LENGTH_SHORT).show()
                Log.w(ContentValues.TAG, "Error getting documents.", exception)
            }
    }

    private fun initRecyclerView(orders: List<Order>) {
        if (binding.srlMain.isRefreshing) {
            binding.srlMain.isRefreshing = false
        }
        if (orders.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.rvOrderHistory.visibility = View.INVISIBLE

        } else {
            binding.tvEmpty.visibility = View.INVISIBLE
            binding.rvOrderHistory.visibility = View.VISIBLE
            // this creates a vertical layout Manager
            binding.rvOrderHistory.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            val adapter = OrderListAdapter(orders, this@OrderHistoryActivity)
            // Setting the Adapter with the recyclerview
            binding.rvOrderHistory.adapter = adapter
        }
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    override fun onRefresh() {

        callDb()
    }

}