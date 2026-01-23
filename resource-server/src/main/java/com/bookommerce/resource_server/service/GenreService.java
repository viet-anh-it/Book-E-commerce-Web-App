package com.bookommerce.resource_server.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookommerce.resource_server.dto.mapper.GenreMapper;
import com.bookommerce.resource_server.dto.request.CreateGenreRequestDto;
import com.bookommerce.resource_server.entity.Genre;
import com.bookommerce.resource_server.repository.GenreRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class GenreService {

    GenreRepository genreRepository;
    GenreMapper genreMapper;

    @Transactional
    public void createGenre(CreateGenreRequestDto createGenreRequestDto) {
        if (this.genreRepository.existsByName(createGenreRequestDto.getName())) {
            throw new RuntimeException("Genre already exists");
        }
        Genre genre = this.genreMapper.toGenre(createGenreRequestDto);
        this.genreRepository.save(genre);
    }

    @Cacheable("genres")
    public List<Genre> getAllGenres() {
        return this.genreRepository.findAll();
    }
}
