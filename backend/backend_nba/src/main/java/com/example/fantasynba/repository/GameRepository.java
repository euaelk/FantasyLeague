package com.example.fantasynba.repository;

import com.example.fantasynba.domain.Game;

import com.example.fantasynba.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByDate(LocalDate date);

    @Query("SELECT g FROM Game g WHERE :team IN (g.visitor, g.home)")
    List<Game> findByTeam(@Param("team") String team);

    @Query("SELECT g FROM Game g WHERE g.date = ?1")
    List<Game> findByTodayGames(String date);

    @Query("SELECT g FROM Game g WHERE g.date = ?1 AND g.visitor_name = ?2 AND g.home_name = ?3")
    Game findByDateAndTeams(@Param("date") LocalDate date, @Param("visitor") String visitor, @Param("home") String home);


}
