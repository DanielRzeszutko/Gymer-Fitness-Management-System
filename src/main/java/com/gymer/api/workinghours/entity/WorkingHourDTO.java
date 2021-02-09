package com.gymer.api.workinghours.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Time;

@Data
@AllArgsConstructor
public class WorkingHourDTO extends RepresentationModel<WorkingHourDTO> {

	private Long id;
	private Day day;
	private Time startHour;
	private Time endHour;

}
