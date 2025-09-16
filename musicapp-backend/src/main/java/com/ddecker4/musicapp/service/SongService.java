package com.ddecker4.musicapp.service;

import com.ddecker4.musicapp.model.*;
import com.ddecker4.musicapp.repository.*;
import com.ddecker4.musicapp.service.MinioService;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class SongService {

    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final PlaylistEntryRepository playlistEntryRepository;
    private final MinioService minioService;

    public SongService(SongRepository songRepository, MinioService minioService, PlaylistRepository playlistRepository, PlaylistEntryRepository playlistEntryRepository) {
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
        this.playlistEntryRepository = playlistEntryRepository;
        this.minioService = minioService;
    }

    // get all songs in database
    // Parameters : none
    // Return : List of song DTOs representing each song in the database
    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    // get song with specified id in database
    // Parameter song_id : id of song to be retrieved
    // Return : if song found, a song DTO representing the song, otherwise, null
    public Optional<SongDTO> getSongById(Integer song_id) {
        return songRepository.findById(song_id).map(this::convertToDTO);
    }

    // retrieve mp3 file for song with specified id from MinIO storage
    // Parameter song_id : id of song to get mp3 of
    // Return : InputStream containing mp3 file content 
    public InputStream downloadSongById(Integer song_id) {
        return minioService.downloadFile(song_id.toString() + ".mp3");
    }

    // save new song to database and upload corresponding mp3 to MinIO storage
    // Parameter file : mp3 file for song
    // Return : song DTO representing newly added song
    public SongDTO saveSong(MultipartFile file) throws IOException {
        Song song = new Song();
        
        // derive title from mp3 file name
        String title = file.getOriginalFilename();
        title = title.substring(0, title.lastIndexOf('.'));
        song.setTitle(title);

        // save song to database
        Song savedSong = songRepository.save(song);
        String newFileName = String.format("%d.mp3", savedSong.getId());
        
        // upload song to MinIO storage
        minioService.uploadFile(newFileName, file.getBytes());

        return convertToDTO(savedSong);
    }
    
    // update song title and artist in database
    // Parameter song_id : id of song to be updated
    // Parameter songDTO : song DTO containing new title and artist
    // Return : song DTO representing updated song 
    public SongDTO updateSong(Integer song_id, SongDTO songDTO) {
        Song song = songRepository.findById(song_id).orElseThrow();
        song.setTitle(songDTO.title());
        song.setArtist(songDTO.artist());
        Song updatedSong = songRepository.save(song);
        return convertToDTO(updatedSong);
    }
    
    // delete song with specified id from database and delete mp3 from storage
    // Parameter song_id : id of song to be deleted
    // Return : none
    public void deleteSong(Integer song_id) {
        // delete mp3 from storage
        String filename = String.valueOf(song_id) + ".mp3";
        minioService.deleteFile(filename);
        // delete song from database
        songRepository.deleteById(song_id);
    }

    // convert song entity to song DTO
    // Parameter song : song entity to be converted
    // Return : song DTO with values corresponding to entity
    private SongDTO convertToDTO(Song song) {
        return new SongDTO(song.getId(), song.getTitle(), song.getArtist());
    }

    // convert song DTO to song entity
    // Parameter songDTO : song DTO to be converted
    // Return : song entity with values corresponding to the input DTO
    private Song convertToEntity(SongDTO songDTO) {
        Song song = new Song();
        song.setTitle(songDTO.title());
        song.setArtist(songDTO.artist());
        return song;
    }
}