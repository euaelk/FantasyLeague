package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private LocalDate date;
    private String time;

    @ManyToOne
    @JoinColumn(name = "visitor_fk", referencedColumnName = "team_id")
    @JsonBackReference
    private Team visitor;

    @ManyToOne
    @JoinColumn(name = "home_fk", referencedColumnName = "team_id")
    @JsonBackReference
    private Team home;

    private Integer vPts;
    private Integer hPts;
    private String overtime;
    private Integer attendance;

    private String visitor_name;
    private String home_name;


    public Game(LocalDate date, String time, Team visitor, Integer vPts, Team home, Integer hPts, String overtime, Integer attendance){
        this.date = date;
        this.time = time;
        this.visitor = visitor;
        this.vPts = vPts;
        this.hPts = hPts;
        this.home = home;
        this.overtime = overtime;
        this.attendance = attendance;
        this.visitor_name = visitor.getName();
        this.home_name = home.getName();
    }


}
