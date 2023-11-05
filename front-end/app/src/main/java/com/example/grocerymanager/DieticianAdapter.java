
package com.example.grocerymanager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class DieticianAdapter extends RecyclerView.Adapter<DieticianAdapter.DieticianViewHolder> {

    private static List<DietitianData> dieticianList;
    final static String TAG = "DieticianAdapter"; //identify where log is coming from
    private Context context;

    public DieticianAdapter(Context context, List<DietitianData> dieticianList) {
        this.context = context;
        this.dieticianList = dieticianList;
    }

    @NonNull
    @Override
    public DieticianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dietician, parent, false);
        return new DieticianViewHolder(itemView, context);
    }

    @Override
    public void onBindViewHolder(@NonNull DieticianViewHolder holder, int position) {
        DietitianData dietician = dieticianList.get(position);
        holder.dieticianName.setText(dietician.getFirstName() + " " + dietician.getLastName());
        holder.dieticianEmail.setText(dietician.getDietitianEmail());
        holder.bindDietician(dietician);  // Associate the dietician with the ViewHolder
    }

    @Override
    public int getItemCount() {
        return dieticianList.size();
    }

    public static class DieticianViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView dieticianName;
        public TextView dieticianEmail;
        private DietitianData currentDietician;
        private Context context;

        public DieticianViewHolder(View view, Context context) {
            super(view);
            this.context = context;
            dieticianName = view.findViewById(R.id.dieticianName);
            dieticianEmail = view.findViewById(R.id.dieticianEmail);
            view.setOnClickListener(this);
        }

        public void bindDietician(DietitianData dietician) {

            this.currentDietician = dietician;
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(context, ChatUserActivity.class);
                Log.d(TAG,""+ dieticianList.get(position).getDID());
                intent.putExtra("selectedDietitianDID", dieticianList.get(position).getDID());
                context.startActivity(intent);
            }
        }
    }
}
