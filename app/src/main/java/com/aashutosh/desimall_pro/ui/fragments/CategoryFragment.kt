package com.aashutosh.desimall_pro.ui.fragments

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.adapter.ProductCategoryAdapter
import com.aashutosh.desimall_pro.repository.ProductRepository
import com.aashutosh.desimall_pro.ui.CategoryView
import com.aashutosh.desimall_pro.ui.categoryWithItsProduct.CategoryBasedProductsActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class CategoryFragment : Fragment(), CategoryView {
    lateinit var mainViewModel: StoreViewModel
    lateinit var rvMain: RecyclerView
    lateinit var etSearch: EditText
    lateinit var ivClear: ImageView
    lateinit var tvEmpty: TextView
    val allCategoryList: ArrayList<String> = ArrayList<String>()

    //2
    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    //3
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fetchDataFromServer()
        return inflater.inflate(R.layout.category_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvMain = requireView().findViewById(R.id.rvMain)
        etSearch = requireView().findViewById(R.id.etSearch)
        ivClear = requireView().findViewById(R.id.ivClear)
        tvEmpty = requireView().findViewById(R.id.tvEmpty)
    }

    private fun initCategoryRecyclerView(categoryResponse: List<String>) {

        // this creates a vertical layout Manager
        rvMain.layoutManager = LinearLayoutManager(context)
        val adapter =
            context?.let { ProductCategoryAdapter(categoryResponse, it, this@CategoryFragment) }
        // Setting the Adapter with the rvMain
        rvMain.adapter = adapter
    }

    override fun getCategoryClicked(categoryItem: String) {
        val intent = Intent(requireContext(), CategoryBasedProductsActivity::class.java)

        intent.putExtra(
            Constant.CATEGORY_NAME, categoryItem
        )
        intent.putExtra(
            Constant.QUERY_KEY, "subcategory_name"
        )
        intent.putExtra(
            Constant.QUERY_VALUE, categoryItem
        )
        startActivity(intent)

    }

    override fun getCategoryClicked2(categoryItem: String, tvLogo: TextView) {
        TODO("Not yet implemented")
    }


    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchDataFromServer() {
        mainViewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        mainViewModel.categoryItem.observe(viewLifecycleOwner, Observer {
            it?.let { initCategoryRecyclerView(it) }
            initSearch()
            Log.d(ContentValues.TAG, "fetchDataFromServer: ${it.toString()}");

        })
        GlobalScope.launch(Dispatchers.Main) {
            val catL = mainViewModel.allCategory()
            val catF: ArrayList<String> = ArrayList()
            for (cat in catL) {
                catF.add(cat.name)
            }
            initCategoryRecyclerView(catF)
        }
    }


    private fun initSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (etSearch.text.toString().isEmpty()) {
                    val appListAdapter =
                        ProductCategoryAdapter(
                            allCategoryList,
                            requireContext(),
                            this@CategoryFragment
                        )
                    val linearLayoutManager = LinearLayoutManager(requireContext())
                    rvMain.adapter = appListAdapter
                    rvMain.layoutManager = linearLayoutManager
                } else {
                    val searchedAppList: ArrayList<String> =
                        ArrayList<String>()
                    ivClear.visibility = View.VISIBLE
                    for (di in allCategoryList) {
                        if (di.lowercase().contains(
                                etSearch.getText().toString().lowercase(
                                    Locale.getDefault()
                                )
                            )
                        ) {
                            searchedAppList.add(di)
                        }
                    }
                    if (searchedAppList.size == 0) {
                        tvEmpty.visibility = View.VISIBLE
                        rvMain.visibility = View.INVISIBLE
                    } else {
                        tvEmpty.visibility = View.GONE
                        rvMain.visibility = View.VISIBLE
                        val appListAdapter = context?.let {
                            ProductCategoryAdapter(
                                searchedAppList,
                                requireContext(),
                                this@CategoryFragment
                            )
                        }
                        val linearLayoutManager = LinearLayoutManager(requireContext())
                        rvMain.adapter = appListAdapter
                        rvMain.layoutManager = linearLayoutManager
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        ivClear.setOnClickListener(View.OnClickListener {
            ivClear.visibility = View.GONE
            etSearch.setText("")
            etSearch.clearFocus()
        })
    }


}

