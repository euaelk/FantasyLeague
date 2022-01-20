package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "player_id", unique = true, nullable = false)
    private Long id;
    private String name;
    private String position;
    private String height;
    private Integer weight;
    private String dob; // birthdate
    private String college;

    @ManyToOne
    @JoinColumn(name = "team_fk", referencedColumnName = "team_id")
    @JsonBackReference
    private Team team;

    @OneToMany(mappedBy = "player", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @Column(nullable = true)
    @JsonManagedReference
    private Set<PlayerStats> stats; // mappedBy targets player field in PlayerStats class


    public Player(String name, String position, String height, Integer weight, String dob, String college, Team team) {
        this.name = name;
        this.position = position;
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.college = college;
        this.team = team;
    }

    public Player(String name){
        this.name = name;
    }

    @Override
    public String toString(){
        return "Player [id = " + id + ", name = " + name + " ]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player)) return false;
        Player player = (Player) o;
        return Objects.equals(this.name, player.name) && Objects.equals(this.position, player.position)
                && Objects.equals(this.height, player.height) && Objects.equals(this.weight, player.weight)
                && Objects.equals(this.dob, player.dob) && Objects.equals(this.college, player.college)
                && Objects.equals(this.team, player.team);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hashCode(id);
    }

    public void recordPlayerStat(PlayerStats s){this.stats.add(s);}
}
