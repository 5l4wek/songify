package com.songify.song.controller;

import com.songify.song.dto.request.PartiallyUpdateSongRequestDto;
import com.songify.song.dto.request.CreateSongRequestDto;
import com.songify.song.dto.request.UpdateSongRequestDto;
import com.songify.song.dto.response.*;
import com.songify.song.error.SongNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log4j2
@RequestMapping("/songs")
public class SongRestController {

    Map<Integer, Song> database = new HashMap<>(Map.of(
            1, new Song("Bring Me The Horizon", "Korn"),
            2, new Song("Fall Out Boy", "Linkin Park"),
            3, new Song("A Day To Remember", "System of a Down"),
            4, new Song("Several Species", "Pink Floyd")
    ));

    @GetMapping
    public ResponseEntity<GetAllSongsResponseDto> getAllSongs(@RequestParam(required = false) Integer limit) {
        if (limit != null) {
            Map<Integer, Song> limitedMap = database.entrySet()
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            GetAllSongsResponseDto response = new GetAllSongsResponseDto(limitedMap);
            return ResponseEntity.ok(response);
        }
        GetAllSongsResponseDto response = new GetAllSongsResponseDto(database);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetSongResponseDto> getSongsById( @PathVariable Integer id, @RequestHeader(required = false) String requestId) {
        log.info(requestId);
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("song with id: " + id + " not found");
        }
        Song song = database.get(id);
        GetSongResponseDto response = new GetSongResponseDto(song);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<CreateSongResponseDto> postSong(@RequestBody @Valid CreateSongRequestDto request) {
        // 1. Mapowanie z CreateSongRequestDto na obiekt domenowy (Song)
        Song song = SongMapper.mapFromCreateSongRequestDtoToSong(request);
        // 2. Warstwa logiki biznesowej/serwisów domenowych: wyświetlamy informacje
        log.info("adding new song " + song);
        // 3. Warstwa bazodanowa: zapisujemy do (pseudo) bazy danych
        database.put(database.size() + 1, song);
        // 4. Mapowanie z obiektu domenowego (Song) na DTO CreateSongRequestDto
        CreateSongResponseDto body = new CreateSongResponseDto(song);
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DeleteSongResponseDto> deleteSongByIdUsingPathVariable(@PathVariable Integer id) {
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("song with id: " + id + " not found");
        }
        database.remove(id);
        return ResponseEntity.ok(new DeleteSongResponseDto("You deleted song with id: " + id, HttpStatus.OK));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateSongResponseDto> update(
            @PathVariable Integer id,
            @RequestBody
            @Valid UpdateSongRequestDto request) {
        if (!database.containsKey(id)) {
            throw new SongNotFoundException("song with id: " + id + " not found");
        }
        String newSongName = request.songName();
        String newArtist = request.artist();
        Song newSong = new Song(newSongName, newArtist);
        Song oldSong = database.put(id, newSong);
        log.info("Updated song with Id: " + id +
                " with songName: " + oldSong.name() + " to newSongName: " + newSong.name() +
                " oldArtist: " + oldSong.artist() + "to newArtist: " + newSong.artist());
        return ResponseEntity.ok(new UpdateSongResponseDto(newSong.name(), newSong.artist()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PartiallyUpdateSongResponseDto> partiallyUpdateSong(
            @PathVariable Integer id,
            @RequestBody PartiallyUpdateSongRequestDto request) {

        if (!database.containsKey(id)) {
            throw new SongNotFoundException("song with id: " + id + " not found");
        }
        Song songFromDataBase = database.get(id);
        Song.SongBuilder builder = Song.builder();
        if (request.songName() != null) {
            builder.name(request.songName());
            log.info("Partially updated song name");
        } else {
            builder.name(songFromDataBase.name());
        }
        if (request.artist() != null) {
            builder.artist(request.artist());
            log.info("Partially updated artist");
        } else {
            builder.artist(songFromDataBase.artist());
        }
        Song updatedSong = builder.build();
        database.put(id, updatedSong);
        return ResponseEntity.ok(new PartiallyUpdateSongResponseDto(updatedSong));
    }
}
