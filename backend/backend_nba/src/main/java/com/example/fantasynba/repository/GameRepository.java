package com.example.fantasynba.repository;

import com.example.fantasynba.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByDate(LocalDate date);

    // finds games of team that played as home or visitor
    // unsure if it works
    @Query("SELECT g FROM Game g WHERE :team IN (g.visitor, g.home)")
    List<Game> findByTeam(@Param("team") String team);

    @Query("SELECT g FROM Game g WHERE g.date = ?1")
    List<Game> findByTodayGames(String date);



}
