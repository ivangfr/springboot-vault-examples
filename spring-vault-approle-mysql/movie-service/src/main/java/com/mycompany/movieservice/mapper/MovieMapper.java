package com.mycompany.movieservice.mapper;

import com.mycompany.movieservice.model.Movie;
import com.mycompany.movieservice.rest.dto.CreateMovieRequest;
import com.mycompany.movieservice.rest.dto.MovieResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(CreateMovieRequest createMovieRequest);

    MovieResponse toMovieResponse(Movie movie);
}
