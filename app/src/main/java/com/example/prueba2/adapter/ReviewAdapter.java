package com.example.prueba2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prueba2.R;
import com.example.prueba2.model.Review;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ReviewAdapter extends FirebaseRecyclerAdapter<Review, ReviewAdapter.ReviewViewHolder> {
    /*private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }
*/
    public ReviewAdapter(@NonNull FirebaseRecyclerOptions<Review> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {
        holder.title.setText(model.getTitle());
        holder.description.setText(model.getDescription());
        holder.ratingBar.setRating(model.getRating());
        holder.restaurantname.setText(model.getRestaurantname());
        holder.username.setText(model.getUsername());
      //  holder.rating.setText(String.valueOf(model.getRating()));
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_card, parent, false);
        return new ReviewViewHolder(view);
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, restaurantname, username;
        RatingBar ratingBar;
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textViewTitle);
            description = itemView.findViewById(R.id.textViewDescription);
          //  rating = itemView.findViewById(R.id.textViewRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            restaurantname=  itemView.findViewById(R.id.textViewNameRes);
            username = itemView.findViewById(R.id.textViewUsername);
        }
    }
}
