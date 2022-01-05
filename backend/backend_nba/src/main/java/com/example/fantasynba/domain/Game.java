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

    @Override
    public boolean equals(Object o) {
        // if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return this.date.isEqual(game.date) && this.visitor.equals(game.visitor)
                && this.home.equals(game.home);
    }

    @Override
    public int hashCode() {
        int hashCode = 1;
        hashCode = hashCode * 37 + this.date.hashCode();
        hashCode = hashCode * 37 + this.time.hashCode();
        hashCode = hashCode * 37 + this.visitor.hashCode();
        hashCode = hashCode * 37 + this.vPts.hashCode();
        hashCode = hashCode * 37 + this.hPts.hashCode();
        hashCode = hashCode * 37 + this.home.hashCode();
        hashCode = hashCode * 37 + this.overtime.hashCode();
        hashCode = hashCode * 37 + this.attendance.hashCode();

        return hashCode;

    }
}
