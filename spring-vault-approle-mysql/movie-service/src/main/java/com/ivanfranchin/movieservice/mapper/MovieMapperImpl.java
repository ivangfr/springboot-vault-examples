package com.ivanfranchin.movieservice.mapper;

import com.ivanfranchin.movieservice.model.Movie;
import com.ivanfranchin.movieservice.rest.dto.CreateMovieRequest;
import com.ivanfranchin.movieservice.rest.dto.MovieResponse;
import org.springframework.stereotype.Service;

@Service
public class MovieMapperImpl implements MovieMapper {

    @Override
    public Movie toMovie(CreateMovieRequest createMovieRequest) {
        if (createMovieRequest == null) {
            return null;
        }
        Movie movie = new Movie();
        movie.setTitle(createMovieRequest.getTitle());
        return movie;
    }

    @Override
    public MovieResponse toMovieResponse(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieResponse(movie.getId(), movie.getTitle());
    }
}
