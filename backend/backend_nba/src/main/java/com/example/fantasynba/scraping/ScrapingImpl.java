package com.example.fantasynba.scraping;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.service.DateService;
import com.example.fantasynba.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;


@Service
@Transactional
@RequiredArgsConstructor
public class ScrapingImpl implements Scraping{

    private final PlayerService playerService;
    private final DateService dateService;
    private final StatsRepository statsRepository;
    private String visiting_team, home_team;
    private static String date;

    static String[] boxScores = {
            "https://www.basketball-reference.com/leagues/NBA_2022_games-october.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-november.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-december.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-january.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-february.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-march.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-april.html"
    };

    @Override
    public void getBoxScoreData(String boxScore) {
        openFutureGames(boxScore);
    }

    @Override
    @Async
    public void openFutureGames(String url) { // DRY Principle
        Document doc;
        try {
            doc = Jsoup.connect(url).get();
            System.out.println("Opening url : " + url);
            //processBoxScore(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processBoxScore(Document doc)  {
        Element body = doc.getElementById("schedule").select("tbody").first();
        Elements games = body.select("tr");
        games.stream().filter(p -> !p.hasAttr(".thead")).forEach(e -> futureScoreLink(e));
    }

    private void futureScoreLink(Element game) { // potentially return the link here to chain the cfs together ?
        date = game.select("[data-stat=date_game]").text(); // formerly game_date
        visiting_team = playerService.getTeamAbv().get(game.select("[data-stat=visitor_team_name]").text());
        home_team = playerService.getTeamAbv().get(game.select("[data-stat=home_team_name]").text());

        Element box_score_node = game.select("[data-stat=box_score_text]").select("a").first();
        processEastAndWest(box_score_node.attr("abs:href"), date);
    }


    private void processEastAndWest(String link, String date) { // turn date global ??
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Element away_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", visiting_team));
            Element home_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", home_team));
            if (away_team_box_score != null) {processTeamData(away_team_box_score, date);}
            if (home_team_box_score != null) {processTeamData(home_team_box_score, date);}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processTeamData(Element boxscore, String date) { //
        Elements players = boxscore.select("tbody").select("tr");
        players.stream().filter(p -> !p.hasAttr("class")).forEach(e -> savePlayerStats(e, date));
    }

    private void savePlayerStats(Element stat, String date) {
        if (stat.hasAttr("[data-stat='reason']"))
            return;

        String name = stat.select("[data-stat=player]").text();
        LocalDate game_date = dateService.getDateFromString(date);
        Integer fieldGoal = stringConversion(stat.select("[data-stat=fg]").text());
        Integer fgA = stringConversion(stat.select("[data-stat=fga]").text());
        Integer three = stringConversion(stat.select("[data-stat=fg3]").text());
        Integer pts = stringConversion(stat.select("[data-stat=pts]").text());
        Integer trb = stringConversion(stat.select("[data-stat=trb]").text());
        Integer ast = stringConversion(stat.select("[data-stat=ast]").text());
        Integer stl = stringConversion(stat.select("[data-stat=stl]").text());
        Integer blk = stringConversion(stat.select("[data-stat=blk]").text());
        Integer tov = stringConversion(stat.select("[data-stat=tov]").text());

        Player player = playerService.findPlayer(name);
        if (player == null){return;}

        PlayerStats p = statsBuilder(player, game_date, fieldGoal, fgA, three, pts, trb, ast, stl, blk, tov);
        statsRepository.save(p);
        player.recordPlayerStat(p);

    }

    private PlayerStats statsBuilder(Player player, LocalDate date, Integer fg, Integer fga, Integer threeP,
                                     Integer pts, Integer trb, Integer ast, Integer stl, Integer blk, Integer tov){
        return PlayerStats.builder()
                .player(player)
                .playerName(player.getName())
                .date(date)
                .fg(fg)
                .fga(fga)
                .threeP(threeP)
                .pts(pts)
                .trb(trb)
                .ast(ast)
                .blk(blk)
                .stl(stl)
                .tov(tov)
                .build();
    }

    @Override
    public Integer largeStringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }

    @Override
    public Integer stringConversion(String s)  {
        try {
            return s.isEmpty() ? 0 : Integer.parseInt(s);
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String[] getBoxScores() {
        return boxScores;
    }
}
