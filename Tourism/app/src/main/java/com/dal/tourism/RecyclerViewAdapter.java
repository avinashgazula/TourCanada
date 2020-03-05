package com.dal.tourism;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.ArrayList;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> implements Serializable {

    private ArrayList<String> mLocations;
    private Context mContext;

    public RecyclerViewAdapter(ArrayList<String> mLocations, Context mContext) {
        this.mLocations = mLocations;
        this.mContext = mContext;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_location_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txt_locationName.setText(mLocations.get(position));

        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast toast = Toast.makeText(mContext, holder.txt_locationName.getText().toString(), Toast.LENGTH_SHORT);
                toast.show();
                Intent intent = new Intent(mContext, ViewDestinationsActivity.class);
                intent.putExtra("location", holder.txt_locationName.getText().toString());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return mLocations.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView txt_locationName;
        RelativeLayout parent_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_locationName = itemView.findViewById(R.id.txt_locationName);
            parent_layout = itemView.findViewById(R.id.parent_layout);
        }
    }

}
