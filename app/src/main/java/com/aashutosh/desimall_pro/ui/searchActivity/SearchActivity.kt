package com.aashutosh.desimall_pro.ui.searchActivity

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.SearchProductAdapter
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.models.desimallApi.DesiDataResponseSubListItem
import com.aashutosh.desimall_pro.ui.bottomSheet.ShortBottomSheet
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var mainViewModel: StoreViewModel
    lateinit var pagingAdapter: SearchProductAdapter
    lateinit var viewpager: ViewPager
    private lateinit var tvMain: TextView
    lateinit var ivBack: ImageView
    private lateinit var etSearch: EditText
    lateinit var ivClear: ImageView
    lateinit var rvMain: RecyclerView
    lateinit var ivFilter: ImageView
    lateinit var progressDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initView()
        intRecyclerView()

    }


    override fun onStart() {
        super.onStart()
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        etSearch.requestFocus()
        initSearch()
    }

    fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun initView() {
        rvMain = findViewById(R.id.rvMain)
        tvMain = findViewById(R.id.tvMain)
        etSearch = findViewById(R.id.etSearch)
        ivClear = findViewById(R.id.ivClear)
        ivBack = findViewById(R.id.ivBack)
        ivFilter = findViewById(R.id.ivFilter)

        ivFilter.setOnClickListener(View.OnClickListener {
            ShortBottomSheet().show(supportFragmentManager, "short")
            Toast.makeText(this, "filter clicked", Toast.LENGTH_SHORT).show()
        })

        ivBack.setOnClickListener(View.OnClickListener {
            this.finish()
        })
        etSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        hideKeyboard()
    }


    private fun intRecyclerView() {

        rvMain.layoutManager = GridLayoutManager(this, 2)
        pagingAdapter = SearchProductAdapter(
            this@SearchActivity,
            this@SearchActivity
        )
        rvMain.adapter = pagingAdapter
    }

    private fun initProgressDialog(): AlertDialog {
        progressDialog = setProgressDialog(this, "Searching product..")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
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

    private fun initSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etSearch.text.toString().isEmpty()) {
                    tvMain.visibility = View.INVISIBLE

                } else {
                    tvMain.visibility = View.INVISIBLE
                    ivClear.visibility = View.VISIBLE
                    //api call here
                    mainViewModel.getDesiSearch(etSearch.text.toString())
                        .observe(this@SearchActivity, androidx.lifecycle.Observer {
                            pagingAdapter.submitData(lifecycle, it)

                        })

                }

            }

            override fun afterTextChanged(s: Editable) {}
        })
        ivClear.setOnClickListener(View.OnClickListener {
            ivClear.visibility = View.GONE
            etSearch.setText("")
            etSearch.clearFocus()
            mainViewModel.getDesiSearch("")
                .observe(this@SearchActivity, androidx.lifecycle.Observer {
                    pagingAdapter.submitData(lifecycle, it)

                })
        })
        hideKeyboard()
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }


    fun getItemClicked(productItem: DesiDataResponseSubListItem) {
        val intent = Intent(this@SearchActivity, ProductActivity::class.java)
        intent.putExtra(
            Constant.IMAGE_URL,
            if (productItem.sku == null) " " else "https://livedesimall.in/ldmimages/" + productItem.sku + ".png"
        )
        intent.putExtra(
            Constant.PRODUCT_NAME, productItem.sku_name
        )
        intent.putExtra(Constant.ID, productItem.sku.toInt())
        Log.d(ContentValues.TAG, "getItemClicked: ${productItem.sku}")
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
                this@SearchActivity,
                "${productItem.sku_name} ADDED TO CART",
                Toast.LENGTH_SHORT
            ).show()
            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.getCartSize()
            }

        } else {
            Toast.makeText(
                this@SearchActivity,
                "${productItem.sku_name} ALREADY ADDED",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


}