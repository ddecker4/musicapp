package com.ddecker4.musicapp.controller;

import com.ddecker4.musicapp.service.SongService;
import com.ddecker4.musicapp.model.SongDTO;

import java.io.InputStream;
import java.net.URLEncoder;
import java.lang.Integer;
import java.util.Optional;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class SongController {
    
    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @GetMapping("/songs")
    public List<SongDTO> getAllSongs() {
        return songService.getAllSongs();
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<SongDTO> getSongById(@PathVariable("id") Integer id) {
        Optional<SongDTO> songDTO = songService.getSongById(id);
        try {
            return ResponseEntity.ok(songDTO.orElseThrow());
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/songs")
    public SongDTO createSong(@RequestParam("file") MultipartFile file) throws IOException {
        return songService.saveSong(file);
    }

    @DeleteMapping("/songs/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable("id") Integer id) {
        songService.deleteSong(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/songs/{id}")
    public ResponseEntity<SongDTO> updateSong(@PathVariable("id") Integer id, @RequestBody SongDTO songDTO) {
        try {
            SongDTO updatedSong = songService.updateSong(id, songDTO);
            return ResponseEntity.ok(updatedSong);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/songs/{songId}/download")
    public  ResponseEntity<StreamingResponseBody> download(@PathVariable("songId") String songId) {
        Integer id = Integer.parseInt(songId);
        SongDTO songDTO = songService.getSongById(id).get();
        String outFileName = songDTO.title() + ".mp3";
        if (songDTO.artist() != null) {
            outFileName = songDTO.artist() + " - " + outFileName;
        }
        InputStream in = songService.downloadSongById(id);
        StreamingResponseBody responseBody = outputStream -> {

            int numberOfBytesToWrite;
            byte[] data = new byte[1024];
            while ((numberOfBytesToWrite = in.read(data, 0, data.length)) != -1) {
                outputStream.write(data, 0, numberOfBytesToWrite);
            }

            in.close();
        };

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + outFileName)
            .contentType(new MediaType("audio", "mp3"))
            .body(responseBody);
    }
}