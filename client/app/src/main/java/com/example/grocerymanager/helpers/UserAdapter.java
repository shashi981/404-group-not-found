package com.example.grocerymanager.helpers;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.example.grocerymanager.R;
import com.example.grocerymanager.activities.ChatDieticianActivity;
import com.example.grocerymanager.models.UserData;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private List<UserData> userList;

    final static String TAG = "UserAdapter"; //identify where log is coming from
    private Context context;

    public UserAdapter(Context context, List<UserData> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(itemView, context, userList);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        UserData user = userList.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.userEmail.setText(user.getUserEmail());
        holder.bindUser(user);  // Associate the user with the ViewHolder
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView userName;
        public TextView userEmail;
        private UserData currentUser;
        private Context context;
        private List<UserData> userList;


        public UserViewHolder(View view, Context context, List<UserData> userList) {
            super(view);
            this.context = context;
            userName = view.findViewById(R.id.userName);
            userEmail = view.findViewById(R.id.userEmail);
            this.userList = userList;
            view.setOnClickListener(this);
        }

        public void bindUser(UserData user) {
            this.currentUser = user;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Log.d(TAG, "" + userList.get(position).getUID());
                Intent intent = new Intent(context, ChatDieticianActivity.class);
                intent.putExtra("selectedDietitianUID", userList.get(position).getUID());
                context.startActivity(intent);
            }
        }
    }
}
