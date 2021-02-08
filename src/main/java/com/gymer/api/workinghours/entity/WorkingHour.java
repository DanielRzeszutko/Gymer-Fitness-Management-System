package com.gymer.api.workinghours.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class WorkingHour {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Day day;
	private Time startHour;
	private Time endHour;

}
