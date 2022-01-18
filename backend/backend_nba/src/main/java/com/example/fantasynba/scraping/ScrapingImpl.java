package com.example.fantasynba.scraping;

import com.example.fantasynba.domain.Player;
import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.repository.PlayerRepository;
import com.example.fantasynba.repository.StatsRepository;
import com.example.fantasynba.service.DateService;
import com.example.fantasynba.service.PlayerService;
import com.example.fantasynba.service.PlayerServiceImpl;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScrapingImpl implements Scraping{

    private final PlayerService playerService;
    private final DateService dateService;
    private final StatsRepository statsRepository;
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

    private String visiting_team, home_team;
    private Map<String,String> teamNames = PlayerServiceImpl.teamAbv;

    @Override
    public void getBoxScoreData() {
        Document doc;
        try {
            for (String s : boxScores){
                doc = Jsoup.connect(s).get();
                Element table_container = doc.getElementById("schedule"); // passing in table of games
                if (table_container != null) { open_boxScore_link(table_container);}
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void open_boxScore_link(Element table) {
        Iterator<Element> row = table.select("tr").iterator();
        row.next();
        while (row.hasNext()){
            Iterator<Element> tr_children = row.next().getAllElements().iterator();
            Element column_data = tr_children.next();
            String game_date = column_data.select("[data-stat=date_game]").text();
            visiting_team = teamNames.get(column_data.select("[data-stat=visitor_team_name]").text());
            home_team = teamNames.get(column_data.select("[data-stat=home_team_name]").text());
            Element box_score_node = column_data.select("[data-stat=box_score_text]").select("a").first();
            if (box_score_node == null) return;

            process_boxScore_data(box_score_node.attr("abs:href"), game_date);
        }
    }

    @Override
    public void process_boxScore_data(String link, String date) {
        Document doc;
        try {
            doc = Jsoup.connect(link).get(); // open box score
            Element away_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", visiting_team));
            Element home_team_box_score = doc.getElementById(String.format("all_box-%s-game-basic", home_team));
            if (away_team_box_score != null) {process_team_data(away_team_box_score, date);}
            if (home_team_box_score != null) {process_team_data(home_team_box_score, date);}

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process_team_data(Element e, String date) {
        Element body = e.select("tbody").first();
        for (Element row : body.select("tr")) {
            if (row.hasAttr("class"))
                continue;
            for (Element element : row.getAllElements()) savePlayerStats(element, date);
        }
    }

    @Override
    @Transactional
    public void savePlayerStats(Element stat, String date) {
        LOGGER.debug("Saving Player stats " + date);
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
            Player player = playerService.findPlayer(name); // null error from players no longer on team

            if (player == null){
                LOGGER.error("Player not found");
                return;
            }
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
