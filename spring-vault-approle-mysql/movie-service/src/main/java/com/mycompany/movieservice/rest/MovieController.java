package com.mycompany.movieservice.rest;

import com.mycompany.movieservice.mapper.MovieMapper;
import com.mycompany.movieservice.model.Movie;
import com.mycompany.movieservice.rest.dto.CreateMovieDto;
import com.mycompany.movieservice.rest.dto.MovieDto;
import com.mycompany.movieservice.service.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;
    private final Environment environment;
    private final MovieMapper movieMapper;

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        return String.format("%s/%s",
                environment.getProperty("datasource.username"),
                environment.getProperty("datasource.password"));
    }

    @GetMapping("/secretMessage")
    public String getSecretMessage() {
        return environment.getProperty("secret.movie-service.message");
    }

    @GetMapping
    public List<MovieDto> getMovies() {
        return movieService.getMovies()
                .stream()
                .map(movieMapper::toMovieDto)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieDto createMovie(@Valid @RequestBody CreateMovieDto createMovieDto) {
        Movie movie = movieMapper.toMovie(createMovieDto);
        movie = movieService.saveMovie(movie);
        return movieMapper.toMovieDto(movie);
    }

}
