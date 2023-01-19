package com.aashutosh.desimall_pro.ui.fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import butterknife.ButterKnife
import butterknife.OnClick
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.CategoryAdapter
import com.aashutosh.desimall_pro.adapter.ImageSlideAdapter
import com.aashutosh.desimall_pro.adapter.ProductPagingAdapter
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.HomeFragmentBinding
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.category.CategoryResponse
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.barCodeActivity.BarCodeActivity
import com.aashutosh.desimall_pro.ui.cartActivity.CartActivity
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.ui.searchActivity.SearchActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.drakeet.multitype.MultiTypeAdapter
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var mainViewModel: StoreViewModel
    lateinit var binding: HomeFragmentBinding
    lateinit var pagingAdapter: ProductPagingAdapter
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var viewPagerAdapter: ImageSlideAdapter
    @VisibleForTesting
    internal lateinit var items: MutableList<Any>
    @VisibleForTesting
    internal lateinit var adapter: MultiTypeAdapter
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
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
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
            initRecyclerViewForCategory(alphas())
            // initRecyclerViewForCategory(catF)
            mainViewModel.getCartSize()
            val id: String = sharedPrefHelper[Constant.BRANCH_CODE]
            mainViewModel.getDesiProduct(id.toInt(), false)
            Log.d(TAG, "onCreateView: $id")

        }
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefHelper = SharedPrefHelper
        ButterKnife.bind(this, view)
        sharedPrefHelper.init(requireActivity().applicationContext)
        binding.tvLogin.text = sharedPrefHelper[Constant.BRANCH_NAME, ""]
        binding.etSearch.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, true)
            startActivity(intent)

        })

        binding.clBarCode.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, BarCodeActivity::class.java)
            startActivity(intent)
        })
        binding.ivFilter.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, SearchActivity::class.java)
            intent.putExtra(Constant.IS_SEARCH_FOCUS, false)
            startActivity(intent)

        })


        /*ends at here*/


        val clNoti: ConstraintLayout = requireView().findViewById(R.id.clNoti)
        clNoti.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        })

        binding.clCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            startActivity(intent)
        })
        mainViewModel.categoryItem.observe(viewLifecycleOwner, Observer {
            it?.let { initRecyclerViewForCategory(it) }
            Log.d(TAG, "fetchDataFromServer: $it");

        })


        initSlider()
        mainViewModel.cartSize.observe(viewLifecycleOwner, Observer {
            if (it == 0) {
                binding.tvCartNo.visibility = View.INVISIBLE
            } else {
                binding.tvCartNo.visibility = View.VISIBLE
                binding.tvCartNo.text = it.toString()
            }
        })

        mainViewModel.desiPagingProduct(1)
            .observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                pagingAdapter.submitData(lifecycle, it)
            })
        binding.list.layoutManager = GridLayoutManager(requireContext(), 2)
        pagingAdapter = ProductPagingAdapter(
            requireContext(),
            this@HomeFragment
        )
        binding.list.adapter = pagingAdapter
    }

    @OnClick(R.id.tvViewAll)
    fun tvViewALlClicked() {
        val i = Intent(requireActivity(), HomeActivity::class.java)
        i.putExtra(Constant.IS_VIEW_ALL, true)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        requireActivity().startActivity(i)
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

    fun getCategoryClicked(categoryItem: String) {
        val intent = Intent(context, CategoryBasedProductsActivity::class.java)

        intent.putExtra(
            Constant.CATEGORY_NAME, categoryItem
        )

        startActivity(intent)

    }

    private fun alphas(): List<String> {
        val alphabet: ArrayList<String> = arrayListOf()
        alphabet.add("A")
        alphabet.add("B")
        alphabet.add("C")
        alphabet.add("D")
        alphabet.add("E")
        alphabet.add("F")
        alphabet.add("G")
        alphabet.add("H")
        alphabet.add("I")
        alphabet.add("J")
        alphabet.add("K")
        alphabet.add("L")
        alphabet.add("M")
        alphabet.add("N")
        alphabet.add("O")
        alphabet.add("P")
        alphabet.add("Q")
        alphabet.add("R")
        alphabet.add("S")
        alphabet.add("T")
        alphabet.add("U")
        alphabet.add("V")
        alphabet.add("W")
        alphabet.add("X")
        alphabet.add("Y")
        alphabet.add("Z")
        return alphabet

    }
}