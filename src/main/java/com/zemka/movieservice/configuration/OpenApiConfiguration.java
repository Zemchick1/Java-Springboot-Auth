package com.zemka.movieservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Movie Service using OMDB Database",
                description = "The Movie Service is a web-based application that leverages the OMDB (Open Movie Database) " +
                        "to provide comprehensive information about movies and related content. This service allows users " +
                        "to search for movies, retrieve details about specific films, and access a wealth of data related " +
                        "to the world of cinema.",
                contact = @Contact(
                        name = "Fam Dyk An",
                        url = "", // TODO
                        email = "zemchickpro@gmail.com"
                ))
)
public class OpenApiConfiguration {
}
