package ci.nsu.mobile.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootNavHost) as NavHostFragment
        val navController = navHostFragment.navController

        if (savedInstanceState == null) {
            val app = application as DepositApplication
            val graph = navController.navInflater.inflate(R.navigation.root_nav_graph)
            graph.setStartDestination(
                if (app.serviceLocator.sessionManager.isLoggedIn()) {
                    R.id.mainContainerFragment
                } else {
                    R.id.loginFragment
                }
            )
            navController.setGraph(graph, null)
        }
    }
}
