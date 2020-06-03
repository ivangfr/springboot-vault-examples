package com.mycompany.movieservice.mapper;

import com.mycompany.movieservice.model.Movie;
import com.mycompany.movieservice.rest.dto.CreateMovieDto;
import com.mycompany.movieservice.rest.dto.MovieDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    Movie toMovie(CreateMovieDto createMovieDto);

    MovieDto toMovieDto(Movie movie);

}
