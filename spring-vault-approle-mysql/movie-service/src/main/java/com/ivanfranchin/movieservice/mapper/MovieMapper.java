package com.ivanfranchin.movieservice.mapper;

import com.ivanfranchin.movieservice.model.Movie;
import com.ivanfranchin.movieservice.rest.dto.CreateMovieRequest;
import com.ivanfranchin.movieservice.rest.dto.MovieResponse;

public interface MovieMapper {

    Movie toMovie(CreateMovieRequest createMovieRequest);

    MovieResponse toMovieResponse(Movie movie);
}
