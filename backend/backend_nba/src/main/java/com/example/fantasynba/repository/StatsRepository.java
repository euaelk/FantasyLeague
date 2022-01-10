package com.example.fantasynba.repository;

import com.example.fantasynba.domain.PlayerStats;
import com.example.fantasynba.domain.TraditionalStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatsRepository extends JpaRepository<PlayerStats, TraditionalStats> {

}
