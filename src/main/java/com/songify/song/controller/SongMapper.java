package com.songify.song.controller;

import com.songify.song.dto.request.CreateSongRequestDto;

public class SongMapper {
    public static Song mapFromCreateSongRequestDtoToSong(CreateSongRequestDto dto) {
        return new Song(dto.songName(), dto.artist());
    }
}
