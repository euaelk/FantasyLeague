package com.example.fantasynba.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@Service
public class DateServiceImpl implements DateService{

    @Override
    public LocalDate getDateFromString(String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E, MMM d, yyyy", Locale.US);
        try {
            return LocalDate.parse(date, dtf);

        } catch (DateTimeParseException e){
            System.err.println("Unable to parse the date!");
        }
        return null;
    }




}
