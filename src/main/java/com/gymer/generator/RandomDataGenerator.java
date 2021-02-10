package com.gymer.generator;

import com.gymer.GymerApplication;
import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
import com.gymer.api.employee.entity.Employee;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import com.gymer.api.slot.entity.Slot;
import com.gymer.api.user.UserService;
import com.gymer.api.user.entity.User;
import com.gymer.api.workinghours.entity.Day;
import com.gymer.api.workinghours.entity.WorkingHour;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URL;
import java.sql.Date;
import java.sql.Time;
import java.util.*;

@Component
public class RandomDataGenerator {

    /**
     * For proper operation of our generator there should be 'sampleData' folder
     * with text files in the resources directory.
     * Necessary txt files: 'cities.txt', 'companies.txt', 'emails.txt', 'names.txt', 'streets.txt', 'surnames.txt'
     */

    private final Random RANDOM = new Random();
    private List<String> cities;
    private List<String> nameList;
    private List<String> surnameList;
    private List<String> emails;
    private List<String> streets;
    private List<String> companies;
    private final List<String> usedEmails = new ArrayList<>();
    private final PartnerService partnerService;
    private final UserService userService;

    @Autowired
    public RandomDataGenerator(PartnerService partnerService, UserService userService) {
        this.partnerService = partnerService;
        this.userService = userService;
    }

    @PostConstruct
    public void init() throws FileNotFoundException {

        nameList = readDataFromFile("src/main/resources/names.txt");
        surnameList = readDataFromFile("src/main/resources/surnames.txt");
        emails = readDataFromFile("src/main/resources/emails.txt");
        cities = readDataFromFile("src/main/resources/cities.txt");
        streets = readDataFromFile("src/main/resources/streets.txt");
        companies = readDataFromFile("src/main/resources/companies.txt");

        for (int i = 0; i < 10; i++) {
            partnerService.updatePartner(getRandomPartner());
            userService.updateUser(getRandomUser());
        }
    }

    private List<String> readDataFromFile(String fileName) throws FileNotFoundException {
        List<String> result = new ArrayList<>();

        File file = new File(fileName);
        InputStream is = new FileInputStream(file);

        try {
            Scanner scanner = new Scanner(is);
            while (scanner.hasNextLine()) {
                result.add(scanner.nextLine());
            }
        } catch (NullPointerException e) {
            System.out.println("File not found");
        }
        return result;
    }

    private Partner getRandomPartner() {
        List<Employee> employees = getRandomEmployees(getRandomNumberBetween(1,3));
        List<Slot> slots = new LinkedList<>();
        for (Employee employee : employees) {
            slots.addAll(getRandomSlots(employee));
        }
        new Partner();
        String gymName = getRandomGymName();
        return new Partner(0L, gymName, "logo" + gymName, "Lorem ipsum" + createRandomWord(20),
                getRandomWebsite(gymName), getRandomCredential(gymName, "business", Role.PARTNER),
                getRandomAddress(), employees, slots, getRandomWorkingHours());
    }

    private User getRandomUser() {
        String name = getRandomName();
        String surname = getRandomSurname();
        return new User(0L, name, surname, getRandomCredential(name, surname, Role.USER));
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
        return new Employee(0L, getRandomName(), getRandomSurname(),
                "Lorem Ipsum Lorem Ipsum", "image", getRandomWorkingHours());
    }

    private Address getRandomAddress() {
        new Address();
        return new Address(0L, getRandomCityName(), getRandomStreetName(),
                String.valueOf(getRandomNumberBetween(1, 200)), getRandomZipcode());
    }

    private Credential getRandomCredential(String name, String secondName, Role role) {
        return new Credential(0L, getRandomEmail(name, secondName), getRandomPassword(), getRandomPhoneNumber(), role, true);
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

    private String getRandomPassword() {
        StringBuilder password = new StringBuilder();
        int length = getRandomNumberBetween(8,16);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345678";
        String specialChars = "(!@#$%^&*(){}[]\\|:\";'<>?,./";
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(getRandomNumberBetween(0, chars.length() - 1)));
        }
        password.append(specialChars.charAt(getRandomNumberBetween(0, specialChars.length() - 1)));
        return password.toString();
    }

    private String getRandomEmail(String name, String surname) {
        String email;
        do {
            int randomInt = getRandomNumberBetween(0, 10000);
            String end = emails.get(getRandomNumberBetween(0, emails.size() - 1));

            email = name.toLowerCase() + surname.toLowerCase() + randomInt + end;
        } while (usedEmails.contains(email));

        usedEmails.add(email);
        return email;
    }

    private int getRandomNumberBetween(int lower, int upper) {
        return lower + RANDOM.nextInt(upper - lower);
    }

    private String getRandomName() {
        return nameList.get(getRandomNumberBetween(0, nameList.size()-1));
    }

    private String getRandomSurname() {
        return surnameList.get(getRandomNumberBetween(0, surnameList.size()-1));
    }

    private String getRandomGymName() {
        return companies.get(getRandomNumberBetween(0, companies.size()-1));
    }

    private String getRandomStreetName() {
        return streets.get(getRandomNumberBetween(0, streets.size()-1));
    }

    private String getRandomCityName() {
        return cities.get(getRandomNumberBetween(0, cities.size()-1));
    }

    private String getRandomZipcode() {
        return getRandomNumberBetween(10,100) + "-"
                + getRandomNumberBetween(100,1000);
    }

    private String getRandomWebsite(String gymName) {
        return "www." + gymName.toLowerCase() + ".pl" ;
    }

    private String getRandomPhoneNumber() {
        return String.valueOf(getRandomNumberBetween(100000000, 999999999));
    }

}
