package com.example.fantasynba.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class GameRequest {

    public Integer vPts;
    public Integer hPts;
    public Integer attendance;
    public Team visitor;
    public Team home;
    public String time;
    public LocalDate date;
    public String overtime;
}
