package com.ivanfranchin.movieservice.movie.dto;

import com.ivanfranchin.movieservice.movie.model.Movie;

public record MovieResponse(Long id, String title) {

    public static MovieResponse from(Movie movie) {
        return new MovieResponse(movie.getId(), movie.getTitle());
    }
}
