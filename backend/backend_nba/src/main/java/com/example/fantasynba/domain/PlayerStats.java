package com.example.fantasynba.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@IdClass(TraditionalStats.class)
public class PlayerStats implements Serializable {

    @Id
    private String playerName;

    @Id
    private LocalDate date;

    private Integer fg;
    private Integer fga;
    private Integer threeP; // 3P
    private Integer trb; // total reb
    private Integer ast;
    private Integer stl;
    private Integer blk;
    private Integer tov; // turnovers
    private Integer pts;

    @ManyToOne
    @JoinColumn(name = "player_id")
    @JsonBackReference
    private Player player;



    public PlayerStats(LocalDate date, Player player){
        this.playerName = player.getName();
        this.date = date;
    }

    public PlayerStats(LocalDate date, Integer fg, Integer fga, Integer threeP, Integer trb, Integer ast, Integer stl, Integer blk, Integer tov, Integer pts, Player player) {
        this.date = date;
        this.fg = fg;
        this.fga = fga;
        this.threeP = threeP;
        this.trb = trb;
        this.ast = ast;
        this.stl = stl;
        this.blk = blk;
        this.tov = tov;
        this.pts = pts;
        this.player = player;
        this.playerName = player.getName();
    }



    public Double fantasyPoints(){
        return (this.pts * 1) + (this.trb * 1.2) + (this.ast * 1.5) + (this.stl * 3) + (this.blk * 3) - (this.tov * 1)
                -(this.fga * .5) + (this.threeP * 2) + (this.fg * 1);
    }

    @Override
    public String toString() {
        return "PlayerStats{" +
                " date=" + date +
                ", fg=" + fg +
                ", fga=" + fga +
                ", threeP=" + threeP +
                ", trb=" + trb +
                ", ast=" + ast +
                ", stl=" + stl +
                ", blk=" + blk +
                ", tov=" + tov +
                ", pts=" + pts +
                '}';
    }
}
