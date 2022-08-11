package com.ivanfranchin.movieservice.service;

import com.ivanfranchin.movieservice.model.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getMovies();

    Movie saveMovie(Movie movie);
}
