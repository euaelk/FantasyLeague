package com.example.fantasynba.repository;

import com.example.fantasynba.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

//    @Query("SELECT t FROM Team t WHERE t.name = :teamName")
//    List<Team> findByName(@Param("teamName") String teamName);
    List<Team> findAll();
    Team findByName(String name);
}
