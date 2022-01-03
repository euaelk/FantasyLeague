package com.example.fantasynba.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class PlayerStats {

    private LocalDate localDate;
    private String minutes;
    private Integer fg;
    private Integer fga;
    private BigDecimal fgPer;
    private Integer threeP; // 3P
    private Integer threeA; // 3PA
    private BigDecimal threePer;
    private Integer ft;
    private Integer fta;
    private BigDecimal ftp;
    private Integer orb; // offensive reb
    private Integer drb; // def reb
    private Integer trb; // total reb
    private Integer ast;
    private Integer stl;
    private Integer blk;
    private Integer tov; // turnovers
    private Integer pf; // personal fouls
    private Integer pts;
    private Integer plusMinus;








}
