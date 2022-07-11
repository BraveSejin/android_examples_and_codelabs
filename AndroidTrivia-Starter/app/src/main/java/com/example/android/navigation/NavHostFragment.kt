package com.example.android.navigation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.navigation.databinding.FragmentNavhostBinding


/**
 * A simple [Fragment] subclass.
 * Use the [NavHostFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NavHostFragment : Fragment() {
    // TODO: Rename and change types of parameters



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding = DataBindingUtil.inflate<FragmentNavhostBinding>(inflater, R.layout.fragment_navhost, container, false)
        return binding.root
    }

}