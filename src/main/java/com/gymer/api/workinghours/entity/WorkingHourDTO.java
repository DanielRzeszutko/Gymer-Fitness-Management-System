package com.gymer.api.workinghours.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Time;

@Data
@AllArgsConstructor
public class WorkingHourDTO {

	private Long id;
	private Day day;
	private Time startHour;
	private Time endHour;

}
