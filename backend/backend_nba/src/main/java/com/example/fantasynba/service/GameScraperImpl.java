package com.example.fantasynba.service;

import com.example.fantasynba.domain.Game;
import com.example.fantasynba.domain.Team;
import com.example.fantasynba.repository.GameRepository;
import com.example.fantasynba.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
import java.util.concurrent.ExecutionException;


@Service
@RequiredArgsConstructor
public class GameScraperImpl implements GameScraper {


    private final GameRepository gameRepository;
    private final DateService dateService;
    private final TeamRepository teamRepository;
    private static final Logger logger = LoggerFactory.getLogger(GameScraperImpl.class);

    @Override
    public void fetchGameData(String reference_url) throws ExecutionException, InterruptedException {
        Map<String, String> urls = new HashMap<>();
        Document doc;

        try {
            String baseUri = "https://www.basketball-reference.com";
            doc = Jsoup.connect(reference_url).get();

            Element months = doc.getElementsByClass("filter").first();
            Elements links = months != null ? months.getElementsByTag("a") : null;

            for (Element link : links){
                String linkHref = link.attr("href");
                String month = link.text();
                urls.put(month, baseUri.concat(linkHref));
            }
            List<String> monthLinks = new ArrayList<>(urls.values());

            for (String s : monthLinks){

                doc = Jsoup.connect(s).get();

                Element table = doc.getElementById("schedule");
                Iterator<Element> row = table != null ? table.select("tr").iterator() : null;
                row.next(); // skip <th>

                while (row.hasNext()){
                    Iterator<Element> game = row.next().getAllElements().iterator();
                    gameDataFromHtml(game.next());
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    @Transactional
    @Async
    public void gameDataFromHtml(Element e) throws ExecutionException, InterruptedException {

        LocalDate date = dateService.getDateFromString(e.select("[data-stat=date_game]").text());
        String time = e.select("[data-stat=game_start_time]").text();
        Integer hPts = stringToInt(e.select("[data-stat=home_pts]").text());
        String overtime = e.select("[data-stat=overtimes]").text();
        Integer attendance = stringToInt(e.select("[data-stat=attendance]").text());
        Integer vPts = stringToInt(e.select("[data-stat=visitor_pts]").text());

        Team visitor = teamRepository.findByName(e.select("[data-stat=visitor_team_name]").text());
        Team home = teamRepository.findByName(e.select("[data-stat=home_team_name]").text());

        CompletableFuture<Game> asyncGame = createGame(date, time, hPts, overtime, attendance, vPts, visitor, home);
        Game game_duplicate = findGame(date, visitor, home);

        if (game_duplicate == null)
            gameRepository.save(asyncGame.get());
        else if (!(game_duplicate.equals(asyncGame.get())))
            gameRepository.save(asyncGame.get());

    }

    @Override
    @Async
    public CompletableFuture<Game> createGame(LocalDate date, String time, Integer hPts,
                                              String overtime, Integer attendance, Integer vPts,
                                              Team visitor, Team home) {
        logger.info("Creating game");

        Game g = Game.builder()
                .vPts(vPts)
                .hPts(hPts)
                .attendance(attendance)
                .visitor(visitor)
                .visitor_name(visitor.getName())
                .home(home)
                .home_name(home.getName())
                .time(time)
                .date(date)
                .overtime(overtime)
                .build();

                return CompletableFuture.completedFuture(g);
    }

    @Override
    public Integer stringToInt(String s)  {
        return StringUtils.isEmpty(s) ? 0 : Integer.parseInt(s.replaceAll(",", ""));
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public Game findGame(LocalDate date, Team visitor, Team home){
        return gameRepository.findByDateAndTeams(date, visitor.getName(), home.getName());
    }
}
