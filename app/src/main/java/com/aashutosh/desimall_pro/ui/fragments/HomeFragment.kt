package com.aashutosh.desimall_pro.ui.fragments

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.AdsAdapter
import com.aashutosh.desimall_pro.adapter.GridItemAdapter
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.HomeFragmentNewBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.GridItem
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), CategoryView {
    private lateinit var mainViewModel: StoreViewModel
    lateinit var binding: HomeFragmentNewBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var viewPagerAdapter: ImageSlideAdapter

    @VisibleForTesting
    internal lateinit var items: MutableList<Any>

    @VisibleForTesting
    internal lateinit var adapter: MultiTypeAdapter

    private var homeView: HomeView? = null

    lateinit var signatureHashHelper: AppSignatureHashHelper
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeView) {
            homeView = context
        } else {
            throw RuntimeException("$context must implement OnIconClickListener")
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentNewBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)
        mainViewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        mainViewModel.getBannerList()
        callDb()
        signatureHashHelper = AppSignatureHashHelper(requireContext())
        Log.d(TAG, "onCreateView Signature: " + signatureHashHelper.appSignatures)


        if (!areNotificationsEnabled(requireContext())) {
            showEnableNotificationDialog()
        }

        val gridItems = listOf(
            GridItem(R.drawable.negative_home, "In House"),
            GridItem(R.drawable.baseline_stacked_bar_chart_24, "All Products"),
            GridItem(R.drawable.baseline_category_24, "All categories"),
            GridItem(R.drawable.baseline_discount_24, "Discounts")
        )

        val gridItemAdapter =
            GridItemAdapter(gridItems, object : GridItemAdapter.OnItemClickListener {
                override fun onItemClick(item: GridItem) {
                    // Handle item click here based on the GridItem
                    when (item.title) {
                        "In House" -> {
                            val ads = Raw(
                                image = "https://firebasestorage.googleapis.com/v0/b/desi-mall-fc808.appspot.com/o/banner%2Flive%20desi%20mall%20products.png?alt=media&token=d7cc3aaa-a3fb-4477-9777-4e57109d52f4",
                                name = "LIVE DESI MALL",
                                type = "category",
                                query = "SELECT * FROM product WHERE company_name = 'LIVE DESI MALL'",
                                branch_code = "all",
                                position = "",
                                size = ""

                            )
                            getAdsClicked(ads)
                        }

                        "All Products" -> {
                            homeView?.onIconClicked(Constant.HOME)
                        }

                        "All categories" -> {
                            val ads = Raw(
                                image = "https://firebasestorage.googleapis.com/v0/b/desi-mall-fc808.appspot.com/o/categories.jpg?alt=media&token=7bb3ae91-219e-4708-9eff-d4dd1290a707",
                                name = "Category",
                                type = "category_page",
                                query = "",
                                branch_code = "all",
                                position = "7",
                                size = ""

                            )
                            getAdsClicked(ads)
                        }

                        "Discounts" -> {
                            // Handle Discounts click
                        }
                    }
                }
            })

        binding.fRecyclerView.adapter = gridItemAdapter


        val gridItems2 = listOf(
            GridItem(R.drawable.baseline_account_circle_24, "View Account"),
            GridItem(R.drawable.baseline_circle_notifications_24, "Notification"),
            GridItem(R.drawable.baseline_work_history_24, "Order History"),
            GridItem(R.drawable.baseline_shopping_cart_24, "Your Cart")
        )
        val gridItemAdapter2 =
            GridItemAdapter(gridItems2, object : GridItemAdapter.OnItemClickListener {
                override fun onItemClick(item: GridItem) {
                    // Handle item click here based on the GridItem
                    when (item.title) {
                        "View Account" -> {
                            homeView?.onIconClicked(Constant.PROFILE)
                        }

                        "Notification" -> {
                            homeView?.onIconClicked(Constant.NOTIFICATION)
                        }

                        "Order History" -> {
                            homeView?.onIconClicked(Constant.ORDER_HISTORY)
                        }

                        "Your Cart" -> {
                            homeView?.onIconClicked(Constant.CART)
                        }
                    }
                }
            })

        binding.sRecyclerView.adapter = gridItemAdapter2


        /* binding.llProfileMini.setOnClickListener(View.OnClickListener {
             homeView?.onIconClicked(Constant.PROFILE)
         })
         binding.llNotificationMini.setOnClickListener(View.OnClickListener {
             homeView?.onIconClicked(Constant.NOTIFICATION)
         })
         binding.llOrderHistoryMini.setOnClickListener(View.OnClickListener {
             homeView?.onIconClicked(Constant.ORDER_HISTORY)
         })

         binding.llCartMini.setOnClickListener(View.OnClickListener {
             homeView?.onIconClicked(Constant.CART)
         })

         binding.llAllProductsMini.setOnClickListener(View.OnClickListener {
             homeView?.onIconClicked(Constant.HOME)
         })
         binding.llLdmProduct.setOnClickListener(View.OnClickListener {

          val  ads = Raw(
                 image = "https://firebasestorage.googleapis.com/v0/b/desi-mall-fc808.appspot.com/o/banner%2Flive%20desi%20mall%20products.png?alt=media&token=d7cc3aaa-a3fb-4477-9777-4e57109d52f4",
                 name = "LIVE DESI MALL",
                 type ="category",
                 query = "SELECT * FROM product WHERE company_name = 'LIVE DESI MALL'",
                 branch_code = "all",
                 position ="",
                 size = ""

             )
              getAdsClicked(ads)
         })

         binding.llCategoryMini.setOnClickListener(View.OnClickListener {
             val   ads = Raw(
                 image = "https://firebasestorage.googleapis.com/v0/b/desi-mall-fc808.appspot.com/o/categories.jpg?alt=media&token=7bb3ae91-219e-4708-9eff-d4dd1290a707",
                 name = "Category",
                 type ="category_page",
                 query = "",
                 branch_code = "all",
                 position ="7",
                 size = ""

             )
             getAdsClicked(ads)
         })*/

        return binding.root
    }

    private fun showEnableNotificationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enable Notifications")
            .setMessage("To receive important updates and notifications, please enable notifications for this app.")
            .setPositiveButton("Enable") { dialog: DialogInterface, _: Int ->
                // Open the notification settings screen
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().packageName)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (sharedPrefHelper[Constant.PHOTO, ""] != "") {
            Glide.with(requireContext())
                .load(sharedPrefHelper[Constant.PHOTO, ""])
                .error(R.drawable.ic_profile)
                .placeholder(R.drawable.ic_profile)
                .into(binding.ivProfile)
        }
        if (sharedPrefHelper[Constant.NAME, ""] != "") {
            binding.tvLogin.text = sharedPrefHelper[Constant.NAME, ""]
        }
        binding.clSearchIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
            startActivity(intent)

        })

        binding.tvBranch.text = sharedPrefHelper[Constant.BRANCH_NAME, ""]
        binding.clBarCode.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, BarCodeActivity::class.java)
            startActivity(intent)
        })
        /*  binding.ivFilter.setOnClickListener(View.OnClickListener {
              val intent = Intent(context, SearchActivity::class.java)
              intent.putExtra(Constant.IS_SEARCH_FOCUS, false)
              startActivity(intent)

          })*/

        binding.clCatIcon.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CategoryActivity::class.java)

            intent.putExtra(
                Constant.CATEGORY_NAME, "A"
            )

            startActivity(intent)
        })
        binding.clNoti.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        })
        binding.clCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            startActivity(intent)
        })
        initSlider()

        /* binding.llProfile.setOnClickListener(View.OnClickListener {
             if (!sharedPrefHelper[Constant.VERIFIED_NUM, false]) {
                 val i = Intent(requireActivity(), EnterNumberActivity::class.java)
                 i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                 startActivity(i)
             } else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                 val i = Intent(requireActivity(), MapsActivity::class.java)
                 i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                 i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                 startActivity(i)
             } else if (!sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
                 val i = Intent(requireActivity(), DetailsVerificationActivity::class.java)
                 i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                 i.putExtra(Constant.DETAILS, true)
                 i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                 startActivity(i)
             } else {
                 val i = Intent(requireActivity(), MyProfileActivity::class.java)
                 i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                 startActivity(i)
             }

         })*/

        binding.llProfile.setOnClickListener {
            View.OnClickListener {
                if (!sharedPrefHelper[Constant.LOGIN_SUCCESS, false]) {
                    val i = Intent(requireContext(), LoginActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else {
                    val i = Intent(requireContext(), MyProfileActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }
            }
        }
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
                // binding.rvAds.visibility = View.VISIBLE
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
                            ImageSlideAdapter(this, requireContext(), filteredCatList)
                        binding.viewpager.adapter = viewPagerAdapter
                        binding.viewpager.autoScroll(3000)
                        binding.indicator.setViewPager(binding.viewpager)
                    }
                }
            }.addOnFailureListener { exception ->
                binding.rvAds.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Error Loading $exception", Toast.LENGTH_SHORT)
                    .show()
            }


    }

    //tung
    private fun callDb() {
        val db = Firebase.firestore
        var ads: Raw
        val adsArrayList: ArrayList<Raw> = arrayListOf()
        val filteredCatList: ArrayList<Raw> = arrayListOf()
        db.collection("raw")
            .orderBy("position", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                // binding.rvAds.visibility = View.VISIBLE
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
                binding.rvAds.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Error Loading $exception", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun initAds(adsArrayList: ArrayList<Raw>) {
        binding.rvAds.layoutManager =
            GridLayoutManager(context, 3)
        binding.rvAds.isNestedScrollingEnabled = false
        val adapter =
            context?.let { AdsAdapter(adsArrayList, requireContext(), this@HomeFragment) }
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
        val intent = Intent(context, ProductActivity::class.java)
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
                requireContext(),
                "${productItem.sku_name} ADDED TO CART",
                Toast.LENGTH_SHORT
            ).show()
            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.getCartSize()
            }

        } else {
            Toast.makeText(
                requireContext(),
                "This product is already added please check the cart.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun getCategoryClicked(categoryItem: String) {
        TODO("Not yet implemented")
    }

    override fun getCategoryClicked2(categoryItem: String, tvLogo: TextView) {
        val intent = Intent(context, CategoryActivity::class.java)
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
                    val intent = Intent(context, ProductActivity::class.java)
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
                val intent = Intent(requireContext(), CategoryBasedProductsActivity::class.java)

                intent.putExtra(
                    Constant.CATEGORY_NAME, ads.name
                )
                intent.putExtra(
                    Constant.QUERY, ads.query
                )


                startActivity(intent)

            }

            Constant.ROUTE_CART -> {
                val intent = Intent(context, CartActivity::class.java)
                startActivity(intent)
            }

            Constant.ROUTE_MY_DETAILS -> {
                if (!sharedPrefHelper[Constant.VERIFIED_NUM, false]) {
                    val i = Intent(requireActivity(), EnterNumberActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                    val i = Intent(requireActivity(), MapsActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                    startActivity(i)
                } else if (!sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
                    val i = Intent(requireActivity(), DetailsVerificationActivity::class.java)
                    i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                    i.putExtra(Constant.DETAILS, true)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                } else {
                    val i = Intent(requireActivity(), MyProfileActivity::class.java)
                    i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(i)
                }
            }

            Constant.ROUTE_DELIVERY_ADDRESS -> {
                val intent = Intent(context, DeliveryAddressActivity::class.java)
                startActivity(intent)

            }

            Constant.ROUTE_CATEGORY -> {
                val intent = Intent(context, CategoryActivity::class.java)

                intent.putExtra(
                    Constant.CATEGORY_NAME, "A"
                )

                startActivity(intent)
            }

            Constant.ROUTE_SEARCH -> {
                val intent = Intent(context, SearchActivity::class.java)
                intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
                startActivity(intent)

            }
        }

    }

}