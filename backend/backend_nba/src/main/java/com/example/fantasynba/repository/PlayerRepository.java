package com.example.fantasynba.repository;

import com.example.fantasynba.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findAll();

    Optional<Player> findByName(String name);
}
