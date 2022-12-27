package com.aashutosh.desimall_pro.ui.barCodeActivity

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.aashutosh.desimall_pro.models.CartProduct
import com.aashutosh.desimall_pro.ui.productScreen.ProductActivity
import com.aashutosh.desimall_pro.utils.Constant
import com.aashutosh.desimall_pro.viewModels.StoreViewModel
import com.budiyev.android.codescanner.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BarCodeActivity : AppCompatActivity() {
    lateinit var mainViewModel: StoreViewModel
    private lateinit var codeScanner: CodeScanner
    lateinit var progressDialog: AlertDialog
    lateinit var cartProduct: CartProduct

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code)
        handlePermissions(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        mainViewModel = ViewModelProvider(this@BarCodeActivity)[StoreViewModel::class.java]

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                GlobalScope.launch(Dispatchers.Main) {
                    initProgressDialog().show()
                    mainViewModel.getBarCodeBasedItem(it.text)
                }
                //   Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                //   Toast.makeText( this, "Camera initialization error: ${it.message}", Toast.LENGTH_LONG ).show()
            }
        }



        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        mainViewModel.productItem.observe(this@BarCodeActivity, Observer {

            val intent = Intent(this@BarCodeActivity, ProductActivity::class.java)
            intent.putExtra(Constant.ID, it?.sku)
            intent.putExtra(
                Constant.IMAGE_URL,
                if (it!!.sku == null) " " else "https://livedesimall.in/ldmimages/" + it.sku + ".png"
            )
            intent.putExtra(
                Constant.PRODUCT_NAME, it.sku_name
            )
            intent.putExtra(Constant.ID, it.sku.toInt())
            Log.d(ContentValues.TAG, "getItemClicked: ${it.sku}")
            intent.putExtra(Constant.PRODUCT_PRICE, it.variant_sale_price.toString())
            intent.putExtra(Constant.MRP_PRICE, it.variant_mrp.toString())
            intent.putExtra(Constant.DESCRIPTION, it.sku_description)
            startActivity(intent)
            this.finish()
            /*cartProduct = CartProduct(
                it.id,
                it.name,
                if (it.images.isEmpty()) "" else (it.images[0].src),
                it.description,
                1,
                it.price.toDouble(),
                it.regular_price.toDouble()
            )
            val imageList: ArrayList<String> = ArrayList()
            for (image in it.images) {
                imageList.add(image.src)
            }
            progressDialog.dismiss()
            GlobalScope.launch(Dispatchers.Main) {
                mainViewModel.insertToCart(cartProduct)
            }*/
        })
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }


    private fun handlePermissions(permissions: Array<String?>) {
        var permissions: Array<String?>? = permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notGrantedPerms: ArrayList<String?> = ArrayList()
            for (p in permissions!!) {
                if (checkSelfPermission(p!!) != PackageManager.PERMISSION_GRANTED) notGrantedPerms.add(
                    p
                )
            }
            permissions = notGrantedPerms.toArray(arrayOfNulls(0))
            if (permissions != null && permissions.isNotEmpty()) requestPermissions(
                permissions,
                701
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 701) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                for (p in permissions) {
                    var msg = ""
                    msg =
                        if (checkSelfPermission(p) == PackageManager.PERMISSION_GRANTED) "Permission Granted for $p" else "Permission not Granted for $p"
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
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

    private fun initProgressDialog(): AlertDialog {
        progressDialog = setProgressDialog(this, "Fetching product")
        progressDialog.setCancelable(false) // blocks UI interaction
        return progressDialog
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}

