package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;


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

}
