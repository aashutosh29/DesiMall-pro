package com.aashutosh.simplestore.ui.deliveryAddress

import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.models.DeliveryDetails
import com.aashutosh.simplestore.utils.Constant
import com.aashutosh.simplestore.viewModels.StoreViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DeliveryAddressActivity : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etArea: EditText
    lateinit var etLandMark: EditText
    lateinit var etZipCode: EditText
    lateinit var btSave: Button
    lateinit var mainViewModel: StoreViewModel
    lateinit var ivBack: ImageView
    private lateinit var sharedPrefHelper: SharedPrefHelper
    var id: Int = 0


    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_delivery_address)
        bindView()
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(this.applicationContext)
        mainViewModel = ViewModelProvider(this)[StoreViewModel::class.java]
        GlobalScope.launch {
            val deliveryDetails: List<DeliveryDetails> = mainViewModel.getProfileDetails()
            if (deliveryDetails.isNotEmpty()) {
                etArea.text = deliveryDetails[0].address.toEditable()
                etMobileNumber.text = deliveryDetails[0].mobileNum.toEditable()
                etName.text = deliveryDetails[0].name.toEditable()
                etZipCode.text = deliveryDetails[0].zipCode.toEditable()
                etLandMark.text = deliveryDetails[0].landMark.toEditable()
                id = deliveryDetails[0].id
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun bindView() {
        etName = findViewById(R.id.etName)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etArea = findViewById(R.id.etArea)
        etLandMark = findViewById(R.id.etLandMark)
        etZipCode = findViewById(R.id.etZipCode)
        btSave = findViewById(R.id.btSave)
        ivBack = findViewById(R.id.ivBack)

        btSave.setOnClickListener(View.OnClickListener {
            if (etName.text.toString() == "" || !etName.text.contains(" ")) {
                Toast.makeText(
                    this@DeliveryAddressActivity, "Enter full name", Toast.LENGTH_SHORT
                ).show()
            } else if (etMobileNumber.text.toString() == "" || etArea.text.toString() == "" || etLandMark.text.toString() == "" || etZipCode.text.toString() == "") {
                Toast.makeText(
                    this@DeliveryAddressActivity, "Enter the Full Details", Toast.LENGTH_SHORT
                ).show()
            } else {

                GlobalScope.launch(Dispatchers.Main) {
                    if (!sharedPrefHelper[Constant.CREATE, false]) {
                        sharedPrefHelper[Constant.CREATE] = true
                        if (mainViewModel.createDelivery(
                                DeliveryDetails(
                                    5,
                                    name = etName.text.toString(),
                                    mobileNum = etMobileNumber.text.toString(),
                                    address = etArea.text.toString(),
                                    landMark = etLandMark.text.toString(),
                                    zipCode = etZipCode.text.toString()
                                )
                            ) > 1
                        ) {

                            Toast.makeText(
                                this@DeliveryAddressActivity,
                                "Successfully Created",
                                Toast.LENGTH_SHORT
                            ).show()
                            getFinish()


                        }
                    } else {
                        if (mainViewModel.updateDelivery(
                                DeliveryDetails(
                                    5,
                                    name = etName.text.toString(),
                                    mobileNum = etMobileNumber.text.toString(),
                                    address = etArea.text.toString(),
                                    landMark = etLandMark.text.toString(),
                                    zipCode = etZipCode.text.toString()
                                )
                            ) > 1
                        ) {
                            Toast.makeText(
                                this@DeliveryAddressActivity,
                                "Successfully Updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            getFinish()
                        }
                    }
                }
            }
        })
        ivBack.setOnClickListener(View.OnClickListener {
            this.finish()
        })
    }

    private fun getFinish() {
        this.finish()
    }

    private fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

}