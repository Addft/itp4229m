package com.example.itp4229m.api;

import android.util.Log;

import com.example.itp4229m.model.Course;

import com.example.itp4229m.model.Review;
import com.example.itp4229m.model.ReviewRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CourseRepository {
    private static final String TAG = "CourseRepository";
    private CourseApiService apiService;

    public CourseRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public List<Course> getCourses() {
        try {
            Log.d(TAG, "Fetching courses...");
            retrofit2.Response<List<Course>> response = apiService.getCourses().execute();

            if (response.isSuccessful()) {
                List<Course> courses = response.body();
                if (courses != null) {
                    Log.d(TAG, "Courses fetched successfully: " + courses.size() + " items");
                    for (Course course : courses) {
                        Log.d(TAG, "Course: " + course.getTitle());
                    }
                    return courses;
                } else {
                    Log.e(TAG, "Courses list is null");
                }
            } else {
                Log.e(TAG, "Error fetching courses: " + response.code() + " " + response.message());
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error body: " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading error body", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception when fetching courses", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error", e);
        }
        return new ArrayList<>();
    }

    public Review postReview(int courseId, int rating, String comment) {
        try {
            Log.d(TAG, "Posting review for course " + courseId);
            retrofit2.Response<Review> response = apiService.postReview(
                    courseId,
                    new ReviewRequest(rating, comment)
            ).execute();

            if (response.isSuccessful()) {
                Log.d(TAG, "Review posted successfully");
                return response.body();
            } else {
                Log.e(TAG, "Error posting review: " + response.code() + " " + response.message());
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, "Error body: " + errorBody);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error reading error body", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Exception when posting review", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error when posting review", e);
        }
        return null;
    }
}

