package com.ddecker4.musicapp.controller;

import com.ddecker4.musicapp.service.*;
import com.ddecker4.musicapp.model.*;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {
    
    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable("id") Integer id) {
        Optional<PlaylistDTO> playlistDTO = playlistService.getPlaylistById(id);
        try {
            return ResponseEntity.ok(playlistDTO.orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public PlaylistDTO createPlaylist(@RequestBody PlaylistDTO playlistDTO) throws IOException {
        return playlistService.savePlaylist(playlistDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable("id") Integer id) {
        playlistService.deletePlaylist(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable("id") Integer id, @RequestBody PlaylistDTO playlistDTO) {
        try {
            PlaylistDTO updatedPlaylist = playlistService.updatePlaylist(id, playlistDTO);
            return ResponseEntity.ok(updatedPlaylist);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{playlist_id}/entries")
    public PlaylistEntryDTO addToPlaylist(@PathVariable("playlist_id") Integer playlist_id, @RequestBody PlaylistEntryDTO playlistEntryDTO) throws IOException {
        return playlistService.savePlaylistEntry(playlist_id, playlistEntryDTO);
    }

    @GetMapping("/{playlist_id}/entries")
    public List<PlaylistEntryDTO> getAllPlaylistEntries(@PathVariable("playlist_id") Integer playlist_id) {
        return playlistService.getAllPlaylistEntries(playlist_id);
    }

    @GetMapping("/entries/{entry_id}")
    public ResponseEntity<PlaylistEntryDTO> getPlaylistEntryById(@PathVariable("entry_id") Integer entry_id) {
        Optional<PlaylistEntryDTO> playlistEntryDTO = playlistService.getPlaylistEntryById(entry_id);
        try {
            return ResponseEntity.ok(playlistEntryDTO.orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    } 

    @PutMapping("/entries/{entry_id}")
    public List<PlaylistEntryDTO> updateSongPosition(@PathVariable("entry_id") Integer entry_id, @RequestBody PlaylistEntryDTO playlistEntryDTO) {
        return playlistService.updateSongPosition(entry_id, playlistEntryDTO);
    }

    @DeleteMapping("/entries/{entry_id}")
    public ResponseEntity<Void> deletePlaylistEntry(@PathVariable("entry_id") Integer entry_id) {
        playlistService.deletePlaylistEntry(entry_id);
        return ResponseEntity.noContent().build();
    }

}