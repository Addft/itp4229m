package com.example.itp4229m.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itp4229m.R;
import com.example.itp4229m.model.Course;
import com.example.itp4229m.util.ImageLoader;

import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {
    private static final String TAG = "CourseAdapter";
    private List<Course> courseList;
    private Context context;
    private OnCourseClickListener listener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public CourseAdapter(Context context, List<Course> courseList, OnCourseClickListener listener) {
        this.context = context;
        this.courseList = courseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        try {
            Course course = courseList.get(position);
            holder.bind(course);
        } catch (Exception e) {
            Log.e(TAG, "Error binding course at position " + position, e);
            holder.setDefaults();
        }
    }

    @Override
    public int getItemCount() {
        return courseList == null ? 0 : courseList.size();
    }

    public void updateCourses(List<Course> courses) {
        this.courseList = courses;
        notifyDataSetChanged();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnailImageView;
        private TextView titleTextView;
        private TextView categoryTextView;
        private RatingBar ratingBar;
        private TextView ratingCountTextView;
        private TextView priceTextView;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImageView = itemView.findViewById(R.id.course_thumbnail);
            titleTextView = itemView.findViewById(R.id.course_title);
            categoryTextView = itemView.findViewById(R.id.course_category);
            ratingBar = itemView.findViewById(R.id.course_rating);
            ratingCountTextView = itemView.findViewById(R.id.rating_count);
            priceTextView = itemView.findViewById(R.id.course_price);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null && courseList != null) {
                    listener.onCourseClick(courseList.get(position));
                }
            });
        }

        public void setDefaults() {
            titleTextView.setText("Unknown Course");
            categoryTextView.setText("Unknown Category");
            ratingBar.setRating(0);
            ratingCountTextView.setText("(0)");
            priceTextView.setText("N/A");
            thumbnailImageView.setImageResource(R.drawable.ic_placeholder);
        }

        public void bind(Course course) {
            try {
                // Handle null course
                if (course == null) {
                    setDefaults();
                    return;
                }

                // Set title with null check
                titleTextView.setText(course.getTitle() != null ? course.getTitle() : "Untitled Course");

                // Set category with null checks
                if (course.getCategory() != null && course.getCategory().getName() != null) {
                    categoryTextView.setText(course.getCategory().getName());
                } else {
                    categoryTextView.setText("Uncategorized");
                }

                // Set rating with null checks
                if (course.getAverage_rating() != null) {
                    try {
                        ratingBar.setRating(course.getAverage_rating().floatValue());
                    } catch (Exception e) {
                        Log.e(TAG, "Error setting rating", e);
                        ratingBar.setRating(0);
                    }
                    ratingCountTextView.setText(String.format("(%d)", course.getRating_count()));
                } else {
                    ratingBar.setRating(0);
                    ratingCountTextView.setText("(0)");
                }

                // Set price
                try {
                    if (course.isIs_free()) {
                        priceTextView.setText("Free");
                    } else {
                        priceTextView.setText(String.format("$%.2f", course.getPrice()));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error formatting price", e);
                    priceTextView.setText("N/A");
                }

                // Load thumbnail image with Picasso
                try {
                    String thumbnailUrl = course.getThumbnail();
                    ImageLoader.loadImage(context, thumbnailUrl, thumbnailImageView);
                } catch (Exception e) {
                    Log.e(TAG, "Error loading thumbnail", e);
                    thumbnailImageView.setImageResource(R.drawable.ic_placeholder);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error in bind()", e);
                setDefaults();
            }
        }
    }
}