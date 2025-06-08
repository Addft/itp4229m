package com.example.itp4229m;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.itp4229m.adapter.CourseAdapter;
import com.example.itp4229m.api.CourseRepository;
import com.example.itp4229m.model.Course;
import com.example.itp4229m.model.Review;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CourseAdapter.OnCourseClickListener {
    private static final String TAG = "MainActivity";
    private CourseRepository repository;
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private ProgressBar progressBar;
    private LinearLayout errorContainer;
    private TextView errorMessage;
    private Button retryButton;
    private List<Course> courses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Set up window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        coursesRecyclerView = findViewById(R.id.courses_recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        errorContainer = findViewById(R.id.error_container);
        errorMessage = findViewById(R.id.error_message);
        retryButton = findViewById(R.id.retry_button);

        // Set up retry button
        retryButton.setOnClickListener(v -> loadCourses());

        // Set up RecyclerView
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        courseAdapter = new CourseAdapter(this, courses, this);
        coursesRecyclerView.setAdapter(courseAdapter);

        // Initialize repository and load courses
        repository = new CourseRepository();
        loadCourses();
    }

    private void loadCourses() {
        // Show progress and hide error message
        progressBar.setVisibility(View.VISIBLE);
        errorContainer.setVisibility(View.GONE);
        coursesRecyclerView.setVisibility(View.GONE);

        Log.d(TAG, "Starting to load courses...");

        new Thread(() -> {
            try {
                List<Course> loadedCourses = repository.getCourses();

                // Update UI on main thread
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);

                    if (loadedCourses != null && !loadedCourses.isEmpty()) {
                        Log.d(TAG, "Courses loaded successfully: " + loadedCourses.size() + " items");
                        courses.clear();
                        courses.addAll(loadedCourses);
                        courseAdapter.updateCourses(courses);
                        coursesRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        Log.e(TAG, "Failed to load courses or empty list returned");
                        errorMessage.setText("Failed to load courses. Please check your server connection and try again.");
                        errorContainer.setVisibility(View.VISIBLE);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Exception while loading courses", e);
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    errorMessage.setText("Error: " + e.getMessage());
                    errorContainer.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    @Override
    public void onCourseClick(Course course) {
        // Handle course click event
        Toast.makeText(this, "Selected: " + course.getTitle(), Toast.LENGTH_SHORT).show();
        // You can start a new activity with course details here
    }

    private void submitReview(int courseId, int rating, String comment) {
        new Thread(() -> {
            Review review = repository.postReview(courseId, rating, comment);

            runOnUiThread(() -> {
                if (review != null) {
                    Toast.makeText(this, "Review submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }
}

