package com.ituto.android.Fragments;

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
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.imageview.ShapeableImageView;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("ALL")
public class AccountFragment extends Fragment {
    private View view;
    private TextView txtEmail, txtUserName;
    private Button btnLogOut;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;
    private ShapeableImageView imgUserInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        btnLogOut = view.findViewById(R.id.btnLogOut);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtUserName = view.findViewById(R.id.txtUserName);
        imgUserInfo = view.findViewById(R.id.imgUserInfo);

        btnLogOut.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.layout_logout_dialog);

            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

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
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");

                    txtUserName.setText(user.getString("firstname") + " " + user.getString("lastname"));
                    txtEmail.setText(user.getString("email"));
                    Picasso.get().load(avatar.getString("url")).fit().centerCrop().into(imgUserInfo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization", "Bearer "+token);
                return map;
            }
        };

        Log.d("TAG", String.valueOf(request.getBodyContentType()));
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}