package com.example.nammasantheledger.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.example.nammasantheledger.R
import com.example.nammasantheledger.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        setSupportActionBar(binding.topToolbar)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeDashboardFragment,
                R.id.customerListFragment,
                R.id.dailySummaryFragment,
                R.id.settingsAboutFragment
            )
        )
        binding.topToolbar.setupWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigation.isVisible = destination.id in appBarConfiguration.topLevelDestinations
            binding.fabQuickActions.isVisible = destination.id in appBarConfiguration.topLevelDestinations
        }

        binding.fabQuickActions.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.quick_actions))
                .setItems(arrayOf(getString(R.string.add_credit), getString(R.string.add_payment), getString(R.string.add_customer))) { _, which ->
                    when (which) {
                        0 -> navController.navigate(R.id.quickCreditFragment)
                        1 -> navController.navigate(R.id.paymentFragment)
                        2 -> navController.navigate(R.id.addEditCustomerFragment)
                    }
                }.show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
