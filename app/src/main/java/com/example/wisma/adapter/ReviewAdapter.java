package com.example.wisma.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.wisma.R;
import com.example.wisma.model.ModelReview;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<ModelReview> reviewList;
    private OnItemClickListener listener;

    public ReviewAdapter(List<ModelReview> reviewList) {
        this.reviewList = reviewList;
    }

    public interface OnItemClickListener {
        void onItemClick(ModelReview review);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ModelReview review = reviewList.get(position);
        holder.bind(review);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(review);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvLocation;
        private TextView tvDescription;

        private ImageView recImage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvLocation = itemView.findViewById(R.id.tv_item_location);
            tvDescription = itemView.findViewById(R.id.tv_item_description);
            recImage = itemView.findViewById(R.id.recImage);
        }

        public void bind(ModelReview review) {
            tvTitle.setText(review.getAuthor());
            tvLocation.setText(review.getLocation());
            tvDescription.setText(review.getDescription());


            Glide.with(itemView).load(review.getImgURL()).into(recImage);
        }
    }
}