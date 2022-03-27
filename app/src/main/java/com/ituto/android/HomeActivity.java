package com.ituto.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ituto.android.Fragments.MainFragments.AccountFragment;
import com.ituto.android.Fragments.MainFragments.ContactsFragment;
import com.ituto.android.Fragments.MainFragments.HomeFragment;
import com.ituto.android.Fragments.MainFragments.MainSessionsFragment;
import com.ituto.android.Fragments.MainFragments.TutorsFragment;
import com.ituto.android.Services.SocketIOService;

import io.socket.client.Socket;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigation;
    private static final int GALLERY_ADD_POST = 2;

    private Boolean fromBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        startService(new Intent(getBaseContext(), SocketIOService.class));
//        Toolbar toolbar = findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }
        init();
    }

    private void init() {

        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment current = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            fromBack = true;
            if (current instanceof HomeFragment)
                bottomNavigation.setSelectedItemId(R.id.item_home);
            else if (current instanceof TutorsFragment)
                bottomNavigation.setSelectedItemId(R.id.item_tutors);
            else if (current instanceof MainSessionsFragment)
                bottomNavigation.setSelectedItemId(R.id.item_tasks);
            else if (current instanceof ContactsFragment)
                bottomNavigation.setSelectedItemId(R.id.item_messages);
            else if (current instanceof AccountFragment)
                bottomNavigation.setSelectedItemId(R.id.item_account);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        startService(new Intent(getBaseContext(), SocketIOService.class));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
//        Fragment currentFragment = fm.findFragmentById(R.id.fragment_container);
//
//        if (currentFragment instanceof HomeFragment && id != R.id.item_home)
//            bottomNavigation.setSelectedItemId(R.id.item_home);
//        else if (currentFragment instanceof TutorsFragment && id != R.id.item_tutors)
//            bottomNavigation.setSelectedItemId(R.id.item_tutors);
//        else if (currentFragment instanceof SessionsFragment && id != R.id.item_tasks)
//            bottomNavigation.setSelectedItemId(R.id.item_tasks);
//        else if (currentFragment instanceof ContactsFragment && id != R.id.item_messages)
//            bottomNavigation.setSelectedItemId(R.id.item_messages);
//        else if (currentFragment instanceof AccountFragment && id != R.id.item_account)
//            bottomNavigation.setSelectedItemId(R.id.item_account);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == bottomNavigation.getSelectedItemId() || fromBack) {
            fromBack = false;
            return true;
        }

        switch (item.getItemId()) {
            case R.id.item_home:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, new HomeFragment()).addToBackStack(null).commit();
                break;
            case R.id.item_tutors:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, new TutorsFragment()).addToBackStack(null).commit();
                break;
            case R.id.item_tasks:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, new MainSessionsFragment()).addToBackStack(null).commit();
                break;
            case R.id.item_messages:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, new ContactsFragment()).addToBackStack(null).commit();
                break;
            case R.id.item_account:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, new AccountFragment()).addToBackStack(null).commit();
                break;
//                dialog = new Dialog(HomeActivity.this);
//                dialog.setContentView(R.layout.custom_alert_dialog);
//
//                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//                Button btnYes = dialog.findViewById(R.id.btnYes);
//                Button btnNo = dialog.findViewById(R.id.btnNo);
//
//                dialog.show();
//
//                btnYes.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        logout();
//                    }
//                });
//
//                btnNo.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.cancel();
//                    }
//                });


        }
        return true;
    }

}