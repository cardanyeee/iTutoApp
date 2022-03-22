package com.ituto.android.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.Constant;
import com.ituto.android.ConversationActivity;
import com.ituto.android.Models.Course;
import com.ituto.android.R;
import com.ituto.android.Utils.FilePath;
import com.muddzdev.styleabletoast.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UpdateProfileFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private View view;
    private static final int GALLERY_REQUEST = 104;
    private CircleImageView imgUserInfo;
    private FloatingActionButton fabUpload;
    private TextInputEditText txtFirstname, txtLastname, txtUsername, txtBirthdate, txtPhone;
    private AutoCompleteTextView txtGender, txtCourse, txtYearLevel;
    private static final String[] GENDERS = new String[]{
            "Male", "Female", "Prefer not to say"
    };
    private static final String[] YEAR = new String[]{
            "First", "Second", "Third", "Fourth"
    };
    private Button btnUpdateProfile;

    private String courseID;
    private String gallery_file_path;

    private ArrayList<Course> courseArrayList;
    private ArrayList<String> stringCourseArrayList;

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_update_profile, container, false);
        init();
        return view;
    }

    private void init() {
        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.GONE);
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
        txtYearLevel = view.findViewById(R.id.txtYearLevel);
        btnUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        getUser();
        getCourses();

        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, GALLERY_REQUEST);
            }
        });

        txtBirthdate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            com.wdullaer.materialdatetimepicker.date.DatePickerDialog datePickerDialog = com.wdullaer.materialdatetimepicker.date.DatePickerDialog.newInstance(
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        monthOfYear = monthOfYear + 1;
                        String date = year1 + "-" + monthOfYear + "-" + dayOfMonth;
                        txtBirthdate.setText(date);
                    },
                    year,
                    month,
                    day);
            datePickerDialog.setAccentColor(getResources().getColor(R.color.colorPrimaryLight));
            datePickerDialog.setMaxDate(Calendar.getInstance());
            datePickerDialog.show(getParentFragmentManager(), "");
        });

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_dropdown,
                R.id.txtDropdownItem,
                GENDERS
        );

        txtGender.setAdapter(arrayAdapter);
        txtGender.setThreshold(10);

        ArrayAdapter<String> yearArrayAdapter = new ArrayAdapter<>(
                getContext(),
                R.layout.item_dropdown,
                R.id.txtDropdownItem,
                YEAR
        );

        txtYearLevel.setAdapter(yearArrayAdapter);

        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                UploadTask uploadTask = new UploadTask();
                uploadTask.execute(new String[]{gallery_file_path});
            }
        });

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

    }

    private void getUser() {

        StringRequest request = new StringRequest(Request.Method.GET, Constant.USER_PROFILE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
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
                    txtYearLevel.setText(user.getString("yearLevel"));
                    txtCourse.setText(course.getString("name"));
                    courseID = course.getString("_id");
                    txtPhone.setText(user.has("phone") ? user.getString("phone") : "");
                    Picasso.get().load(avatar.getString("url")).into(imgUserInfo);
                }
            } catch (JSONException | ParseException e) {
                e.printStackTrace();
            }


        }, error -> {
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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getCourses() {
        stringCourseArrayList = new ArrayList<>();
        courseArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Constant.COURSES, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    JSONArray coursesArray = new JSONArray(object.getString("courses"));

                    for (int i = 0; i < coursesArray.length(); i++) {
                        JSONObject courseObject = coursesArray.getJSONObject(i);

                        if (courseObject.getBoolean("active")) {
                            Course course = new Course();

                            course.setId(courseObject.getString("_id"));
                            course.setCode(courseObject.getString("code"));
                            course.setName(courseObject.getString("name"));

                            stringCourseArrayList.add(courseObject.getString("name"));
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.item_dropdown,
                                    R.id.txtDropdownItem,
                                    stringCourseArrayList
                            );

                            courseArrayList.add(course);
                            txtCourse.setAdapter(arrayAdapter);
                        }

                    }

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
                map.put("Authorization", "Bearer " + token);
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("requestCoderequestCoderequestCode", String.valueOf(requestCode));
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST) {
                if (data == null) {
                    return;
                }

                Uri uri = data.getData();
                String selectedPath = FilePath.getFilePath(getContext(), uri);
                Log.d("File Path ", " " + selectedPath);
                if (selectedPath != null) {
//                    gallery_file_name.setText("" + new File(selectedPath).getName());
                }
                Bitmap bitmap = BitmapFactory.decodeFile(selectedPath);
                imgUserInfo.setImageBitmap(bitmap);
                gallery_file_path = selectedPath;
            }
        }
    }

    public class UploadTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                getActivity().getSupportFragmentManager().popBackStack();
                StyleableToast.makeText(getContext(), "Account Updated", R.style.CustomToast).show();
            } else {
                StyleableToast.makeText(getContext(), "There was a problem updating your account", R.style.CustomToast).show();
            }
            dialog.dismiss();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                MultipartBody.Builder body = new MultipartBody.Builder().setType(MultipartBody.FORM);

                if (!(strings[0] == null)) {
                    File file1 = new File(strings[0]);
                    body.addFormDataPart("avatar", file1.getName(), RequestBody.create(MediaType.parse("*/*"), file1));
                }

                body.addFormDataPart("firstname", txtFirstname.getText().toString().trim())
                        .addFormDataPart("lastname", txtLastname.getText().toString().trim())
                        .addFormDataPart("username", txtUsername.getText().toString().trim())
                        .addFormDataPart("birthdate", txtBirthdate.getText().toString().trim())
                        .addFormDataPart("gender", txtGender.getText().toString().trim())
                        .addFormDataPart("course", courseID)
                        .addFormDataPart("yearLevel", txtYearLevel.getText().toString().trim())
                        .addFormDataPart("phone", txtPhone.getText().toString().trim());

                RequestBody requestBody = body.build();


                String token = sharedPreferences.getString("token", "");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(Constant.UPDATE_PROFILE)
                        .addHeader("Authorization", "Bearer " + token)
                        .put(requestBody)
                        .build();

                OkHttpClient okHttpClient = new OkHttpClient();

                Response response = okHttpClient.newCall(request).execute();
                if (response != null && response.isSuccessful()) {
                    dialog.dismiss();
                    return response.body().string();
                } else {
                    dialog.dismiss();
                    return null;
                }

            } catch (Exception e) {
                dialog.dismiss();
                e.printStackTrace();
            }
            return null;
        }
    }
}