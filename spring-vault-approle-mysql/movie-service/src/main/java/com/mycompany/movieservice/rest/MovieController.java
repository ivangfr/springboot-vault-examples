package com.mycompany.movieservice.rest;

import com.mycompany.movieservice.model.Movie;
import com.mycompany.movieservice.rest.dto.CreateMovieDto;
import com.mycompany.movieservice.rest.dto.MovieDto;
import com.mycompany.movieservice.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@RestController
@RequestMapping("/api/movies")
public class MovieController {

    @Value("${database.username}")
    private String username;

    @Value("${database.password}")
    private String password;

    private final MovieService movieService;
    private final MapperFacade mapperFacade;

    public MovieController(MovieService movieService, MapperFacade mapperFacade) {
        this.movieService = movieService;
        this.mapperFacade = mapperFacade;
    }

    @GetMapping("/dbcredentials")
    public String getDBCredentials() {
        log.info("Get database credentials");

        return String.format("%s/%s", username, password);
    }

    @GetMapping
    public List<MovieDto> getMovies() {
        log.info("Get movies");

        return movieService.getMovies()
                .stream()
                .map(student -> mapperFacade.map(student, MovieDto.class))
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public MovieDto createMovie(@Valid @RequestBody CreateMovieDto createMovieDto) {
        log.info("Post request to create movie: {}", createMovieDto);

        Movie movie = mapperFacade.map(createMovieDto, Movie.class);
        movie = movieService.saveMovie(movie);

        return mapperFacade.map(movie, MovieDto.class);
    }

}
