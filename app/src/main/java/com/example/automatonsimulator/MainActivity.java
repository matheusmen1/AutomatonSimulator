package com.example.automatonsimulator;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    // drawerMenu
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private ExpressaoRegularFragment expressaoRegularFragment = new ExpressaoRegularFragment();
    private AutomatoFinitoFragment automatoFinitoFragment = new AutomatoFinitoFragment();
    private integrantesFragment integrantesFragment = new integrantesFragment();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        drawerLayout=findViewById(R.id.drawerLayout);
        navigationView=findViewById(R.id.navigationView);
        frameLayout = findViewById(R.id.frameLayout);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_menu,R.string.close_menu);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        // achou para itens do menu lateral
        fragmentManager=getSupportFragmentManager();

        if (savedInstanceState == null)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(frameLayout.getId(), automatoFinitoFragment);
            fragmentTransaction.commit();

            navigationView.setCheckedItem(R.id.it_automato);
        }

        navigationView.setNavigationItemSelectedListener(item->
        {
            if(item.getItemId()==R.id.it_expressao)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frameLayout.getId(), expressaoRegularFragment);
                fragmentTransaction.commit();
            }
            if(item.getItemId()==R.id.it_automato)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frameLayout.getId(), automatoFinitoFragment);
                fragmentTransaction.commit();
            }
            if(item.getItemId()==R.id.it_gramatica)
            {
                Toast.makeText(this, "Comming Soon", Toast.LENGTH_SHORT).show();
            }
            if(item.getItemId()==R.id.it_dev)
            {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(frameLayout.getId(), integrantesFragment);
                fragmentTransaction.commit();
            }
            if(item.getItemId()==R.id.it_sair)
            {
                finish();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }
}