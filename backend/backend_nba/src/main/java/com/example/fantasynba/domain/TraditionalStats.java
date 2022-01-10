package com.example.fantasynba.domain;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
public class TraditionalStats implements Serializable {
    private LocalDate date;
    private String playerName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TraditionalStats stats = (TraditionalStats) o;
        return this.date.equals(stats.date) && this.playerName.equals(stats.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, playerName);
    }
}
