package com.aashutosh.simplestore.ui.bottomSheet

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.adapter.SmallCategoryAdapter
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.models.category.CategoryResponse
import com.aashutosh.simplestore.models.category.CategoryResponseItem
import com.aashutosh.simplestore.utils.Constant
import com.aashutosh.simplestore.viewModels.StoreViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider


class ShortBottomSheet : BottomSheetDialogFragment() {

    lateinit var ivClear: ImageView
    lateinit var spShortBy: Spinner
    lateinit var rsPriceRange: RangeSlider
    lateinit var rvCategory: RecyclerView
    private lateinit var sharedPrefHelper: SharedPrefHelper


    private lateinit var mainViewModel: StoreViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    companion object {
        fun newInstance(): ShortBottomSheet {
            return ShortBottomSheet()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadData()
        return inflater.inflate(R.layout.fragment_short_bottom_sheet, container, false)
    }

    fun loadData() {
        mainViewModel = ViewModelProvider(requireActivity())[StoreViewModel::class.java]
        mainViewModel.category.observe(viewLifecycleOwner, Observer { it ->
            it.data.also {
                if (it != null) {
                    initRecyclerViewForCategory(it)
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ivClear = requireView().findViewById(R.id.ivClear)
        spShortBy = requireView().findViewById(R.id.spShortBy)
        rsPriceRange = requireView().findViewById(R.id.rsPriceRange)
        rvCategory = requireView().findViewById(R.id.rvCategory)

        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)



        ivClear.setOnClickListener(View.OnClickListener {
            dismiss()
        })

        //If you only want the slider start and end value and don't care about the previous values
        rsPriceRange.addOnChangeListener { slider, value, fromUser ->
            val test = rsPriceRange.values
            sharedPrefHelper[Constant.MIN_PRICE] = test[0]
            sharedPrefHelper[Constant.MAX_PRICE] = test[1]
            Log.d(TAG, "onViewCreated: " + "Start value: ${test[0]}, End value: ${test[1]}")

        }

        val values: Array<String> = arrayOf(
            "Price-- low to high",
            "Price-- high to low",
            "Date-- low to High",
            "Date-- low to high"
        )
        spShortBy.adapter =
            ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, values)
        spShortBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                val item = parent.getItemAtPosition(position)
                when (position) {
                    0 -> {
                        sharedPrefHelper[Constant.ORDER_BY, "price"]
                        sharedPrefHelper[Constant.ASC_OR_DSC, "asc"]
                    }
                    1 -> {
                        sharedPrefHelper[Constant.ORDER_BY, "price"]
                        sharedPrefHelper[Constant.ASC_OR_DSC, "desc"]

                    }
                    2 -> {
                        sharedPrefHelper[Constant.ORDER_BY, "date"]
                        sharedPrefHelper[Constant.ASC_OR_DSC, "desc"]

                    }
                    3 -> {
                        sharedPrefHelper[Constant.ORDER_BY, "date"]
                        sharedPrefHelper[Constant.ASC_OR_DSC, "desc"]

                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initRecyclerViewForCategory(categoryResponse: CategoryResponse) {
        val recyclerview = view?.findViewById<RecyclerView>(R.id.rvCategory)
        // this creates a vertical layout Manager
        recyclerview?.layoutManager =
            GridLayoutManager(activity, 3, GridLayoutManager.HORIZONTAL, false)

        val adapter =
            context?.let { SmallCategoryAdapter(categoryResponse, it, this@ShortBottomSheet) }
        // Setting the Adapter with the recyclerview
        recyclerview?.adapter = adapter
    }

    fun getCategoryClicked(categoryItem: CategoryResponseItem, clMain: ConstraintLayout) {
        if (sharedPrefHelper.get(Constant.CATEGORY_ID, 0) == categoryItem.id) {
            clMain.setBackgroundResource(R.drawable.corner_radius_border_stroke)
            sharedPrefHelper[Constant.CATEGORY_ID, 0]
        } else {
            sharedPrefHelper.set(Constant.CATEGORY_ID, categoryItem.id)
            clMain.setBackgroundResource(R.drawable.corner_radus_border_stroke_solid_color)

        }
    }


}