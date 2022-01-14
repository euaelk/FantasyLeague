package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;

import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id", unique = true, nullable = false)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    private Integer wins;
    private Integer losses;
    private Double winLossPer;
    private Double scoreAvg;
    private Double oppScoreAvg;

    @OneToMany(mappedBy = "team", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Column(nullable = true)
    @JsonManagedReference
    private Set<Player> players; // mappedBy targets team field in Players class

    @OneToMany(mappedBy = "visitor", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Column(nullable = true)
    @JsonManagedReference
    private Set<Game> awayGames; // mappedBy -> visitor field in Game class

    @OneToMany(mappedBy = "home", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Column(nullable = true)
    @JsonManagedReference
    private Set<Game> homeGames; // mappedBy -> home field in Game class


    public String getRecord(){
        return this.getWins() + " W | " + this.getLosses() + " L ";
    }

    public Team(String name){
        this.name = name;
    }

    public Team(String name, Integer wins, Integer losses, Double winLossPer, Double scoreAvg, Double oppScoreAvg) {
        this.name = name;
        this.wins = wins;
        this.losses = losses;
        this.winLossPer = winLossPer;
        this.scoreAvg = scoreAvg;
        this.oppScoreAvg = oppScoreAvg;
    }

    public Team(String name){
        this.name = name;
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Team)) return false;
        Team team = (Team) o;
        return this.name.equals(team.name) && this.wins.equals(team.wins)
                && this.losses.equals(team.losses) && this.winLossPer.equals(team.winLossPer)
                && this.scoreAvg.equals(team.scoreAvg) && this.oppScoreAvg.equals(team.oppScoreAvg);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 37 + this.name.hashCode();
        hashCode = hashCode * 37 + this.wins.hashCode();
        hashCode = hashCode * 37 + this.losses.hashCode();
        hashCode = hashCode * 37 + this.winLossPer.hashCode();
        hashCode = hashCode * 37 + this.scoreAvg.hashCode();
        hashCode = hashCode * 37 + this.oppScoreAvg.hashCode();
        return hashCode;
    }

    @Override
    public String toString(){
        return "Team [id = " + id + ", name = " + name + " ]";
    }

    public void addPlayer(Player p){
        this.players.add(p);
    }



}
