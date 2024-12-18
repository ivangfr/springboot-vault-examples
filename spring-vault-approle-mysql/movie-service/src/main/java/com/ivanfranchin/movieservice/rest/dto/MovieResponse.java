package com.ivanfranchin.movieservice.rest.dto;

import com.ivanfranchin.movieservice.model.Movie;

public record MovieResponse(Long id, String title) {

    public static MovieResponse from(Movie movie) {
        return new MovieResponse(movie.getId(), movie.getTitle());
    }
}
