package com.example.gpsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

// For After login implementaion
    FirebaseAuth auth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    TextView username,email;

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView ProfilePic;
    GoogleSignInAccount acc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        navigationView= findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        username = headerView.findViewById(R.id.username);
        email = headerView.findViewById(R.id.email);
        ProfilePic = headerView.findViewById(R.id.profilepic);

        navigationView.setNavigationItemSelectedListener(this);

        drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        acc = GoogleSignIn.getLastSignedInAccount(this);
        SharedPreferences sharedPreferences = this.getSharedPreferences("mypref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(acc == null){
            auth = FirebaseAuth.getInstance();
            System.out.println(auth.getCurrentUser()+"--------");
            if (auth.getCurrentUser() == null) {
                Intent intent = new Intent(NavActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }else{
                username.setText(auth.getCurrentUser().getDisplayName());
                email.setText(auth.getCurrentUser().getEmail());
                editor.putString("username",auth.getCurrentUser().getDisplayName());
                editor.putString("email",auth.getCurrentUser().getEmail());
                editor.apply();
            }
        }
        else{
            username.setText(acc.getDisplayName());
            email.setText(acc.getEmail());
            editor.putString("username",acc.getDisplayName());
            editor.putString("email",acc.getEmail());
            editor.apply();
        }


    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment()).commit();
        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new SettingsFragment()).commit();
        } else if (id == R.id.nav_steps) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new StepsFragment()).commit();
        }else if(id == R.id.nav_map){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MapFragment()).commit();
        }else if(id == R.id.nav_logout){
            if(acc != null){
                gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(NavActivity.this, LoginActivity.class));
                        finish();
                    }
                });
            }else{
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(NavActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            Toast.makeText(NavActivity.this, "You are logged out!", Toast.LENGTH_SHORT).show();
        }
        if(id!= R.id.nav_logout){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}