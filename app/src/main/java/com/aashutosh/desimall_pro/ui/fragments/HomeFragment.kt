package com.aashutosh.desimall_pro.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
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
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
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
    lateinit var binding: HomeFragmentBinding
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var viewPagerAdapter: ImageSlideAdapter

    @VisibleForTesting
    internal lateinit var items: MutableList<Any>

    @VisibleForTesting
    internal lateinit var adapter: MultiTypeAdapter

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)
        mainViewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        mainViewModel.getBannerList()
        callDb()

        return binding.root
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
        binding.ivFilter.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, false)
            startActivity(intent)

        })
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

        binding.llProfile.setOnClickListener(View.OnClickListener {
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

        })
    }


    private fun initSlider() {

        val db = Firebase.firestore
        var ads: Raw
        val adsArrayList: ArrayList<Raw> = arrayListOf()
        val filteredCatList: ArrayList<Raw> = arrayListOf()
        db.collection("slider")
            .orderBy("position", Query.Direction.DESCENDING)
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

    private fun callDb() {
        val db = Firebase.firestore
        var ads: Raw
        val adsArrayList: ArrayList<Raw> = arrayListOf()
        val filteredCatList: ArrayList<Raw> = arrayListOf()
        db.collection("raw")
            .orderBy("position", Query.Direction.DESCENDING)
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

                initAds(filteredCatList)
            }.addOnFailureListener { exception ->
                binding.rvAds.visibility = View.INVISIBLE
                Toast.makeText(requireContext(), "Error Loading $exception", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    private fun initAds(adsArrayList: ArrayList<Raw>) {
        binding.rvAds.layoutManager =
            GridLayoutManager(context, 2)
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
    fun getAdsClicked(ads: Raw) {

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