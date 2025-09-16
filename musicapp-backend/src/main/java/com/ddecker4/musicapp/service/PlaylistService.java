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

    // get all playlists in database
    // Parameters : none
    // Return : List of Playlist DTOs representing each Playlist in stored in the database
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // get playlist with specified id, if it exists
    // Parameter playlist_id : id of Playlist being searched for
    // Return : Playlist DTO representing the desired playlist or null if it does not exist
    public Optional<PlaylistDTO> getPlaylistById(Integer playlist_id) {
        return playlistRepository.findById(playlist_id).map(this::convertToDTO);
    }

    // save new playlist to the database
    // Parameter playlistDTO : Playlist DTO containing the title of the new playlist
    // Return : Playlist DTO representing the newly created playlist
    public PlaylistDTO savePlaylist(PlaylistDTO playlistDTO) throws IOException {
        Playlist playlist = new Playlist();
        playlist.setTitle(playlistDTO.title());
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(savedPlaylist);
    }

    // update title of playlist and save changes to database
    // Parameter playlist_id : id of playlist whose title is being updated
    // Parameter playlistDTO : Playlist DTO containing new playlist title
    // Return : Playlist DTO representing updated playlist
    public PlaylistDTO updatePlaylist(Integer playlist_id, PlaylistDTO playlistDTO) {
        Playlist playlist = playlistRepository.findById(playlist_id).orElseThrow();
        playlist.setTitle(playlistDTO.title());
        Playlist updatedPlaylist = playlistRepository.save(playlist);
        return convertToDTO(updatedPlaylist);
    }

    // delete playlist with specified id from database
    // Parameter playlist_id : id of playlist to be deleted
    // Return : none
    public void deletePlaylist(Integer playlist_id) {
        playlistRepository.deleteById(playlist_id);
    }

    // add song to playlist
    // Parameter playlist_id : id of playlist being added to
    // Parameter playlistEntryDTO : playlist entry DTO containing id of song being added
    // Return : playlist entry DTO representing new item in PlaylistEntry table
    public PlaylistEntryDTO savePlaylistEntry(Integer playlist_id, PlaylistEntryDTO playlistEntryDTO) throws IOException {
        PlaylistEntry playlistEntry = new PlaylistEntry();
        Playlist playlist = playlistRepository.findById(playlist_id).orElseThrow();
        Song song = songRepository.findById(playlistEntryDTO.song_id()).orElseThrow();
        try { // ensure position field of new entry is the greatest value in the playlist
            playlistEntry.setPosition(1 + playlistEntryRepository
                .findTop1ByPlaylistOrderByPositionDesc(playlist)
                .getPosition());
        } catch (Exception e) { // set position of new entry to 1 if playlist is empty
            playlistEntry.setPosition(1);
        }
        playlistEntry.setPlaylist(playlist);
        playlistEntry.setSong(song);
        PlaylistEntry savedPlaylistEntry = playlistEntryRepository.save(playlistEntry);
        return convertToDTO(savedPlaylistEntry);
    }

    // get all playlist entries belonging to specified playlist
    // Parameter playlist_id : id of playlist to get associated entries of
    // Return : list of playlist entry DTOs representing all entries in the specified playlist, returned in order by position in playlist
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

    // get playlist entry with specified id
    // Parameter entry_id : id of playlist entry to be retrieved
    // Return : if entry with entry_id exists, a playlist entry DTO reprenting that entry, otherwise null
    public Optional<PlaylistEntryDTO> getPlaylistEntryById(Integer entry_id) {
        return playlistEntryRepository.findById(entry_id).map(this::convertToDTO);
    }

    // update song position in playlist
    // given new position value n, updates the position fields of playlist entries as necessary 
    // so that the specified entry falls at index n (1-indexed) when the playlist entries are sorted
    // by position value in ascending order
    // Parameter entry_id : id of playlist entry to update the position of
    // Parameter playlistEntryDTO : playlist entry DTO containing new position value n
    // Return : list of playlist entry DTOs representing all the entries that were updated
    public List<PlaylistEntryDTO> updateSongPosition(Integer entry_id, PlaylistEntryDTO playlistEntryDTO) {
        PlaylistEntry playlistEntry;
        try {
            playlistEntry = playlistEntryRepository.findById(entry_id).orElseThrow();
        } catch (Exception e) {
            return new ArrayList<PlaylistEntryDTO>();
        }

        Playlist playlist = playlistEntry.getPlaylist();

        // determine position value that corresponds to track #n in the playlist
        Integer newPosition;
        try {
            newPosition = playlistEntryRepository.findByPlaylistOrderByPositionAsc(playlist)
                .get(playlistEntryDTO.position() - 1)
                .getPosition();
        } catch (Exception e) {
            return new ArrayList<PlaylistEntryDTO>();
        }

        Integer oldPosition = playlistEntry.getPosition();

        // determine which entries must also be updated and how their position should be updated
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

        // get all other entries that need to be updated and alter position accordingly
        List<PlaylistEntry> playlistEntries;
        if (newPosition.compareTo(oldPosition) == 0) {
            playlistEntries = new ArrayList<PlaylistEntry>();
        } else {
            playlistEntries = playlistEntryRepository.findPlaylistEntriesInRange(playlist, lowerBound, upperBound);
            playlistEntries.stream().forEach(entry -> entry.setPosition(entry.getPosition() + adjustment));
        } 

        // save changes to db
        List<PlaylistEntryDTO> savedPlaylistEntries = playlistEntries.stream()
            .map(entry -> playlistEntryRepository.save(entry))
            .map(entry -> convertToDTO(entry))
            .collect(Collectors.toList());

        // update and save target entry position 
        playlistEntry.setPosition(newPosition);
        PlaylistEntry savedPlaylistEntry = playlistEntryRepository.save(playlistEntry);
        savedPlaylistEntries.add(0, convertToDTO(savedPlaylistEntry));
        return savedPlaylistEntries;
    }

    // delete playlist entry with specified id
    // Parameter entry_id : id of entry to be deleted
    // Return : none
    public void deletePlaylistEntry(Integer entry_id) {
        playlistEntryRepository.deleteById(entry_id);
    }

    // convert Playlist entity to playlist DTO
    // Parameter playlist : playlist entity to be converted
    // Return : playlist DTO with values corresponding to entity
    private PlaylistDTO convertToDTO(Playlist playlist) {
        return new PlaylistDTO(playlist.getId(), playlist.getTitle());
    }
    
    // convert playlist DTO to playlist entity
    // Parameter playlistDTO : playlist DTO to be converted
    // Return : playlist entity with values corresponding to the input DTO
    private Playlist convertToEntity(PlaylistDTO playlistDTO) {
        Playlist playlist = new Playlist();
        playlist.setTitle(playlistDTO.title());
        return playlist;
    }

    // convert Playlist entry entity to playlist entry DTO
    // Parameter playlistEntry : playlist entry entity to be converted
    // Return : playlist entry DTO with values corresponding to entity
    private PlaylistEntryDTO convertToDTO(PlaylistEntry playlistEntry) {
        return new PlaylistEntryDTO(
            playlistEntry.getId(), 
            playlistEntry.getPosition(), 
            playlistEntry.getPlaylist().getId(), 
            playlistEntry.getSong().getId());
    }

    // convert playlist entry DTO to playlist entry entity
    // Parameter playlistEntryDTO : playlist entry DTO to be converted
    // Return : playlist entry entity with values corresponding to the input DTO
    private PlaylistEntry convertToEntity(PlaylistEntryDTO playlistEntryDTO) {
        PlaylistEntry playlistEntry = new PlaylistEntry();
        playlistEntry.setPlaylist(playlistRepository.findById(playlistEntryDTO.playlist_id()).get());
        playlistEntry.setSong(songRepository.findById(playlistEntryDTO.song_id()).get());
        playlistEntry.setPosition(playlistEntryDTO.position());
        return playlistEntry;
    }

}