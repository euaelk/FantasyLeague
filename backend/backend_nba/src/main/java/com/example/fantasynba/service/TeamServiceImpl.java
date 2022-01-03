package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;

    @Override
    @Async
    public void nbaStandings() {
        try {
            String url = "https://www.basketball-reference.com/leagues/NBA_2022_standings.html";
            Document doc = Jsoup.connect(url).get();
            Element table = doc.getElementById("all_standings");
            Element eastConf = table.getElementById("confs_standings_E");
            Element westConf = table.getElementById("confs_standings_W");

            updateStandings(eastConf);
            updateStandings(westConf);

        } catch (IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public void updateStandings(Element conf) {
        Element table = conf.select("table").first();
        Iterator<Element> tr = table.select("tr").iterator();

        tr.next();
        while(tr.hasNext()){
            Iterator<Element> team = tr.next().getAllElements().iterator();
            teamStanding(team.next());
        }
    }

    @Override
    @Transactional
    public void teamStanding(Element team) {
        teamRepository.save(Team.builder()
                .name(team.select("[data-stat=team_name]").select("a").text())
                .wins(Integer.valueOf(team.select("[data-stat=wins]").text()))
                .losses(Integer.valueOf(team.select("[data-stat=losses]").text()))
                .winLossPer(Double.parseDouble(team.select("[data-stat=win_loss_pct]").text()))
                .scoreAvg(Double.parseDouble(team.select("[data-stat=pts_per_g]").text()))
                .oppScoreAvg(Double.parseDouble(team.select("[data-stat=opp_pts_per_g]").text()))
                .build());
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public Team findTeam(String name) {
        return teamRepository.findByName(name);
    }

    @Override
    public List<Game> getTeamGames(String team) {
        Team team_games = teamRepository.findByName(team);
        List<Game> games = new ArrayList<>();
        team_games.getAwayGames().forEach(g -> games.add(g));
        return games;
    }


}
