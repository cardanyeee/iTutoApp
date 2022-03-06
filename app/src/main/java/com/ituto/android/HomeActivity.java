package com.ituto.android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ituto.android.Fragments.AccountFragment;
import com.ituto.android.Fragments.ContactsFragment;
import com.ituto.android.Fragments.HomeFragment;
import com.ituto.android.Fragments.TasksFragment;
import com.ituto.android.Fragments.TutorsFragment;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigation;
    private static final int GALLERY_ADD_POST = 2;

    public Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Toolbar toolbar = findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TutorsFragment()).commit();
        }
        init();
    }

    private void init() {

        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(this);
//        getUser();
    }

//    private void getUser() {
//        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {
//
//            try {
//                JSONObject object = new JSONObject(response);
//                if (object.getBoolean("success")) {
//                    JSONObject user = object.getJSONObject("user");
//
//                    navUserName.setText(user.getString("name"));
//                    navUserEmail.setText(user.getString("email"));
////                    Picasso.get().load(Constant.URL+"storage/profiles/" + user.getString("photo")).fit().centerCrop().into(navUserPhoto);
//
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        }, error -> {
//            error.printStackTrace();
//        }){
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                String token = userPref.getString("access_token", "");
//                HashMap<String, String> map = new HashMap<>();
//                map.put("Authorization", "Bearer" + token);
//                return map;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        queue.add(request);
//    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

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
                ).replace(R.id.fragment_container, new TasksFragment()).addToBackStack(null).commit();
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