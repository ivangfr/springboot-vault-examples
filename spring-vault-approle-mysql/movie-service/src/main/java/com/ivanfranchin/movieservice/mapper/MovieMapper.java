package com.ivanfranchin.movieservice.mapper;

import com.ivanfranchin.movieservice.rest.dto.MovieResponse;
import com.ivanfranchin.movieservice.model.Movie;
import com.ivanfranchin.movieservice.rest.dto.CreateMovieRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    @Mapping(target = "id", ignore = true)
    Movie toMovie(CreateMovieRequest createMovieRequest);

    MovieResponse toMovieResponse(Movie movie);
}
