package com.ddecker4.musicapp.service;

import com.ddecker4.musicapp.model.*;
import com.ddecker4.musicapp.repository.*;

import org.springframework.stereotype.Service; 
import java.util.stream.Collectors;
import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final PlaylistEntryRepository playlistEntryRepository;
    private final SongService songService;
    private final SongRepository songRepository;

    public PlaylistService(PlaylistRepository playlistRepository, PlaylistEntryRepository playlistEntryRepository, SongService songService, SongRepository songRepository) {
        this.playlistRepository = playlistRepository;
        this.playlistEntryRepository = playlistEntryRepository;
        this.songRepository = songRepository;
        this.songService = songService;
    }

    public List<PlaylistDTO> getAllPlaylists() {
        return playlistRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Optional<PlaylistDTO> getPlaylistById(Integer id) {
        return playlistRepository.findById(id).map(this::convertToDTO);
    }

    public PlaylistDTO savePlaylist(PlaylistDTO playlistDTO) throws IOException {
        Playlist playlist = new Playlist();
        playlist.setTitle(playlistDTO.title());
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    public PlaylistDTO updatePlaylist(Integer id, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow();
        playlist.setTitle(playlistDTO.title());
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(updatedPlaylist);
    }

    public void deletePlaylist(Integer id) {
        playlistRepository.deleteById(id);
    }

    // add song to playlist
    public PlaylistEntryDTO savePlaylistEntry(Integer playlist_id, PlaylistEntryDTO playlistEntryDTO) throws IOException {
        PlaylistEntry playlistEntry = new PlaylistEntry();
        Playlist playlist = playlistRepository.findById(playlist_id).orElseThrow();
        Song song = songRepository.findById(playlistEntryDTO.song_id()).orElseThrow();
        try {
            playlistEntry.setPosition(1 + playlistEntryRepository
                .findTop1ByPlaylistOrderByPositionDesc(playlist)
                .getPosition());
        } catch (Exception e) {
            playlistEntry.setPosition(1);
        }
        playlistEntry.setPlaylist(playlist);
        playlistEntry.setSong(song);
        PlaylistEntry savedPlaylistEntry = playlistEntryRepository.save(playlistEntry);
        return convertToDTO(savedPlaylistEntry);
    }

    public List<PlaylistEntryDTO> getAllPlaylistEntries(Integer playlist_id) {
        Playlist playlist;
        try {
            playlist = playlistRepository.findById(playlist_id).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<PlaylistEntryDTO>();
        }
        
        return playlistEntryRepository.findByPlaylistOrderByPositionAsc(playlist)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Optional<PlaylistEntryDTO> getPlaylistEntryById(Integer entry_id) {
        return playlistEntryRepository.findById(entry_id).map(this::convertToDTO);
    }

    // update song position in playlist
    public List<PlaylistEntryDTO> updateSongPosition(Integer entry_id, PlaylistEntryDTO playlistEntryDTO) {
        PlaylistEntry playlistEntry;
        try {
            playlistEntry = playlistEntryRepository.findById(entry_id).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<PlaylistEntryDTO>();
        }
        Playlist playlist = playlistEntry.getPlaylist();
        Integer newPosition;
        try {
            newPosition = playlistEntryRepository.findByPlaylistOrderByPositionAsc(playlist).get(playlistEntryDTO.position() - 1).getPosition();
        } catch (Exception e) {
            return new ArrayList<PlaylistEntryDTO>();
        }
        Integer oldPosition = playlistEntry.getPosition();
        Integer adjustment, lowerBound, upperBound;
        if (newPosition.compareTo(oldPosition) > 0) {
            adjustment = -1;
            lowerBound = oldPosition + 1;
            upperBound = newPosition;
        } else {
            adjustment = 1;
            lowerBound = newPosition;
            upperBound = oldPosition - 1;
        }
        List<PlaylistEntry> playlistEntries;
        if (newPosition.compareTo(oldPosition) == 0) {
            playlistEntries = new ArrayList<PlaylistEntry>();
        } else {
            playlistEntries = playlistEntryRepository.findPlaylistEntriesInRange(playlist, lowerBound, upperBound);
            playlistEntries.stream().forEach(entry -> entry.setPosition(entry.getPosition() + adjustment));
        } 
        List<PlaylistEntryDTO> savedPlaylistEntries = playlistEntries.stream()
            .map(entry -> playlistEntryRepository.save(entry))
            .map(entry -> convertToDTO(entry))
            .collect(Collectors.toList());
        playlistEntry.setPosition(newPosition);
        PlaylistEntry savedPlaylistEntry = playlistEntryRepository.save(playlistEntry);
        savedPlaylistEntries.add(0, convertToDTO(savedPlaylistEntry));
        return savedPlaylistEntries;
    }

    public void deletePlaylistEntry(Integer entry_id) {
        playlistEntryRepository.deleteById(entry_id);
    }

    private PlaylistDTO convertToDTO(Playlist playlist) {
        return new PlaylistDTO(playlist.getId(), playlist.getTitle());
    }
    
    private Playlist convertToEntity(PlaylistDTO playlistDTO) {
        Playlist playlist = new Playlist();
        playlist.setTitle(playlistDTO.title());
        return playlist;
    }

    private PlaylistEntryDTO convertToDTO(PlaylistEntry playlistEntry) {
        return new PlaylistEntryDTO(
            playlistEntry.getId(), 
            playlistEntry.getPosition(), 
            playlistEntry.getPlaylist().getId(), 
            playlistEntry.getSong().getId());
    }

    private PlaylistEntry convertToEntity(PlaylistEntryDTO playlistEntryDTO) {
        PlaylistEntry playlistEntry = new PlaylistEntry();
        playlistEntry.setPlaylist(playlistRepository.findById(playlistEntryDTO.playlist_id()).get());
        playlistEntry.setSong(songRepository.findById(playlistEntryDTO.song_id()).get());
        playlistEntry.setPosition(playlistEntryDTO.position());
        return playlistEntry;
    }

}