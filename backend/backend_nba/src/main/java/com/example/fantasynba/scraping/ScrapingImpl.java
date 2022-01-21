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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
public class ScrapingImpl implements Scraping{

    private final PlayerService playerService;
    private final DateService dateService;
    private final StatsRepository statsRepository;
    private String visiting_team, home_team;
    private String date;
    private static final Logger LOGGER = LoggerFactory.getLogger(ScrapingImpl.class);

    static String[] boxScores = {
            "https://www.basketball-reference.com/leagues/NBA_2022_games-october.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-november.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-december.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-january.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-february.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-march.html",
            "https://www.basketball-reference.com/leagues/NBA_2022_games-april.html"
    };

    @Async
    public void getBoxScoreData() {
        List<CompletableFuture<Document>> completableFutures = new ArrayList<>();
        for (String s : boxScores) {
            CompletableFuture<Void> cf = CompletableFuture.supplyAsync(() -> openFutureGames(s))
                    .thenAccept(p -> processBoxScore(p));
        }
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0])).join();
    }

    private Document openFutureGames(String url)  { // DRY Principle
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            processBoxScore(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }

    private void processBoxScore(Document document) {
        Element table = document.getElementById("schedule");
        Iterator<Element> row = table.select("tr").iterator();
        row.next();
        while (row.hasNext()){
            Iterator<Element> tr_children = row.next().getAllElements().iterator();
            futureScoreLink(tr_children.next());
        }
    }

    private void futureScoreLink(Element game){ // potentially return the link here to chain the cfs together ?
        date = game.select("[data-stat=date_game]").text(); // formerly game_date
        visiting_team = playerService.getTeamAbv().get(game.select("[data-stat=visitor_team_name]").text());
        home_team = playerService.getTeamAbv().get(game.select("[data-stat=home_team_name]").text());

        Element box_score_node = game.select("[data-stat=box_score_text]").select("a").first();
        processEastAndWest(box_score_node.attr("abs:href"), date);
    }


    private CompletableFuture<Document> processEastAndWest(String link, String date) { // turn date global ??
        Document doc;
        try {
            doc = Jsoup.connect(link).get(); // open box score
            Element away_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", visiting_team));
            Element home_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", home_team));
            if (away_team_box_score != null) {processTeamData(away_team_box_score, date);}
            if (home_team_box_score != null) {processTeamData(home_team_box_score, date);}

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return CompletableFuture.completedFuture(doc);
    }

    private void processTeamData(Element e, String date) {
        Element body = e.select("tbody").first();
        for (Element row : body.select("tr")) {
            if (row.hasAttr("class"))
                continue;
            for (Element element : row.getAllElements()) savePlayerStats(element, date);
        }
    }

    private void savePlayerStats(Element stat, String date) {
        if (stat.hasAttr("[data-stat='reason']"))
            return;
        try {
            String name = stat.select("[data-stat=player]").text();
            LocalDate game_date = dateService.getDateFromString(date);
            Integer fieldGoal = Integer.valueOf(stat.select("[data-stat=fg]").text());
            Integer fgA = Integer.valueOf(stat.select("[data-stat=fga]").text());
            Integer three = Integer.valueOf(stat.select("[data-stat=fg3]").text());
            Integer pts = Integer.valueOf(stat.select("[data-stat=pts]").text());
            Integer trb = Integer.valueOf(stat.select("[data-stat=trb]").text());
            Integer ast = Integer.valueOf(stat.select("[data-stat=ast]").text());
            Integer stl = Integer.valueOf(stat.select("[data-stat=stl]").text());
            Integer blk = Integer.valueOf(stat.select("[data-stat=blk]").text());
            Integer tov = Integer.valueOf(stat.select("[data-stat=tov]").text());

            Player player = playerService.findPlayer(name);
            if (player == null){return;}

            PlayerStats p = statsBuilder(player, game_date, fieldGoal, fgA, three, pts, trb, ast, stl, blk, tov);
            statsRepository.save(p);
            player.recordPlayerStat(p);

        } catch(NumberFormatException e){
            LOGGER.warn("Error retrieving player stats");
        }
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
    public Integer stringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }
}
