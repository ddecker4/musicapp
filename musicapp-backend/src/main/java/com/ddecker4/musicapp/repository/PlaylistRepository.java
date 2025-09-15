package com.ddecker4.musicapp.repository;

import com.ddecker4.musicapp.model.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
}