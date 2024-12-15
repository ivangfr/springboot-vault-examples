package com.ivanfranchin.movieservice.rest;

import com.ivanfranchin.movieservice.model.Movie;
import com.ivanfranchin.movieservice.repository.MovieRepository;
import com.ivanfranchin.movieservice.rest.dto.CreateMovieRequest;
import com.ivanfranchin.movieservice.rest.dto.MovieResponse;
import jakarta.validation.Valid;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final Environment environment;

    public MovieController(MovieRepository movieRepository, Environment environment) {
        this.movieRepository = movieRepository;
        this.environment = environment;
    }

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
    public List<MovieResponse> getMovies() {
        return movieRepository.findAll()
                .stream()
                .map(this::toMovieResponse)
                .toList();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieResponse createMovie(@Valid @RequestBody CreateMovieRequest createMovieRequest) {
        Movie movie = movieRepository.save(toMovie(createMovieRequest));
        return toMovieResponse(movie);
    }

    private Movie toMovie(CreateMovieRequest createMovieRequest) {
        return new Movie(createMovieRequest.title());
    }

    private MovieResponse toMovieResponse(Movie movie) {
        return new MovieResponse(movie.getId(), movie.getTitle());
    }
}
