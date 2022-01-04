package com.example.fantasynba.controller;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.service.PlayerService;
import com.example.fantasynba.service.TeamService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final PlayerService playerService;



    @GetMapping("/{team}")
    public ResponseEntity<Team> getTeam(@PathVariable String team){
        return new ResponseEntity<>(teamService.findTeam(team), HttpStatus.OK);
    }

    @GetMapping("/players/{name}")
    public ResponseEntity<Player> getPlayer(@PathVariable String name){
        System.out.println(playerService.findPlayer(name).toString());
        return new ResponseEntity<>(playerService.findPlayer(name), HttpStatus.OK);
    }
}
