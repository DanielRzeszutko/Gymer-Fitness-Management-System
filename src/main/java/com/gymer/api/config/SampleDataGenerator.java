package com.gymer.api.config;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.workinghours.entity.Day;
import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Date;
import java.sql.Time;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
public class SampleDataGenerator {

    private final PartnerService partnerService;

    @Autowired
    public SampleDataGenerator(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostConstruct
    public void init() {
        for (int i = 0; i < 10; i++) {
            partnerService.updatePartner(getRandomPartner());
        }
    }

    private Partner getRandomPartner() {
        List<Employee> employees = getRandomEmployees(new Random().nextInt(1) + 3);
        List<Slot> slots = new LinkedList<>();
        for (Employee employee : employees) {
            slots.addAll(getRandomSlots(employee));
        }
        return new Partner(0L, createRandomWord(6), createRandomWord(6), createRandomWord(20),
                createRandomWord(12), getRandomCredential(), getRandomAddress(), employees,
                slots, getRandomWorkingHours());
    }

    private List<Slot> getRandomSlots(Employee employee) {
        List<Slot> slots = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            slots.add(getRandomSlot(employee));
        }
        return slots;
    }

    private Slot getRandomSlot(Employee employee) {
        int startHour = new Random().nextInt(12) + 6;
        int endHour = startHour + 1;
        String startHourString = startHour < 10 ? "0" + startHour : Integer.toString(startHour);
        String endHourString = endHour < 10 ? "0" + endHour : Integer.toString(endHour);
        return new Slot(
                0L, Date.valueOf("2021-02-10"), Time.valueOf(startHourString + ":00:00"),
                Time.valueOf(endHourString + ":00:00"), Collections.emptyList(), employee, startHour < 12);
    }

    private List<Employee> getRandomEmployees(int howMuch) {
        List<Employee> employees = new LinkedList<>();
        for (int i = 0; i < howMuch; i++) {
            employees.add(getRandomEmployee());
        }
        return employees;
    }

    private List<WorkingHour> getRandomWorkingHours() {
        List<WorkingHour> workingHours = new LinkedList<>();
        for (int i = 0; i < Day.values().length; i++) {
            int startHour = new Random().nextInt(12) + 7;
            int endHour = startHour + 8;
            if (startHour >= 18) continue;
            String startHourString = startHour < 10 ? "0" + startHour : Integer.toString(startHour);
            String endHourString = endHour < 10 ? "0" + endHour : Integer.toString(endHour);
            workingHours.add(new WorkingHour(0L, Day.values()[i], Time.valueOf(startHourString + ":00:00"), Time.valueOf(endHourString + ":00:00")));
        }
        return workingHours;
    }

    private Employee getRandomEmployee() {
        return new Employee(0L, createRandomWord(6), createRandomWord(6), createRandomWord(6), createRandomWord(6), getRandomWorkingHours());
    }

    private Address getRandomAddress() {
        return new Address(0L, createRandomWord(6), createRandomWord(10), createRandomNumber(1, 50), createRandomNumber(10000, 80000));
    }

    private Credential getRandomCredential() {
        return new Credential(0L, createRandomWord(12), createRandomWord(8), createRandomNumber(10000000, 90000000), true);
    }

    private String createRandomNumber(int from, int to) {
        Random random = new Random();
        return Integer.toString(random.nextInt(to) + from);
    }

    private String createRandomWord(int length) {
        StringBuilder word = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int v = 1 + (int) (Math.random() * 26);
            char c = (char) (v + (i == 0 ? 'A' : 'a') - 1);
            word.append(c);
        }
        return word.toString();
    }

}
