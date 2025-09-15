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

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    public Optional<SongDTO> getSongById(Integer id) {
        return songRepository.findById(id).map(this::convertToDTO);
    }

    public InputStream downloadSongById(Integer id) {
        return minioService.downloadFile(id.toString() + ".mp3");
    }

    public SongDTO saveSong(MultipartFile file) throws IOException {
        Song song = new Song();
        String title = file.getOriginalFilename();
        title = title.substring(0, title.lastIndexOf('.'));
        song.setTitle(title);
        Song savedSong = songRepository.save(song);
        String newFileName = String.format("%d.mp3", savedSong.getId());
        minioService.uploadFile(newFileName, file.getBytes());
        return convertToDTO(savedSong);
    }
    
    public SongDTO updateSong(Integer id, SongDTO songDTO) {
        Song song = songRepository.findById(id).orElseThrow();
        song.setTitle(songDTO.title());
        song.setArtist(songDTO.artist());
        Song updatedSong = songRepository.save(song);
        return convertToDTO(updatedSong);
    }
    
    public void deleteSong(Integer id) {
        String filename = String.valueOf(id) + ".mp3";
        minioService.deleteFile(filename);
        songRepository.deleteById(id);
    }

    private SongDTO convertToDTO(Song song) {
        return new SongDTO(song.getId(), song.getTitle(), song.getArtist());
    }

    private Song convertToEntity(SongDTO songDTO) {
        Song song = new Song();
        song.setTitle(songDTO.title());
        song.setArtist(songDTO.artist());
        return song;
    }
}