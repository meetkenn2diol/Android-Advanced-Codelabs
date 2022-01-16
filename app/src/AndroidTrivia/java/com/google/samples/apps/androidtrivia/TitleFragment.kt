package com.google.samples.apps.androidtrivia

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.samples.apps.androidtrivia.databinding.FragmentTitleBinding


class TitleFragment : Fragment() {
    //binding object
    private lateinit var binding: FragmentTitleBinding

    //declare the navigation controller
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container, false)

     
        binding.apply {
            playButton.setOnClickListener { navController.navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment()) }
            rulesButton.setOnClickListener { navController.navigate(TitleFragmentDirections.actionTitleFragmentToRulesFragment()) }
            aboutButton.setOnClickListener { navController.navigate(TitleFragmentDirections.actionTitleFragmentToAboutFragment()) }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = view.findNavController()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //The following code displays the Fragment that has the same ID as the menu item. (This code only works if the menu item and the Fragment have identical ID values.)
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }
}