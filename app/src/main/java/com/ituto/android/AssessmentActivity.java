package com.ituto.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Adapters.MessagesAdapter;
import com.ituto.android.Models.Assessment;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.Question;
import com.ituto.android.Models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AssessmentActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ArrayList<Question> questionArrayList;
    private TextView txtQuestion, txtOptionOne, txtOptionTwo, txtOptionThree, txtOptionFour;
    private Button btnSignIn;
    private int currentPosition;
    private int selectedOptionPosition;
    private Assessment assessment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment);
        init();
    }

    private void init() {
        sharedPreferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    private void getAssessment() {
        assessment = new Assessment();
        questionArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Constant.GET_ASSESSMENT, response -> {
            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("success")) {

                    JSONObject assessmentObject = object.getJSONObject("assessment");
                    JSONArray questionArray = new JSONArray(object.getString("questions"));



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
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }
}