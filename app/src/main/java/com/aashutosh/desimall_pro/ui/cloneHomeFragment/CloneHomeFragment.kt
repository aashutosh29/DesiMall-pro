package com.aashutosh.desimall_pro.ui.cloneHomeFragment


import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.AdsAdapter
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.HomeFragmentBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.Raw
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.LoginActivity
import com.aashutosh.desimall_pro.ui.barCodeActivity.BarCodeActivity
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.categoryActivity.CategoryActivity
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
import com.aashutosh.desimall_pro.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.ui.myProfileActivity.MyProfileActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.ui.searchActivity.HomeView
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
import com.aashutosh.desimall_pro.utils.AppSignatureHashHelper
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.utils.Constant.Companion.autoScroll
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.bumptech.glide.Glide
import com.drakeet.multitype.MultiTypeAdapter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CloneHomeFragment : AppCompatActivity(), CategoryView {
    private lateinit var mainViewModel: StoreViewModel
    private lateinit var binding: HomeFragmentBinding // Change the binding class to the appropriate activity layout binding.
    private lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var viewPagerAdapter: ImageSlideAdapter
    private lateinit var progressDialog: AlertDialog

    @VisibleForTesting
    internal lateinit var items: MutableList<Any>

    @VisibleForTesting
    internal lateinit var adapter: MultiTypeAdapter

    private var homeView: HomeView? = null

    private lateinit var signatureHashHelper: AppSignatureHashHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(applicationContext)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        mainViewModel.getBannerList()
        callDb()
        signatureHashHelper = AppSignatureHashHelper(this)
        Log.d(TAG, "onCreate Signature: " + signatureHashHelper.appSignatures)

        if (!areNotificationsEnabled(this)) {
            showEnableNotificationDialog()
        }
        // Your onViewCreated logic for setting up views in the fragment goes here.
        if (sharedPrefHelper[Constant.PHOTO, ""] != "") {
            Glide.with(this)
                .load(sharedPrefHelper[Constant.PHOTO, ""])
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }
        if (sharedPrefHelper[Constant.NAME, ""] != "") {
            binding.tvLogin.text = sharedPrefHelper[Constant.NAME, ""]
        }

        binding.clSearchIcon.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
            startActivity(intent)
        }
        binding.ivBack.setOnClickListener(View.OnClickListener {
            onBackPressed()
        })
        binding.tvBranch.text = sharedPrefHelper[Constant.BRANCH_NAME, ""]
        binding.clBarCode.setOnClickListener {
            val intent = Intent(this, BarCodeActivity::class.java)
            startActivity(intent)
        }

        // binding.ivFilter.setOnClickListener {
        //     val intent = Intent(this, SearchActivity::class.java)
        //     intent.putExtra(Constant.IS_SEARCH_FOCUS, false)
        //     startActivity(intent)
        // }

        binding.clCatIcon.setOnClickListener {
            val intent = Intent(this, CategoryActivity::class.java)
            intent.putExtra(Constant.CATEGORY_NAME, "A")
            startActivity(intent)
        }

        binding.clNoti.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        }

        binding.clCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        initSlider()

        binding.llProfile.setOnClickListener {
            if (!sharedPrefHelper[Constant.LOGIN_SUCCESS, false]) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                val intent = Intent(this, MyProfileActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }


    }
    private fun initProgressDialog(): AlertDialog {
        progressDialog = Constant.setProgressDialog(this, "Fetching Category")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    private fun showEnableNotificationDialog() {
        val builder = AlertDialog.Builder(this@CloneHomeFragment)
        builder.setTitle("Enable Notifications")
            .setMessage("To receive important updates and notifications, please enable notifications for this app.")
            .setPositiveButton("Enable") { dialog: DialogInterface, _: Int ->
                // Open the notification settings screen
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, this@CloneHomeFragment.packageName)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }




    private fun initSlider() {

        val db = Firebase.firestore
        var ads: Raw
        val adsArrayList: ArrayList<Raw> = arrayListOf()
        val filteredCatList: ArrayList<Raw> = arrayListOf()
        db.collection("slider")
            .orderBy("position", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { result ->
                binding.rvAds.visibility = View.VISIBLE

                for (document in result) {
                    ads = Raw(
                        image = document.data["image"].toString(),
                        name = document.data["name"].toString(),
                        type = document.data["type"].toString(),
                        query = document.data["query"].toString(),
                        branch_code = document.data["branch_code"].toString(),
                        position = document.data["position"].toString(),
                        size = document.data["size"].toString()

                    )
                    adsArrayList.add(ads)
                }

                for (cat in adsArrayList) {
                    if (cat.branch_code == sharedPrefHelper[Constant.BRANCH_CODE, ""]) {
                        filteredCatList.add(cat)
                    } else if (cat.branch_code == Constant.DEFAULT_BRANCH) {
                        filteredCatList.add(cat)
                    }
                }

                if (filteredCatList.isEmpty()) {
                    binding.ivDefaultImage.visibility = View.VISIBLE
                } else {
                    binding.ivDefaultImage.visibility = View.INVISIBLE
                    filteredCatList.let {
                        viewPagerAdapter =
                            ImageSlideAdapter(this, this@CloneHomeFragment, filteredCatList)
                        binding.viewpager.adapter = viewPagerAdapter
                        binding.viewpager.autoScroll(3000)
                        binding.indicator.setViewPager(binding.viewpager)
                    }
                }
            }.addOnFailureListener { exception ->

                binding.rvAds.visibility = View.INVISIBLE
                Toast.makeText(this@CloneHomeFragment, "Error Loading $exception", Toast.LENGTH_SHORT)
                    .show()
            }


    }

    //tung
    private fun callDb() {
        initProgressDialog().show()
        val db = Firebase.firestore
        var ads: Raw
        val adsArrayList: ArrayList<Raw> = arrayListOf()
        val filteredCatList: ArrayList<Raw> = arrayListOf()
        db.collection("raw")
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                binding.rvAds.visibility = View.VISIBLE
                progressDialog.dismiss()
                for (document in result) {
                    ads = Raw(
                        image = document.data["image"].toString(),
                        name = document.data["name"].toString(),
                        type = document.data["type"].toString(),
                        query = document.data["query"].toString(),
                        branch_code = document.data["branch_code"].toString(),
                        position = document.data["position"].toString(),
                        size = document.data["size"].toString()

                    )
                    adsArrayList.add(ads)
                }
                for (cat in adsArrayList) {
                    if (cat.branch_code == sharedPrefHelper[Constant.BRANCH_CODE, ""]) {
                        filteredCatList.add(cat)
                    } else if (cat.branch_code == Constant.DEFAULT_BRANCH) {
                        filteredCatList.add(cat)
                    }
                }

                initAds(filteredCatList)
            }.addOnFailureListener { exception ->
                progressDialog.dismiss()
                binding.rvAds.visibility = View.INVISIBLE
                Toast.makeText(this@CloneHomeFragment, "Error Loading $exception", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun initAds(adsArrayList: ArrayList<Raw>) {
        binding.rvAds.layoutManager =
            GridLayoutManager(this@CloneHomeFragment, 3)
        binding.rvAds.isNestedScrollingEnabled = false
        val adapter =
         AdsAdapter(adsArrayList,this@CloneHomeFragment, this@CloneHomeFragment)
        binding.rvAds.adapter = adapter

    }


    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            mainViewModel.getCartSize()
            mainViewModel.allCategory()
        }
    }


    fun getItemClicked(productItem: DesiDataResponseSubListItem) {
        val intent = Intent(this@CloneHomeFragment, ProductActivity::class.java)
        intent.putExtra(
            Constant.IMAGE_URL,
            if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"
        )
        intent.putExtra(
            Constant.PRODUCT_NAME, productItem.sku_name
        )
        intent.putExtra(Constant.ID, productItem.sku.toInt())
        Log.d(TAG, "getItemClicked: ${productItem.sku}")
        intent.putExtra(Constant.PRODUCT_PRICE, productItem.variant_sale_price.toString())
        intent.putExtra(Constant.PRODUCT_SERVER_QTY, productItem.product_quantity.toString())
        intent.putExtra(Constant.PRODUCT_PUBLISHED, productItem.Published)
        intent.putExtra(Constant.MRP_PRICE, productItem.variant_mrp.toString())
        intent.putExtra(Constant.DESCRIPTION, productItem.sku_description)
        startActivity(intent)
    }


    @OptIn(DelicateCoroutinesApi::class)
    suspend fun addToCart(productItem: DesiDataResponseSubListItem, quantity: Int) {

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
                this@CloneHomeFragment,
                "${productItem.sku_name} ADDED TO CART",
                Toast.LENGTH_SHORT
            ).show()
            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.getCartSize()
            }

        } else {
            Toast.makeText(
                this@CloneHomeFragment,
                "This product is already added please check the cart.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getCategoryClicked(categoryItem: String) {
        TODO("Not yet implemented")
    }

    override fun getCategoryClicked2(categoryItem: String, tvLogo: TextView) {
        val intent = Intent(this@CloneHomeFragment, CategoryActivity::class.java)
        intent.putExtra(
            Constant.CATEGORY_NAME, categoryItem
        )

        startActivity(intent)
    }

    @OptIn(DelicateCoroutinesApi::class)
   override fun getAdsClicked(ads: Raw) {

        when (ads.type) {
            Constant.ROUTE_PRODUCT -> {
                GlobalScope.launch {
                    val productItem: DesiDataResponseSubListItem =
                        mainViewModel.getIdBasedProduct(ads.query)
                    val intent = Intent(this@CloneHomeFragment, ProductActivity::class.java)
                    intent.putExtra(
                        Constant.IMAGE_URL,
                        if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"
                    )
                    intent.putExtra(
                        Constant.PRODUCT_NAME, productItem.sku_name
                    )
                    intent.putExtra(Constant.ID, productItem.sku.toInt())
                    Log.d(TAG, "getItemClicked: ${productItem.sku}")
                    intent.putExtra(
                        Constant.PRODUCT_PRICE,
                        productItem.variant_sale_price.toString()
                    )
                    intent.putExtra(Constant.PRODUCT_PUBLISHED, productItem.Published)
                    intent.putExtra(
                        Constant.PRODUCT_SERVER_QTY,
                        productItem.product_quantity.toString()
                    )
                    intent.putExtra(Constant.MRP_PRICE, productItem.variant_mrp.toString())
                    intent.putExtra(Constant.DESCRIPTION, productItem.sku_description)
                    startActivity(intent)

                }
            }

            Constant.ROUTE_PRODUCT_LIST -> {
                val intent = Intent(this@CloneHomeFragment, CategoryBasedProductsActivity::class.java)

                intent.putExtra(
                    Constant.CATEGORY_NAME, ads.name
                )
                intent.putExtra(
                    Constant.QUERY, ads.query
                )


                startActivity(intent)

            }

            Constant.ROUTE_CART -> {
                val intent = Intent(this@CloneHomeFragment, CartActivity::class.java)
                startActivity(intent)
            }

            Constant.ROUTE_MY_DETAILS -> {
                if (!sharedPrefHelper[Constant.VERIFIED_NUM, false]) {
                    val i = Intent(this@CloneHomeFragment, EnterNumberActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                    val i = Intent(this@CloneHomeFragment, MapsActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                    startActivity(i)
                } else if (!sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
                    val i = Intent(this@CloneHomeFragment, DetailsVerificationActivity::class.java)
                    i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                    i.putExtra(Constant.DETAILS, true)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else {
                    val i = Intent(this@CloneHomeFragment, MyProfileActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }
            }

            Constant.ROUTE_DELIVERY_ADDRESS -> {
                val intent = Intent(this@CloneHomeFragment, DeliveryAddressActivity::class.java)
                startActivity(intent)

            }

            Constant.ROUTE_CATEGORY -> {
                val intent = Intent(this@CloneHomeFragment, CategoryActivity::class.java)

                intent.putExtra(
                    Constant.CATEGORY_NAME, "A"
                )

                startActivity(intent)
            }

            Constant.ROUTE_SEARCH -> {
                val intent = Intent(this@CloneHomeFragment, SearchActivity::class.java)
                intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
                startActivity(intent)

            }
        }

    }
}

