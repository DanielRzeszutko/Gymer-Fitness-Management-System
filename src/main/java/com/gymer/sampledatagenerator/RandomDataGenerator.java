package com.gymer.sampledatagenerator;

import com.gymer.commonresources.address.entity.Address;
import com.gymer.commonresources.credential.CredentialService;
import com.gymer.commonresources.credential.entity.Credential;
import com.gymer.commonresources.credential.entity.Role;
import com.gymer.commonresources.employee.entity.Employee;
import com.gymer.commonresources.partner.PartnerService;
import com.gymer.commonresources.partner.entity.Partner;
import com.gymer.commonresources.slot.entity.Slot;
import com.gymer.commonresources.user.UserService;
import com.gymer.commonresources.user.entity.User;
import com.gymer.commonresources.workinghours.entity.Day;
import com.gymer.commonresources.workinghours.entity.WorkingHour;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;

/**
 * Generator class providing generating all random data for our project needs.
 * For proper operation of our generator there should be 'sampleData' folder
 * with text files in the resources directory.
 * Necessary txt files: 'cities.txt', 'companies.txt', 'emails.txt', 'names.txt', 'streets.txt', 'surnames.txt'
 */
@Component
@RequiredArgsConstructor
class RandomDataGenerator {

    private final Random RANDOM = new Random();
    private final List<String> usedEmails = new ArrayList<>();
    private final PartnerService partnerService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CredentialService credentialservice;
    private List<String> cities;
    private List<String> nameList;
    private List<String> surnameList;
    private List<String> emails;
    private List<String> streets;
    private List<String> companies;
    private List<String> descriptions;

    /**
     * Initial method that fills database with 10 random partners, 10 random users and with
     * test_user and admin_user for tests if they not exist already in database
     *
     * @throws FileNotFoundException - if any file not exist in resources.
     */
    public void init() throws FileNotFoundException {
        fillListsWithData();

        addTestPartner();
        addAdminIfDoesntExists();
        User testUser = getUserIfExist();

        for (int i = 0; i < 10; i++) {
            User user = getRandomUser();
            userService.updateElement(user);
            Partner partner = getRandomPartner();
            Slot slot = partner.getSlots().get(i);
            if (slot.isPrivate() && slot.getUsers().size() == 0) {
                slot.setUsers(List.of(user));
            }
            partner.getSlots().get(i).setUsers(List.of(testUser));
            partnerService.updateElement(partner);
        }
    }

    private void addTestPartner() {
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        Credential credential = new Credential("testpartner@gmail.com", "$2a$10$7aFEbq/nmgvT1kuMhjXUc.g3jlk4.Bt7FfQMAF61m1Y78MhdS/6b2",
                "999999999", Role.PARTNER, true, time);

        List<Employee> employees = getRandomEmployees(getRandomNumberBetween(1, 3));
        List<Slot> slots = new LinkedList<>();
        for (Employee employee : employees) {
            slots.addAll(getRandomSlots(employee));
        }

        Partner partner = new Partner("TESTPARTNER", "logo", "image", "TEST OPIS", "website",
                credential, getRandomAddress(), employees, slots, getRandomWorkingHours());
        if (!credentialservice.isCredentialExistsByEmail(partner.getCredential().getEmail())) {
            partnerService.updateElement(partner);
        }
    }

    private User getUserIfExist() {
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        if (!credentialservice.isCredentialExistsByEmail("test@gmail.com")) {
            User testUser = new User("TEST", "TEST", new Credential("test@gmail.com", "$2a$10$7aFEbq/nmgvT1kuMhjXUc.g3jlk4.Bt7FfQMAF61m1Y78MhdS/6b2",
                    "999999999", Role.USER, true, time));
            userService.updateElement(testUser);
            return testUser;
        } else {
            return userService.getByCredentials(credentialservice.getCredentialByEmail("test@gmail.com"));
        }
    }

    private void fillListsWithData() throws FileNotFoundException {
        nameList = readDataFromFile("names.txt");
        surnameList = readDataFromFile("surnames.txt");
        emails = readDataFromFile("emails.txt");
        cities = readDataFromFile("cities.txt");
        streets = readDataFromFile("streets.txt");
        companies = readDataFromFile("companies.txt");
        descriptions = readDataFromFile("descriptions.txt");
    }

    private void addAdminIfDoesntExists() {
        Timestamp time = new Timestamp(new java.util.Date().getTime());
        User adminUser = new User("ADMIN", "ADMIN", new Credential("admin@gmail.com", "$2a$10$OyyKn5189yggrUjbPsZytezro033h6qYCQaMAVz2RaUtZ6hBWFAOy",
                "000000000", Role.ADMIN, true, time));
        if (!credentialservice.isCredentialExistsByEmail(adminUser.getCredential().getEmail())) {
            userService.updateElement(adminUser);
        }
    }

