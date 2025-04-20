package com.example.prueba2.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.prueba2.R;
import com.example.prueba2.model.Restaurant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.util.ArrayList;

public class RestaurantAdapter extends FirebaseRecyclerAdapter<Restaurant, RestaurantAdapter.resviewHolder> {
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    public RestaurantAdapter(@NonNull FirebaseRecyclerOptions<Restaurant> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull resviewHolder holder, int position, @NonNull Restaurant model) {
        holder.name.setText(model.getName());
        holder.phone.setText(model.getPhone());
        holder.address.setText(model.getAddress());
        holder.category.setText(model.getCategory());
        holder.averageRating.setRating(model.getAverageRating());

        // Carga la imagen del restaurante utilizando Glide
        Glide.with(holder.itemView.getContext())
                .load(model.getPhotoUrl())
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int clickedPosition;
                if (mListener != null) {
                    int adapterPosition = holder.getBindingAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        clickedPosition = adapterPosition;
                    } else {

                        return;
                    }
                    mListener.onItemClick(clickedPosition);

                }
            }

        });
    }

    public int getItemCount() {
        return super.getItemCount();
    }


    @NonNull
    @Override
    public resviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_card, parent, false);
        return new resviewHolder(view);
    }

    static class resviewHolder extends RecyclerView.ViewHolder {

        TextView name, address, category, phone;
        RatingBar averageRating;
        ImageView imageView;

        public resviewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.textViewName);
            address = itemView.findViewById(R.id.textViewAddress);
            category = itemView.findViewById(R.id.textViewCategory);
            phone = itemView.findViewById(R.id.textViewPhone);
            imageView = itemView.findViewById(R.id.imageViewPic);
            averageRating = itemView.findViewById(R.id.ratingBarAvg);
        }
    }

}