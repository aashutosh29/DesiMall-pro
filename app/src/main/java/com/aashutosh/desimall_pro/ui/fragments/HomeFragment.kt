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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.AdsAdapter
import com.aashutosh.desimall_pro.adapter.CategoryAdapter
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.HomeFragmentBinding
import com.aashutosh.desimall_pro.models.Ads
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.barCodeActivity.BarCodeActivity
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.categoryActivity.CategoryActivity
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
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
                .into(binding.ivProfile);
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
            } else if (!sharedPrefHelper[Constant.DETAIlS_VERIFED, false]) {
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
        mainViewModel.bannerList.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                binding.ivDefaultImage.visibility = View.VISIBLE
            } else {
                binding.ivDefaultImage.visibility = View.INVISIBLE
                it.let {
                    viewPagerAdapter =
                        ImageSlideAdapter(requireContext(), it as ArrayList<String>)
                    binding.viewpager.adapter = viewPagerAdapter
                    binding.viewpager.autoScroll(3000)
                    binding.indicator.setViewPager(binding.viewpager)
                }
            }
        })

    }


    private fun initRecyclerViewForCategory(categoryResponse: List<String>) {
        val recyclerview = view?.findViewById<RecyclerView>(R.id.rvCategory)
        // this creates a vertical layout Manager
        recyclerview?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter =
            context?.let { CategoryAdapter(categoryResponse, this@HomeFragment, "nothing") }
        // Setting the Adapter with the recyclerview
        recyclerview?.adapter = adapter
    }

    private fun callDb() {
        val db = Firebase.firestore
        var ads: Ads
        val adsArrayList: ArrayList<Ads> = arrayListOf()
        val filteredCatList: ArrayList<Ads> = arrayListOf()
        db.collection("ads")
            .get()
            .addOnSuccessListener { result ->
                binding.rvAds.visibility = View.VISIBLE
                for (document in result) {
                    ads = Ads(
                        id = document.id,
                        image = document.data["image"].toString(),
                        name = document.data["name"].toString(),
                        type = document.data["type"].toString(),
                        query_key = document.data["query_key"].toString(),
                        query_value = document.data["query_value"].toString(),
                        category_id = document.data["cat"].toString()
                    )
                    adsArrayList.add(ads)
                }
                for (cat in adsArrayList) {
                    if (cat.category_id == sharedPrefHelper[Constant.BRANCH_CODE, ""]) {
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

    private fun initAds(adsArrayList: ArrayList<Ads>) {
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
    fun getAdsClicked(ads: Ads) {
        GlobalScope.launch {
            if (ads.type == Constant.PRODUCT) {
                val productItem: DesiDataResponseSubListItem =
                    mainViewModel.getIdBasedProduct(ads.query_value)
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

            } else if (ads.type == Constant.CATEGORY) {
                val intent = Intent(requireContext(), CategoryBasedProductsActivity::class.java)

                intent.putExtra(
                    Constant.CATEGORY_NAME, ads.name
                )
                intent.putExtra(
                    Constant.QUERY_KEY, ads.query_key
                )
                intent.putExtra(
                    Constant.QUERY_VALUE, ads.query_value
                )

                startActivity(intent)

            }
        }
    }

}