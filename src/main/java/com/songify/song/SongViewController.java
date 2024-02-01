package com.songify.song;

import ch.qos.logback.core.model.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class SongViewController {

    private Map<Integer, String> database = new HashMap<>();

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/view/songs")
    public String songs(Model model) {
        database.put(1, "Bring Me The Horizon – One Day The Only");
        database.put(2, "Fall Out Boy – A Little Less 16 Candles");
        database.put(3, "A Day To Remember – I’m Made Of Wax");
        database.put(4, "Pink Floyd – Several Species");

//        model.addAttribute("songMap", database);

        return "songs";
    }
}