    private List<String> readDataFromFile(String fileName) throws FileNotFoundException {
        final String ADDON_URL = "src/main/resources/sampleData/";
        List<String> result = new ArrayList<>();

        File file = new File(ADDON_URL + fileName);
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
        List<Employee> employees = getRandomEmployees(getRandomNumberBetween(1, 3));
        List<Slot> slots = new LinkedList<>();
        for (Employee employee : employees) {
            slots.addAll(getRandomSlots(employee));
        }
        String gymName = getRandomGymName();
        return new Partner(gymName, "../../images/randomLogoGym.jpg", "../../images/gym8.jpg", getRandomDescription(),
                getRandomWebsite(gymName), getRandomCredential(gymName, "business", Role.PARTNER),
                getRandomAddress(), employees, slots, getRandomWorkingHours());
    }

    private User getRandomUser() {
        String name = getRandomName();
        String surname = getRandomSurname();
        return new User(name, surname, getRandomCredential(name, surname, Role.USER));
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
        boolean isPrivate = startHour > 12;
        Integer size = isPrivate ? 1 : 10;
        String description = isPrivate ? "Personal Training" : getRandomSlotDescription();

        return new Slot(description, getRandomDateFromNow(), Time.valueOf(startHourString + ":00:00"),
                Time.valueOf(endHourString + ":00:00"), Collections.emptyList(), employee, description, isPrivate, size);
    }

    private Date getRandomDateFromNow() {
        int howMuchDayBack = 15;
        int howMuchDayForth = 30;
        Timestamp time = new Timestamp(System.currentTimeMillis());
        LocalDate now = time.toLocalDateTime().toLocalDate();
        int dayToAdd = getRandomNumberBetween(-howMuchDayBack, howMuchDayForth);
        LocalDate date = now.plusDays(dayToAdd);
        return Date.valueOf(date);
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
            workingHours.add(new WorkingHour(Day.values()[i], Time.valueOf(startHourString + ":00:00"), Time.valueOf(endHourString + ":00:00")));
        }
        return workingHours;
    }

    private Employee getRandomEmployee() {
        String name = getRandomName();
        String surname = getRandomSurname();
        return new Employee(name, surname, "Hello. My name is " + name + " " + surname,
                "../../images/employee.jpg", getRandomWorkingHours());
    }

    private Address getRandomAddress() {
        new Address();
        return new Address(getRandomCityName(), getRandomStreetName(),
                String.valueOf(getRandomNumberBetween(1, 200)), getRandomZipcode());
    }

    private Credential getRandomCredential(String name, String secondName, Role role) {
        Timestamp timestamp = new Timestamp(new java.util.Date().getTime());
        return new Credential(getRandomEmail(name, secondName), getRandomPassword(), getRandomPhoneNumber(), role, true, timestamp);
    }

    private String getRandomSlotDescription() {
        List<String> list = List.of("Full body workout", "Dance step", "Indoor cycling",
                "Kettlebells", "Mobility & Stability", "Stretching", "TRX");
        return list.get(getRandomNumberBetween(0, list.size()));
    }

    private String getRandomPassword() {
        StringBuilder password = new StringBuilder();
        int length = getRandomNumberBetween(8, 16);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz012345678";
        String specialChars = "(!@#$%^&*(){}[]\\|:\";'<>?,./";
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(getRandomNumberBetween(0, chars.length() - 1)));
        }
        password.append(specialChars.charAt(getRandomNumberBetween(0, specialChars.length() - 1)));
        return passwordEncoder.encode(password);
    }

    private String getRandomEmail(String name, String surname) {
        String email;
        do {
            int randomInt = getRandomNumberBetween(0, 10000);
            String end = emails.get(getRandomNumberBetween(0, emails.size() - 1));

            email = name.toLowerCase().replace(" ", "") + surname.toLowerCase() + randomInt + end;
        } while (usedEmails.contains(email));

        usedEmails.add(email);
        return email;
    }

    private int getRandomNumberBetween(int lower, int upper) {
        return lower + RANDOM.nextInt(upper - lower);
    }

    private String getRandomName() {
        return nameList.get(getRandomNumberBetween(0, nameList.size() - 1));
    }

    private String getRandomSurname() {
        return surnameList.get(getRandomNumberBetween(0, surnameList.size() - 1));
    }

    private String getRandomGymName() {
        return companies.get(getRandomNumberBetween(0, companies.size() - 1));
    }

    private String getRandomStreetName() {
        return streets.get(getRandomNumberBetween(0, streets.size() - 1));
    }

    private String getRandomCityName() {
        return cities.get(getRandomNumberBetween(0, cities.size() - 1));
    }

    private String getRandomZipcode() {
        return "" + getRandomNumberBetween(10, 100) + getRandomNumberBetween(100, 1000);
    }

    private String getRandomWebsite(String gymName) {
        return "www." + gymName.toLowerCase().replace(" ", "") + ".com";
    }

    private String getRandomPhoneNumber() {
        return String.valueOf(getRandomNumberBetween(100000000, 999999999));
    }

    private String getRandomDescription() {
        return descriptions.get(getRandomNumberBetween(0, descriptions.size() - 1));
    }

}
