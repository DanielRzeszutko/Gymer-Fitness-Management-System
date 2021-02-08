package com.gymer.api.slot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

import java.sql.Date;
import java.sql.Time;

@Data
@AllArgsConstructor
public class SlotDTO {

    private Long id;
    private Date date;
    private Time startTime;
    private Time endTime;
    private Links users;
    private Link employee;
    private boolean isPrivate;

}
