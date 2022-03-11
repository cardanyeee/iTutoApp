package com.ituto.android.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.ituto.android.Constant;
import com.ituto.android.ConversationActivity;
import com.ituto.android.Fragments.TutorProfileFragment;
import com.ituto.android.HomeActivity;
import com.ituto.android.Models.Message;
import com.ituto.android.Models.Tutor;
import com.ituto.android.Models.User;
import com.ituto.android.R;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class TutorsAdapter extends RecyclerView.Adapter<TutorsAdapter.TutorHolder> {
    private Context context;
    private ArrayList<Tutor> tutorArrayList;
    private SharedPreferences sharedPreferences;
    private Tutor tutor;

    public TutorsAdapter(Context context, ArrayList<Tutor> tutorArrayList) {
        this.context = context;
        this.tutorArrayList = tutorArrayList;
        sharedPreferences = context.getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public TutorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tutor, parent, false);
        return new TutorHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TutorHolder holder, @SuppressLint("RecyclerView") int position) {
        tutor = tutorArrayList.get(position);
        Picasso.get().load(tutor.getAvatar()).resize(500, 0).into(holder.imgTutor);
        holder.txtName.setText(tutor.getFirstname() + " " + tutor.getLastname());

        holder.btnMessage.setOnClickListener(v -> message(holder, position));

        holder.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TutorProfileFragment tutorProfileFragment = new TutorProfileFragment();
                ((HomeActivity)context).getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in,  // enter
                        R.anim.fade_out,  // exit
                        R.anim.fade_in,   // popEnter
                        R.anim.slide_out  // popExit
                ).replace(R.id.fragment_container, tutorProfileFragment).addToBackStack(null).commit();
            }
        });

    }

    private void message(TutorHolder holder, int position) {
        StringRequest request = new StringRequest(Request.Method.POST, Constant.CONVERSATION, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject conversationObject = object.getJSONObject("conversation");

                    Intent i = new Intent(((HomeActivity)context), ConversationActivity.class);
                    i.putExtra("conversationID", conversationObject.getString("_id"));
                    i.putExtra("name", tutorArrayList.get(position).getFirstname() + " " + tutorArrayList.get(position).getLastname());
                    i.putExtra("avatar", tutorArrayList.get(position).getAvatar());
                    i.putExtra("users", conversationObject.getJSONArray("users").toString());
                    context.startActivity(i);
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

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("userId", tutorArrayList.get(position).getUserID());
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(holder.itemView.getContext());
        queue.add(request);
    }

    class TutorHolder extends RecyclerView.ViewHolder {

        private TextView txtName, txtCourse;
        private CircleImageView imgTutor;
        private Button btnProfile, btnMessage;

        public TutorHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCourse = itemView.findViewById(R.id.txtCourse);
            imgTutor = itemView.findViewById(R.id.imgTutor);
            btnProfile = itemView.findViewById(R.id.btnProfile);
            btnMessage = itemView.findViewById(R.id.btnMessage);

            itemView.setClickable(true);
        }

    }


    @Override
    public int getItemCount() {
        return tutorArrayList.size();
    }
}
