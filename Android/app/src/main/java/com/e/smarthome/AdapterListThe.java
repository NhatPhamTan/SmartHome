package com.e.smarthome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdapterListThe extends RecyclerView.Adapter<AdapterListThe.ViewHolder> {
    int count=0;
    private LayoutInflater layoutInflater;
    AdapterListThe(Context context, int count)
    {
        this.count = count;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public AdapterListThe.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.carditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterListThe.ViewHolder holder, int position) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    int j=0;
                    try {
                        if (j==position) break;
                    long a = Long.parseLong(dataSnapshot.getKey());
                    holder.cardID.setText(dataSnapshot.getKey());
                    holder.cardName.setText(dataSnapshot.getChildren().toString());
                    j++;
                }
                catch (NumberFormatException e)
                {
                    break;
                }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {

        return count;
    }
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView cardID = itemView.findViewById(R.id.txt_cardid);
        TextView cardName= itemView.findViewById(R.id.txt_cardname);
        Button delCard = itemView.findViewById(R.id.btn_deletecard);
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
