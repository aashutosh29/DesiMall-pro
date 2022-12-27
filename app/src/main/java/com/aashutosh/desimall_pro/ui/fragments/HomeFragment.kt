package com.aashutosh.desimall_pro.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CategoryAdapter
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.adapter.ProductPagingAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.category.CategoryResponse
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.barCodeActivity.BarCodeActivity
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
import com.aashutosh.desimall_pro.ui.onBoarding.OnboardFinishActivity
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.bumptech.glide.Glide
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.relex.circleindicator.CircleIndicator


class HomeFragment : Fragment() {
    private lateinit var mainViewModel: StoreViewModel
    private lateinit var tvViewAll: TextView
    lateinit var recyclerView: RecyclerView
    lateinit var pagingAdapter: ProductPagingAdapter
    lateinit var nsvMain: ScrollView

    lateinit var tvCartNo: TextView
    lateinit var clCart: ConstraintLayout

    lateinit var clBarcode: ConstraintLayout

    lateinit var viewPagerAdapter: ImageSlideAdapter
    lateinit var indicator: CircleIndicator

    lateinit var clSearch: ConstraintLayout
    lateinit var ivFilter: ImageView

    private lateinit var sharedPrefHelper: SharedPrefHelper


    lateinit var viewpager: ViewPager
    lateinit var etSearch: EditText

    @VisibleForTesting
    internal lateinit var items: MutableList<Any>

    @VisibleForTesting
    internal lateinit var adapter: MultiTypeAdapter


    lateinit var ivDefaultImage: ImageView

    lateinit var categoryList: CategoryResponse

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    //3
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainViewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        mainViewModel.getBannerList()

        GlobalScope.launch(Dispatchers.Main) {
            val catL = mainViewModel.allCategory()
            if (catL.isEmpty()) {
                mainViewModel.getAllDesiProduct()
            }
            val catF: ArrayList<String> = ArrayList()
            for (cat in catL) {
                catF.add(cat.name)
            }
            initRecyclerViewForCategory(catF)
            mainViewModel.getCartSize()
            val id: String = sharedPrefHelper[Constant.BRANCH_CODE]
            mainViewModel.getDesiProduct(id.toInt(), false)
            Log.d(TAG, "onCreateView: $id")

        }
        return inflater.inflate(R.layout.home_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivProfile = requireView().findViewById<ImageView>(R.id.ivProfile)
        val tvLogin = requireView().findViewById<TextView>(R.id.tvLogin)
        val tvCreateAccount = requireView().findViewById<TextView>(R.id.tvCreateAccount)
        ivDefaultImage = requireView().findViewById(R.id.ivDefaultImage)
        viewpager = requireView().findViewById(R.id.viewpager)
        indicator = requireView().findViewById<CircleIndicator>(R.id.indicator)
        clCart = requireView().findViewById(R.id.clCart)
        etSearch = requireView().findViewById(R.id.etSearch)
        tvCartNo = requireView().findViewById(R.id.tvCartNo)
        clBarcode = requireView().findViewById(R.id.clBarCode)
        nsvMain = requireView().findViewById(R.id.nsvMain)
        ivFilter = requireView().findViewById(R.id.ivFilter)
        recyclerView = requireView().findViewById(R.id.list)
        clSearch = requireView().findViewById(R.id.clSearch)


        /*checking the shared pref working or not*/

        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)

        etSearch.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
            startActivity(intent)

        })

        clBarcode.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, BarCodeActivity::class.java)
            startActivity(intent)
        })
        ivFilter.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, false)
            startActivity(intent)

        })

        if (sharedPrefHelper[Constant.LOGIN, false]) {
            Glide.with(requireContext())
                .load(sharedPrefHelper[Constant.PHOTO, ""])
                .placeholder(R.drawable.app_icon)
                .into(ivProfile)
            tvLogin.text = sharedPrefHelper[Constant.NAME, ""]
            tvLogin.textSize = 14.0F
            tvCreateAccount.visibility = View.GONE

            ivProfile.setOnClickListener(View.OnClickListener {
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra(Constant.IS_PROFILE, true)
                startActivity(intent)
            })
        } else {
            ivProfile.setOnClickListener(View.OnClickListener {
                startActivity(Intent(requireActivity(), OnboardFinishActivity::class.java))
            })
        }
        recyclerView = requireView().findViewById(R.id.list)


        /*ends at here*/
        tvViewAll = requireView().findViewById(R.id.tvViewAll)

        val clNoti: ConstraintLayout = requireView().findViewById(R.id.clNoti)
        clNoti.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        })
        tvViewAll.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_VIEW_ALL, true)
            startActivity(intent)
        })
        clCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            startActivity(intent)
        })
        mainViewModel.categoryItem.observe(viewLifecycleOwner, Observer {
            it?.let { it1 -> initRecyclerViewForCategory(it) }
            Log.d(TAG, "fetchDataFromServer: ${it.toString()}");

        })


        initSlider()
        mainViewModel.cartSize.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                tvCartNo.visibility = View.INVISIBLE
            } else {
                tvCartNo.visibility = View.VISIBLE
                tvCartNo.text = it.toString()
            }
        })

        mainViewModel.desiPagingProduct(1)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                pagingAdapter.submitData(lifecycle, it)
            })
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        pagingAdapter = ProductPagingAdapter(
            requireContext(),
            this@HomeFragment
        )
        recyclerView.adapter = pagingAdapter
    }


    private fun initSlider() {
        mainViewModel.bannerList.observe(viewLifecycleOwner, Observer {
            if (it.isEmpty()) {
                ivDefaultImage.visibility = View.VISIBLE
            } else {
                ivDefaultImage.visibility = View.INVISIBLE
                it.let {
                    viewPagerAdapter =
                        ImageSlideAdapter(requireContext(), it as ArrayList<String>)
                    viewpager.adapter = viewPagerAdapter
                    viewpager.autoScroll(3000)
                    indicator.setViewPager(viewpager)
                }
            }
        })

    }

    private fun ViewPager.autoScroll(interval: Long) {

        val handler = Handler()
        var scrollPosition = 0

        val runnable = object : Runnable {

            override fun run() {

                /**
                 * Calculate "scroll position" with
                 * adapter pages count and current
                 * value of scrollPosition.
                 */
                val count = adapter?.count ?: 0
                setCurrentItem(scrollPosition++ % count, true)

                handler.postDelayed(this, interval)
            }
        }

        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                // Updating "scroll position" when user scrolls manually
                scrollPosition = position + 1
            }

            override fun onPageScrollStateChanged(state: Int) {
                // Not necessary
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // Not necessary
            }
        })

        handler.post(runnable)
    }


    private fun initRecyclerViewForCategory(categoryResponse: List<String>) {
        val recyclerview = view?.findViewById<RecyclerView>(R.id.rvCategory)
        // this creates a vertical layout Manager
        recyclerview?.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val adapter = context?.let { CategoryAdapter(categoryResponse, it, this@HomeFragment) }
        // Setting the Adapter with the recyclerview
        recyclerview?.adapter = adapter
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
    suspend fun addToCart(productItem: DesiDataResponseSubListItem) {

        if (mainViewModel.insertToCart(
                CartProduct(
                    productItem.sku.toInt(),
                    productItem.sku_name,
                    (if (productItem.sku == "") " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"),
                    productItem.sku_description,
                    1,
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
                "${productItem.sku_name} ALREADY ADDED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun getCategoryClicked(categoryItem: String) {
        val intent = Intent(context, CategoryBasedProductsActivity::class.java)

        intent.putExtra(
            Constant.CATEGORY_NAME, categoryItem
        )

        startActivity(intent)

    }
}