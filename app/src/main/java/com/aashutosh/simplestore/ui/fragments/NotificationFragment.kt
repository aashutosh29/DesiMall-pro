package com.aashutosh.simplestore.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.aashutosh.simplestore.R
import com.aashutosh.simplestore.ui.HomeActivity

class NotificationFragment : Fragment() {
    lateinit var acbShopNow: AppCompatButton

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
        return inflater.inflate(R.layout.notification_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        acbShopNow = requireView().findViewById(R.id.acbShopNow)
        acbShopNow.setOnClickListener(View.OnClickListener {
            startActivity(Intent(requireContext(), HomeActivity::class.java))

        })
    }
}