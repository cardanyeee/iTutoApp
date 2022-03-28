package com.ituto.android.Fragments.AuthFragments;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ituto.android.AuthActivity;
import com.ituto.android.Constant;
import com.ituto.android.Fragments.AuthFragments.MainAuthFragment;
import com.ituto.android.HomeActivity;
import com.ituto.android.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class SignInFragment extends Fragment {
    private View view;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtSignUp;
    private Button btnSignIn, btnSignInWithGoogle;
    private ProgressDialog dialog;
    private GoogleSignInClient googleSignInClient;
    public static String loggedInAs;

    private static int RC_SIGN_IN = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        init();
        return view;
    }

    private void init() {
        loggedInAs = null;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity().getApplicationContext(), gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());

//        btnSignInWithGoogle = view.findViewById(R.id.btnSignInWithGoogle);
//
//        btnSignInWithGoogle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signInWithGoogle();
//            }
//        });

        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);
        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        txtSignUp = view.findViewById(R.id.txtSignUp);
        btnSignIn = view.findViewById(R.id.btnSignIn);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        txtSignUp.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            ).replace(R.id.frameAuthContainer, new MainAuthFragment()).addToBackStack(null).commit();
        });

        btnSignIn.setOnClickListener(v -> {
            if (validate()) {
                login();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (txtPassword.getText().toString().length() > 7) {
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (loggedInAs == null) {
            StyleableToast.makeText(getContext(), "Please select what kind account you would like to login", R.style.CustomToast).show();
            return false;
        }

        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Enter a valid e-mail");
            return false;
        }

        if (txtPassword.getText().toString().length() < 8) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Required at least 8 characters");
            return false;
        }

        return true;
    }

    private void login() {
        dialog.setMessage("Logging in");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.LOGIN, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    JSONObject avatar = user.getJSONObject("avatar");
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putString("_id", user.getString("_id"));
                    editor.putString("avatar", avatar.getString("url"));
                    editor.putString("firstname", user.getString("firstname"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("isTutor", user.getString("isTutor"));
                    editor.putString("loggedInAs", loggedInAs);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(((AuthActivity) getContext()), HomeActivity.class));
                    ((AuthActivity) getContext()).finish();

                    StyleableToast.makeText(getContext(), "Login Successful", R.style.CustomToast).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "Login Unsuccessful", R.style.CustomToast).show();
            }
            dialog.dismiss();
        }, error -> {
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtPassword.getText().toString());
                map.put("loggedInAs", loggedInAs);
                return map;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                runOnUiThread(() -> {
                    try {
                        String body;
                        body = new String(volleyError.networkResponse.data,"UTF-8");
                        JSONObject error = new JSONObject(body);
                        StyleableToast.makeText(getContext(), error.getString("message"), R.style.CustomToast).show();
                    } catch (UnsupportedEncodingException | JSONException e) {
                        e.printStackTrace();
                    }
                });
                return super.parseNetworkError(volleyError);
            }

        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
        Log.d("TAG", request.toString());
    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity().getApplicationContext());
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();

                Toast.makeText(getActivity().getApplicationContext(), personEmail, Toast.LENGTH_SHORT).show();

                googleSignIn(acct.getIdToken());
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", e.toString());

        }
    }

    public void googleSignIn(String idToken) {
        dialog.setMessage("Registering");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, Constant.GOOGLE_LOGIN, response -> {

            try {
                JSONObject object = new JSONObject(response);
                Log.d("TAGTAGTAGTAG", object.toString());
                if (object.getBoolean("success")) {
                    JSONObject user = object.getJSONObject("user");
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("token", object.getString("token"));
                    editor.putString("_id", user.getString("_id"));
                    editor.putString("firstname", user.getString("firstname"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("isTutor", user.getString("isTutor"));
                    editor.putString("loggedInAs", loggedInAs);
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();
                    startActivity(new Intent(((AuthActivity) getContext()), HomeActivity.class));
                    ((AuthActivity) getContext()).finish();
                }
                StyleableToast.makeText(getContext(), "Login Successful", R.style.CustomToast).show();
            } catch (JSONException e) {
                e.printStackTrace();
                StyleableToast.makeText(getContext(), "Login Unsuccessful", R.style.CustomToast).show();
            }
            dialog.dismiss();

        }, error -> {
            StyleableToast.makeText(getContext(), "Login Unsuccessful", R.style.CustomToast).show();
            error.printStackTrace();
            dialog.dismiss();
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("tokenId", idToken);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

}
