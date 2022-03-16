package com.ituto.android.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Constant;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private View view;
    private CircleImageView imgUserInfo;
    private FloatingActionButton fabUpload;
    private TextInputEditText txtFirstname, txtLastname, txtUsername, txtBirthdate, txtPhone;
    private AutoCompleteTextView txtGender, txtCourse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        init();
        return view;
    }

    private void init() {
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        imgUserInfo = view.findViewById(R.id.imgUserInfo);
        fabUpload = view.findViewById(R.id.fabUpload);
        txtFirstname = view.findViewById(R.id.txtFirstname);
        txtLastname = view.findViewById(R.id.txtLastname);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtBirthdate = view.findViewById(R.id.txtBirthdate);
        txtGender = view.findViewById(R.id.txtGender);
        txtCourse = view.findViewById(R.id.txtCourse);
        txtPhone = view.findViewById(R.id.txtPhone);

        getUser();

    }

    private void getUser() {

        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if(object.getBoolean("success")){
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");
                    JSONObject course = user.getJSONObject("course");

                    txtFirstname.setText(user.getString("firstname"));
                    txtLastname.setText(user.getString("lastname"));
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    Date date = format.parse(user.getString("birthdate"));
                    String outputPattern = "yyyy-MM-dd";
                    SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
                    txtBirthdate.setText(outputFormat.format(date));
                    txtUsername.setText(user.getString("username"));
                    txtGender.setText(user.getString("gender"));
                    txtCourse.setText(course.getString("name"));
                    Picasso.get().load(avatar.getString("url")).into(imgUserInfo);
                    txtPhone.setText(user.getString("phone"));
                }
            } catch (JSONException | ParseException e) {
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