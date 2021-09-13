package com.mycompany.movieservice.service;

import com.mycompany.movieservice.model.Movie;

import java.util.List;

public interface MovieService {

    List<Movie> getMovies();

    Movie saveMovie(Movie movie);
}
