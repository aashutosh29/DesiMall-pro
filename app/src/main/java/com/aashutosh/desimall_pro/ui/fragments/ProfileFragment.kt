package com.aashutosh.desimall_pro.ui.fragments

import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.aashutosh.desimall_pro.R
import com.aashutosh.desimall_pro.database.SharedPrefHelper
import com.aashutosh.desimall_pro.databinding.ProfileFragmentBinding
import com.aashutosh.desimall_pro.ui.HomeActivity
import com.aashutosh.desimall_pro.ui.LoginActivity
import com.aashutosh.desimall_pro.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.desimall_pro.ui.detailsVerificationPage.DetailsVerificationActivity
import com.aashutosh.desimall_pro.ui.mapActivity.MapsActivity
import com.aashutosh.desimall_pro.ui.myProfileActivity.MyProfileActivity
import com.aashutosh.desimall_pro.ui.orderHistoryActivity.OrderHistoryActivity
import com.aashutosh.desimall_pro.ui.phoneVerification.EnterNumberActivity
import com.aashutosh.desimall_pro.utils.Constant

class ProfileFragment : Fragment() {


    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var binding: ProfileFragmentBinding

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
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)


       /* binding.rlProfile.setOnClickListener(View.OnClickListener {
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

        binding.rlProfile.setOnClickListener(View.OnClickListener {
        if (!sharedPrefHelper[Constant.LOGIN_SUCCESS, false]) {
            val i = Intent(requireContext(), LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        else{
            val i = Intent(requireContext(), MyProfileActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
        }
        })

        binding.rlLogout.setOnClickListener(View.OnClickListener {

            showLogoutConfirmationDialog()
        })
        binding.ivBack.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })
        binding.rlNotification.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        })
        binding.rlPaymentMethod.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "Default set as Cash On Delivery, other option will be available soon ",
                Toast.LENGTH_SHORT
            ).show()
        })
        binding.rlDelivery.setOnClickListener(View.OnClickListener {
            Toast.makeText(requireContext(),"Available soon",Toast.LENGTH_SHORT).show()
            /*if (!sharedPrefHelper[Constant.VERIFIED_NUM, false]) {
                val i = Intent(requireContext(), EnterNumberActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            } else if (!sharedPrefHelper[Constant.VERIFIED_LOCATION, false]) {
                val i = Intent(requireContext(), MapsActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                startActivity(i)
            } else if (!sharedPrefHelper[Constant.DETAILIlS_VERIFIED, false]) {
                val i = Intent(
                    requireContext(),
                    DetailsVerificationActivity::class.java
                )
                i.putExtra(Constant.VERIFY_USER_LOCATION, true)
                i.putExtra(Constant.DETAILS, true)
                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(i)
            } else {
                val intent = Intent(context, DeliveryAddressActivity::class.java)
                startActivity(intent)
            }*/
        })

        binding.rlOrderHistory.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, OrderHistoryActivity::class.java)
            startActivity(intent)
        })

        binding.rlPromoCard.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "Promo card will be available soon ",
                Toast.LENGTH_SHORT
            ).show()
        })
        binding.ivBack.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })
        binding.rlContactUs.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://livedesimall.in/contact-2/")
            )
            startActivity(browserIntent)
        })


        binding.rlHelps.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://livedesimall.in/contact-2/")
            )
            startActivity(browserIntent)
        })

    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
            .setMessage("Do you want to logout?")
            .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                // Clear all shared preferences
                val sharedPreferences =
                    requireContext().getSharedPreferences("app_preferences", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply()

                // Send the user back to the Login screen
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                requireActivity().finish()
            }
            .setNegativeButton("No") { dialog: DialogInterface, _: Int ->
                // Dismiss the dialog
                dialog.dismiss()
            }
            .show()
    }
}