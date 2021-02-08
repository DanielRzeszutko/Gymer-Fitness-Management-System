package com.gymer.api.slot.entity;

import com.gymer.api.employee.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.Link;

import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@AllArgsConstructor
public class SlotDTO {

	private Long id;
	private Date date;
	private Time startTime;
	private Time endTime;
	private List<Link> users;
	private Employee employee;
	private boolean isPrivate;

}
