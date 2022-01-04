package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Objects;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String name;
    private String position;
    private String height;
    private Integer weight;
    private String dob; // birthdate
    private String college;

    @ManyToOne
    @JoinColumn(name = "team_id")
    @JsonBackReference
    private Team team;


    public Player(String name, String position, String height, Integer weight, String dob, String college, Team team) {
        this.name = name;
        this.position = position;
        this.height = height;
        this.weight = weight;
        this.dob = dob;
        this.college = college;
        this.team = team;
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
        return this.name.equals(player.name) && this.position.equals(player.position)
                && this.height.equals(player.height) && this.weight.equals(player.weight)
                && this.dob.equals(player.dob) && this.college.equals(player.college)
                && this.team.getName().equals(player.team.getName());
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 37 + this.name.hashCode();
        hashCode = hashCode * 37 + this.position.hashCode();
        hashCode = hashCode * 37 + this.height.hashCode();
        hashCode = hashCode * 37 + this.weight.hashCode();
        hashCode = hashCode * 37 + this.dob.hashCode();
        hashCode = hashCode * 37 + this.college.hashCode();
        hashCode = hashCode * 37 + this.team.hashCode();
        return hashCode;
    }
}
