package com.aashutosh.simplestore.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.database.SharedPrefHelper
import com.aashutosh.simplestore.ui.HomeActivity
import com.aashutosh.simplestore.ui.deliveryAddress.DeliveryAddressActivity
import com.aashutosh.simplestore.ui.mapActivity.MapsActivity
import com.aashutosh.simplestore.ui.onBoarding.OnboardFinishActivity
import com.aashutosh.simplestore.ui.profile.MyDetailsActivity
import com.aashutosh.simplestore.utils.Constant
import com.bumptech.glide.Glide

class ProfileFragment : Fragment() {

    lateinit var rlProfile: RelativeLayout
    lateinit var clNameAndPhoto: ConstraintLayout
    lateinit var ivBack: ImageView
    lateinit var llLogin: LinearLayout
    private lateinit var sharedPrefHelper: SharedPrefHelper
    lateinit var rlDelivery: RelativeLayout
    lateinit var rlPromoCard: RelativeLayout
    lateinit var rlPaymentMethod: RelativeLayout
    lateinit var rlContactUs: RelativeLayout
    lateinit var rlHelps: RelativeLayout
    lateinit var rlOrderHistory: RelativeLayout
    lateinit var rlNotification: RelativeLayout

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rlProfile = requireView().findViewById(R.id.rlProfile)
        clNameAndPhoto = requireView().findViewById(R.id.clNameAndPhoto)
        ivBack = requireView().findViewById(R.id.ivBack)
        llLogin = requireView().findViewById(R.id.llLogin)
        rlDelivery = requireView().findViewById(R.id.rlDelivery)
        rlPromoCard = requireView().findViewById(R.id.rlPromoCard)
        rlPaymentMethod = requireView().findViewById(R.id.rlPaymentMethod)
        rlContactUs = requireView().findViewById(R.id.rlContactUs)
        rlHelps = requireView().findViewById(R.id.rlHelps)
        rlOrderHistory = requireView().findViewById(R.id.rlOrderHistory)
        rlNotification = requireView().findViewById(R.id.rlNotification)
        val ivProfile = requireView().findViewById<ImageView>(R.id.ivProfile)
        val tvName = requireView().findViewById<TextView>(R.id.tvName)
        val rlLogout = requireView().findViewById<RelativeLayout>(R.id.rlLogout)
        sharedPrefHelper = SharedPrefHelper
        sharedPrefHelper.init(requireActivity().applicationContext)
        if (sharedPrefHelper[Constant.LOGIN, false]) {
            llLogin.visibility = View.GONE
            clNameAndPhoto.visibility = View.VISIBLE
            Glide.with(requireContext()).load(sharedPrefHelper[Constant.PHOTO, ""])
                .placeholder(R.drawable.ic_profile).into(ivProfile)
            tvName.text = sharedPrefHelper[Constant.NAME, ""]
            tvName.textSize = 14.0F
            rlLogout.visibility = View.VISIBLE
            //  rlProfile.visibility = View.VISIBLE
            rlLogout.setOnClickListener(View.OnClickListener {
                sharedPrefHelper[Constant.LOGIN] = false
                val intent = Intent(context, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)

            })
        } else {
            rlLogout.visibility = View.GONE
            llLogin.visibility = View.VISIBLE
            clNameAndPhoto.visibility = View.GONE
            rlProfile.visibility = View.GONE

        }

        rlNotification.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(Constant.IS_NOTIFICATION, true)
            startActivity(intent)
        })
        rlPaymentMethod.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "Default set as Cash On Delivery, other option will be available soon ",
                Toast.LENGTH_SHORT
            ).show()
        })
        rlDelivery.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, DeliveryAddressActivity::class.java)
            startActivity(intent)
        })

        rlOrderHistory.setOnClickListener(View.OnClickListener {
            Toast.makeText(
                context,
                "Check your order history in Website or contact Store.",
                Toast.LENGTH_SHORT
            ).show()
        })

        rlPromoCard.setOnClickListener(View.OnClickListener {
            startActivity(Intent(context, MapsActivity::class.java))
        })
        ivBack.setOnClickListener(View.OnClickListener {

            val intent = Intent(context, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        })
        clNameAndPhoto.setOnClickListener(View.OnClickListener {
            startActivity(
                Intent(
                    requireActivity(), MyDetailsActivity::class.java
                )
            )
        })
        rlContactUs.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://keybrains.xyz/contact")
            )
            startActivity(browserIntent)
        })
        rlProfile.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), MyDetailsActivity::class.java))
        })

        rlHelps.setOnClickListener(View.OnClickListener {
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://keybrains.xyz/contact")
            )
            startActivity(browserIntent)
        })

        llLogin.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireActivity(), OnboardFinishActivity::class.java))
        })
    }
}