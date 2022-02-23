package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService{

    private final TeamRepository teamRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Override
    @Async
    public void nbaStandings() throws IOException {
            Document doc = Jsoup.connect("https://www.basketball-reference.com/leagues/NBA_2022_standings.html").get();
            Element table = doc.getElementById("all_standings");
            Element eastConf = table.getElementById("confs_standings_E");
            Element westConf = table.getElementById("confs_standings_W");
            updateStandings(eastConf);
            updateStandings(westConf);
    }

    @Override
    public void updateStandings(Element conf) { // use a lamda instead of while loop?
        Element table = conf.select("tbody").first();
        Iterator<Element> tr = table.select("tr").iterator();
        while(tr.hasNext()){
            Iterator<Element> team = tr.next().getAllElements().iterator();
            saveTeam(team.next());
        }
    }

    @Override
    public void saveTeam(Element team) {
        String img = setTeamLogo(team.select("[data-stat=team_name]").select("a").attr("abs:href"));
        String name = team.select("[data-stat=team_name]").select("a").text();
        Integer wins = Integer.valueOf(team.select("[data-stat=wins]").text());
        Integer losses = Integer.valueOf(team.select("[data-stat=losses]").text());
        Double winLossPer = Double.parseDouble(team.select("[data-stat=win_loss_pct]").text());
        Double scoreAvg = Double.parseDouble(team.select("[data-stat=pts_per_g]").text());
        Double oppScoreAvg = Double.parseDouble(team.select("[data-stat=opp_pts_per_g]").text());

        Team t = createTeam(name, wins, losses, winLossPer, scoreAvg, oppScoreAvg, img);
        Team duplicate = teamRepository.findByName(name);
        if (duplicate != null && duplicate.equals(t)) return;
        else teamRepository.save(t);
    }

    private Team createTeam(String name, Integer wins, Integer losses, Double winLossPer, Double scoreAvg, Double oppScoreAvg, String img){
        return Team.builder()
                .name(name)
                .wins(wins)
                .losses(losses)
                .winLossPer(winLossPer)
                .scoreAvg(scoreAvg)
                .oppScoreAvg(oppScoreAvg)
                .teamLogo(img)
                .build();
    }

    private String setTeamLogo(String teamUrl){
        Document doc = null;
        try {
            doc = Jsoup.connect(teamUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String imgUrl = doc.getElementById("info").select("img").attr("abs:src");
        return imgUrl;
    }

    @Override
    public List<Team> getAllTeams() {
        transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            return teamRepository.findAll();
        });
    }

    @Override
    public Team findTeam(String name) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        return transactionTemplate.execute(status -> {
            return teamRepository.findByName(name);
        });
    }

    @Override
    public List<Game> getTeamGames(String team_name) {
        transactionTemplate = new TransactionTemplate(transactionManager);
        List<Game> games = transactionTemplate.execute(status -> {
            Team team = teamRepository.findByName(team_name);
            List<Game> allGames = new ArrayList<>();
            team.getAwayGames().forEach(g -> allGames.add(g));
            team.getHomeGames().forEach(g -> allGames.add(g));
            return allGames;

        });
        return games;
    }

}
