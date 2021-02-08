package com.gymer.api.slot.entity;

import com.gymer.api.employee.entity.Employee;
import com.gymer.api.user.entity.User;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Slot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private Date date;

	@NotNull
	private Time startTime;
	@NotNull
	private Time endTime;

	@NotNull
	@ManyToMany
	private List<User> users;

	@OneToOne
	private Employee employee;

	@Column(columnDefinition = "boolean default true")
	private boolean isPrivate = true;

}
