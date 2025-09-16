package com.ddecker4.musicapp.repository;

import com.ddecker4.musicapp.model.Playlist;
import com.ddecker4.musicapp.model.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, Integer> {
    // get all entries in a playlist, ordered by increasing position
    List<PlaylistEntry> findByPlaylistOrderByPositionAsc(Playlist playlist);

    // get all entries in specified playlist whose positions are within a specified range (inclusive)
    @Query(value = "SELECT pe FROM PlaylistEntry pe WHERE pe.playlist = :playlist AND pe.position >= :position1 AND pe.position <= :position2")
    List<PlaylistEntry> findPlaylistEntriesInRange(
        @Param("playlist") Playlist playlist,
        @Param("position1") Integer position1,
        @Param("position2") Integer position2);

    // get entry in specified playlist with greatest position
    PlaylistEntry findTop1ByPlaylistOrderByPositionDesc(Playlist playlist);

}