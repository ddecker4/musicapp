package com.ddecker4.musicapp.repository;

import com.ddecker4.musicapp.model.Playlist;
import com.ddecker4.musicapp.model.PlaylistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PlaylistEntryRepository extends JpaRepository<PlaylistEntry, Integer> {
    
    List<PlaylistEntry> findByPlaylistOrderByPositionAsc(Playlist playlist);

    @Query(value = "SELECT pe FROM PlaylistEntry pe WHERE pe.playlist = :playlist AND pe.position = :position")
    PlaylistEntry findByPlaylistAndPosition(@Param("playlist") Playlist playlist, @Param("position") Integer position);

    @Query(value = "SELECT pe FROM PlaylistEntry pe WHERE pe.playlist = :playlist AND pe.position >= :position1 AND pe.position <= :position2")
    List<PlaylistEntry> findPlaylistEntriesInRange(
        @Param("playlist") Playlist playlist,
        @Param("position1") Integer position1,
        @Param("position2") Integer position2);


    PlaylistEntry findTop1ByPlaylistOrderByPositionDesc(Playlist playlist);

/*
    @Query(value = "SELECT pe FROM PlaylistEntry pe WHERE pe.playlist = :playlist AND pe.position >= :new_position AND pe.position < :old_position")
    List<PlaylistEntry> getPlaylistEntriesAfterPosition(
        @Param("playlist") Playlist playlist, 
        @Param("new_position") Integer new_position, 
        @Param("old_position") Integer old_position);
*/
}