package com.ituto.android.Fragments.MainFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.UpdateProfileFragment;
import com.ituto.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class AccountFragment extends Fragment {
    private View view;
    private TextView txtName, txtBirthdate, txtEmail, txtUsername, txtPhone, txtGender, txtCourse;
    private ImageView imgUpdateProfile;
    private Button btnLogOut;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;
    private CircleImageView imgUserInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.VISIBLE);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        txtName = view.findViewById(R.id.txtName);
        imgUpdateProfile = view.findViewById(R.id.imgUpdateProfile);
        txtBirthdate = view.findViewById(R.id.txtBirthdate);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtGender = view.findViewById(R.id.txtGender);
        txtCourse = view.findViewById(R.id.txtCourse);
        imgUserInfo = view.findViewById(R.id.imgUserInfo);

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        RelativeLayout dialogLayout = dialog.findViewById(R.id.rllDialog);
        dialog.setCancelable(false);
        dialog.show();
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams wlp = window.getAttributes();
//        wlp.height = getActivity().getResources().getDisplayMetrics().heightPixels - bottomAppBar.getHeight();
//        window.setAttributes(wlp);

        btnLogOut.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.layout_dialog_logout);

            Button btnYes = dialog.findViewById(R.id.btnYes);
            Button btnNo = dialog.findViewById(R.id.btnNo);

            dialog.show();

            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
        });

        imgUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
                updateProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.slide_in,
                        0// popExit
                ).replace(R.id.fragment_container, updateProfileFragment).addToBackStack(null).commit();
            }
        });

        getUser();
    }

    private void logout() {
        dialog.dismiss();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.LOGOUT, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.apply();
                    startActivity(new Intent((getActivity().getApplicationContext()), AuthActivity.class));
                    getActivity().finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer" + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getUser() {

        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");
                    JSONObject course = user.getJSONObject("course");

                    txtName.setText(user.getString("firstname") + " " + user.getString("lastname"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(user.getString("birthdate"));
                    String outputPattern = "MMMM dd, yyyy";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                    txtBirthdate.setText(outputFormat.format(date));
                    txtUsername.setText(user.getString("username"));
                    txtEmail.setText(user.getString("email"));
                    txtGender.setText(user.getString("gender"));
                    txtCourse.setText(course.getString("name"));
                    txtPhone.setText(user.has("phone") ? user.getString("phone") : "");
                    Glide.with(getContext()).load(avatar.getString("url")).placeholder(R.drawable.blank_avatar).into(imgUserInfo);
                }
                dialog.dismiss();
            } catch (JSONException | ParseException e) {
                dialog.dismiss();
                e.printStackTrace();
            }


        }, error -> {
            dialog.dismiss();
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };

        Log.d("TAG", String.valueOf(request.getBodyContentType()));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}