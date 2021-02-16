package com.gymer.api.slot.entity;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.user.entity.User;
import com.sun.istack.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Slot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String description;

	@NotNull
	@Temporal(TemporalType.DATE)
	private Date date;

	@NotNull
	@Temporal(TemporalType.TIME)
	private Time startTime;

	@NotNull
	@Temporal(TemporalType.TIME)
	private Time endTime;

	@NotNull
	@ManyToMany
	private List<User> users;

	@NotNull
	@OneToOne
	private Employee employee;

	private String slotType;

	@Column(columnDefinition = "boolean default true")
	private boolean isPrivate = true;

	public Slot(String description, Date date, Time startTime, Time endTime, List<User> users, Employee employee, String slotType, boolean isPrivate) {
		this.description = description;
		this.date = date;
		this.startTime = startTime;
		this.endTime = endTime;
		this.users = users;
		this.employee = employee;
		this.slotType = slotType;
		this.isPrivate = isPrivate;
	}

	public Slot(SlotDTO slotDTO) {
		this.id = slotDTO.getId();
		this.date = slotDTO.getDate();
		this.description = slotDTO.getDescription();
		this.startTime = slotDTO.getStartTime();
		this.endTime = slotDTO.getEndTime();
		this.slotType = slotDTO.getSlotType();
		this.isPrivate = slotDTO.isPrivate();
	}

}
