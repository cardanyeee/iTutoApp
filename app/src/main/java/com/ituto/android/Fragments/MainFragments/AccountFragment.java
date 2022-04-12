package com.ituto.android.Fragments.MainFragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.TutorProfileFragment;
import com.ituto.android.Fragments.UpdateProfileFragment;
import com.ituto.android.Fragments.UpdateTutorAvailabilityFragment;
import com.ituto.android.Fragments.UpdateTutorSubjectsFragment;
import com.ituto.android.Models.Tutor;
import com.ituto.android.R;
import com.ituto.android.Services.SocketIOService;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private View view;
    private MaterialCardView crdAboutMe, crdUpdateSubjects, crdUpdateAvailability, crdTutorProfile;
    private TextInputEditText txtAboutMeInput;
    private TextView txtName, txtBirthdate, txtEmail, txtUsername, txtPhone, txtGender, txtCourse, txtAboutMe;
    private ImageView imgUpdateProfile, imgEditAboutMe;
    private Button btnLogOut;
    private SharedPreferences sharedPreferences;
    private Dialog dialog;
    private CircleImageView imgUserInfo;
    private String loggedInAs;
    private String userID, tutorID;
    private Dialog reviewDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        return view;
    }

    private void init() {

        dialog = new Dialog(getContext(), R.style.DialogTheme);
        dialog.getWindow().getAttributes().windowAnimations = R.style.SplashScreenDialogAnimation;
        dialog.setContentView(R.layout.layout_dialog_progress);
        RelativeLayout dialogLayout = dialog.findViewById(R.id.rllDialog);
        dialog.show();

        BottomAppBar bottomAppBar = getActivity().findViewById(R.id.bottomAppBar);
        bottomAppBar.setVisibility(View.VISIBLE);
        sharedPreferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        loggedInAs = sharedPreferences.getString("loggedInAs", "");
        btnLogOut = view.findViewById(R.id.btnLogOut);
        crdAboutMe = view.findViewById(R.id.crdAboutMe);
        crdUpdateSubjects = view.findViewById(R.id.crdUpdateSubjects);
        crdUpdateAvailability = view.findViewById(R.id.crdUpdateAvailability);
        crdTutorProfile = view.findViewById(R.id.crdTutorProfile);
        txtAboutMe = view.findViewById(R.id.txtAboutMe);
        txtName = view.findViewById(R.id.txtName);
        imgUpdateProfile = view.findViewById(R.id.imgUpdateProfile);
        imgEditAboutMe = view.findViewById(R.id.imgEditAboutMe);
        txtBirthdate = view.findViewById(R.id.txtBirthdate);
        txtEmail = view.findViewById(R.id.txtEmail);
        txtUsername = view.findViewById(R.id.txtUsername);
        txtPhone = view.findViewById(R.id.txtPhone);
        txtGender = view.findViewById(R.id.txtGender);
        txtCourse = view.findViewById(R.id.txtCourse);
        imgUserInfo = view.findViewById(R.id.imgUserInfo);

        if (loggedInAs.equals("TUTOR")) {
            crdAboutMe.setVisibility(View.VISIBLE);
            crdUpdateSubjects.setVisibility(View.VISIBLE);
            crdUpdateAvailability.setVisibility(View.VISIBLE);
            crdTutorProfile.setVisibility(View.VISIBLE);
            imgEditAboutMe.setOnClickListener(view -> {
                reviewDialog = new Dialog(getContext());

                reviewDialog.setContentView(R.layout.layout_dialog_aboutme);
                reviewDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                reviewDialog.getWindow().getAttributes().windowAnimations = R.style.AddQuestionDialogAnimation;
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int width = metrics.widthPixels;
                reviewDialog.getWindow().setLayout((6 * width) / 7, reviewDialog.getWindow().getAttributes().height);

                MaterialButton btnSubmit, btnCancel;

//                    rtbTutorRating = reviewDialog.findViewById(R.id.rtbTutorRating);
//                    txtComment = reviewDialog.findViewById(R.id.txtComment);
                txtAboutMeInput = reviewDialog.findViewById(R.id.txtAboutMeInput);
                btnCancel = reviewDialog.findViewById(R.id.btnCancel);
                btnSubmit = reviewDialog.findViewById(R.id.btnSubmit);

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!(txtAboutMeInput.getText().toString().isEmpty())) {
                            updateAboutMe();
                        }
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        reviewDialog.cancel();
                    }
                });

                reviewDialog.show();
            });

            crdUpdateSubjects.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                UpdateTutorSubjectsFragment updateTutorSubjectsFragment = new UpdateTutorSubjectsFragment();
                bundle.putString("userID", userID);
                updateTutorSubjectsFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.slide_in,
                        0// popExit
                ).replace(R.id.fragment_container, updateTutorSubjectsFragment).addToBackStack(null).commit();
            });

            crdUpdateAvailability.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                UpdateTutorAvailabilityFragment updateTutorAvailabilityFragment = new UpdateTutorAvailabilityFragment();
                bundle.putString("userID", userID);
                updateTutorAvailabilityFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.slide_in,
                        0// popExit
                ).replace(R.id.fragment_container, updateTutorAvailabilityFragment).addToBackStack(null).commit();
            });

            crdTutorProfile.setOnClickListener(view -> {
                Bundle bundle = new Bundle();
                TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
                bundle.putString("_id", tutorID);
                tutorProfileFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, tutorProfileFragment).addToBackStack(null).commit();
            });

            getCurrentTutor();
        }

        dialog.setOnCancelListener(dialogInterface -> getActivity().getSupportFragmentManager().popBackStack());

        btnLogOut.setOnClickListener(v -> {
            dialog = new Dialog(getContext());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setContentView(R.layout.layout_dialog_logout);

            Button btnYes = dialog.findViewById(R.id.btnYes);
            Button btnNo = dialog.findViewById(R.id.btnNo);

            dialog.show();

            btnYes.setOnClickListener(v1 -> logout());

            btnNo.setOnClickListener(v12 -> dialog.cancel());
        });

        imgUpdateProfile.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            UpdateProfileFragment updateProfileFragment = new UpdateProfileFragment();
            updateProfileFragment.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.slide_in,
                    0// popExit
            ).replace(R.id.fragment_container, updateProfileFragment).addToBackStack(null).commit();
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
                    getActivity().getApplicationContext().stopService(new Intent(getActivity().getApplicationContext(), SocketIOService.class));
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

                    userID = user.getString("_id");
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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void getCurrentTutor() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.CURRENT_TUTOR, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject tutorObject = object.getJSONObject("tutor");
                    tutorID = tutorObject.getString("_id");
                    if (!tutorObject.has("aboutMe")) {
                        txtAboutMe.setText("");
                    } else {
                        txtAboutMe.setText(tutorObject.getString("aboutMe"));
                    }
                }
                dialog.dismiss();
            } catch (JSONException e) {
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

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    private void updateAboutMe() {
        StringRequest request = new StringRequest(Request.Method.PUT, Constant.UPDATE_ABOUTME, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    reviewDialog.dismiss();
                    getCurrentTutor();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "Subjects Updated Unsuccessfully!", R.style.CustomToast).show();
            }

        }, error -> {
            StyleableToast.makeText(getContext(), "Subjects Updated Unsuccessfully!", R.style.CustomToast).show();
            error.printStackTrace();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                String token = sharedPreferences.getString("token", "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("aboutMe", txtAboutMeInput.getText().toString().trim());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}