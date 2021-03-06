package com.gymer.commonresources.workinghours.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Time;

@Data
@Entity
@NoArgsConstructor
public class WorkingHour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Day day;
    private Time startHour;
    private Time endHour;

    public WorkingHour(Day day, Time startHour, Time endHour) {
        this.day = day;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    public WorkingHour(WorkingHourDTO workingHourDTO) {
        this.id = workingHourDTO.getId();
        this.day = workingHourDTO.getDay();
        this.startHour = workingHourDTO.getStartHour();
        this.endHour = workingHourDTO.getEndHour();
    }

}
