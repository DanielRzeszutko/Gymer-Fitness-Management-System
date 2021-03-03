package com.gymer.crudresources.workinghours.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Time;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
public class WorkingHourDTO extends RepresentationModel<WorkingHourDTO> {

    private Long id;
    private Day day;
    private Time startHour;
    private Time endHour;

    public WorkingHourDTO(WorkingHour workingHour) {
        this.id = workingHour.getId();
        this.day = workingHour.getDay();
        this.startHour = workingHour.getStartHour();
        this.endHour = workingHour.getEndHour();
    }

}
