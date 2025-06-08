package com.example.itp4229m.api;

import com.example.itp4229m.model.Course;

import com.example.itp4229m.model.CourseCategory;
import com.example.itp4229m.model.Lesson;
import com.example.itp4229m.model.Review;
import com.example.itp4229m.model.ReviewRequest;

import java.util.List;

import com.example.itp4229m.model.Subject;

import retrofit2.Call;
import retrofit2.http.*;

public interface CourseApiService {
    // Get all subjects
    @GET("subjects/")
    Call<List<Subject>> getSubjects();

    // Get all categories
    @GET("categories/")
    Call<List<CourseCategory>> getCategories();

    // Get all courses - using the correct endpoint
    @GET("courses/")
    Call<List<Course>> getCourses();

    // Get course lessons
    @GET("courses/{courseId}/lessons/")
    Call<List<Lesson>> getCourseLessons(@Path("courseId") int courseId);

    // Get course reviews
    @GET("courses/{courseId}/reviews/")
    Call<List<Review>> getCourseReviews(@Path("courseId") int courseId);

    // Post course review
    @POST("courses/{courseId}/reviews/")
    Call<Review> postReview(
            @Path("courseId") int courseId,
            @Body ReviewRequest review
    );
}
