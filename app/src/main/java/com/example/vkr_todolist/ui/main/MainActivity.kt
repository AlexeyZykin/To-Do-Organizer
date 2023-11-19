package com.example.vkr_todolist.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vkr_todolist.App
import com.example.vkr_todolist.NavGraphDirections
import com.example.vkr_todolist.R
import com.example.vkr_todolist.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navigationView: NavigationView
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as App).database)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        actionBarSettings()
        visibilityBottomBar()
        listItemObserver()
    }

    private fun listItemObserver(){
        navigationView = binding.navView
        val menu = navigationView.menu
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.addEditListFragment -> {
                   val action = NavGraphDirections.actionGlobalAddEditListFragment(null)
                   navController.navigate(action)
                }
                R.id.settingsFragment -> {
                    val action = NavGraphDirections.actionGlobalSettingsFragment()
                    navController.navigate(action)
                }
                R.id.importantTasksFragment -> {
                    val action = NavGraphDirections.actionGlobalImportantTasksFragment()
                    navController.navigate(action)
                }
                R.id.completedTasksFragment -> {
                    val action = NavGraphDirections.actionGlobalCompletedTasksFragment()
                    navController.navigate(action)
                }
                R.id.productivityFragment -> {
                    val action = NavGraphDirections.actionGlobalProductivityFragment()
                    navController.navigate(action)
                }
                R.id.pomodoroTimerFragment -> {
                    val action = NavGraphDirections.actionGlobalPomodoroTimerFragment()
                    navController.navigate(action)
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START, true)
            true
        }

        viewModel.allListItem.observe(this){items->
            menu.removeGroup(R.id.group_lists)
            // добавляем новые элементы меню
            items.forEach { item ->
                val icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_list_item)
                menu.add(R.id.group_lists, item.listId!!, Menu.NONE, item.listTitle)
                    .setIcon(icon)
                    .setCheckable(true)
                    .setOnMenuItemClickListener {menuItem->
                        val action = NavGraphDirections.actionGlobalListFragment(item)
                        navController.navigate(action)
                        binding.drawerLayout.closeDrawers()
                        true
                    }
                //viewModel.deleteListItem(item)
            }
            menu.add(R.id.group_lists, R.id.addEditListFragment, Menu.NONE, R.string.add_new_list)
                .setIcon(R.drawable.ic_add)
                .setCheckable(true)
        }
    }

    private fun initNavigation(){
        setSupportActionBar(binding.appBarMain.toolbar)
        navController=findNavController(R.id.fragmentContainerView)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.taskFragment,
                R.id.noteFragment,
                R.id.eventFragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bNav.setupWithNavController(navController)
        binding.navView.setupWithNavController(navController)
        navController.handleDeepLink(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun actionBarSettings(){
        val ab=supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun visibilityBottomBar() = with (binding){
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addEditNoteFragment-> bNav.visibility = View.GONE
                R.id.addEditTaskFragment->bNav.visibility=View.GONE
                R.id.aboutAppFragment->bNav.visibility=View.GONE
                R.id.addEditListFragment->bNav.visibility=View.GONE
                R.id.listFragment->bNav.visibility=View.GONE
                R.id.settingsFragment -> bNav.visibility=View.GONE
                R.id.importantTasksFragment -> bNav.visibility=View.GONE
                R.id.completedTasksFragment -> bNav.visibility=View.GONE
                R.id.searchFragment -> bNav.visibility=View.GONE
                R.id.productivityFragment -> bNav.visibility=View.GONE
                R.id.pomodoroTimerFragment -> bNav.visibility=View.GONE
                else -> bNav.visibility = View.VISIBLE
            }
        }
    }

    companion object{
        const val LIST_NAME = "list_name"
    }

}