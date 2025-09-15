package com.ddecker4.musicapp.repository;

import com.ddecker4.musicapp.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongRepository extends JpaRepository<Song, Integer> {
}