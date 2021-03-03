package com.gymer.sampledatagenerator;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;

/**
 * Controller responsible for populating a database.
 * Provides one endpoint for any, anonymous or logged user.
 * Used in development process to generate random data for displaying.
 */
@RestController
@AllArgsConstructor
class PopulateController {

    private final RandomDataGenerator dataGenerator;

    /**
     * Endpoint that fills database with randomized data.
     *
     * @return JsonResponse - object with message and valid status if data is filled successfully or
     * message and invalid status if any error occurs during reading the text files.
     */
    @GetMapping("/api/populate")
    public void populateDB() {
        try {
            dataGenerator.init();
            throw new ResponseStatusException(HttpStatus.CREATED, "Test data initialized.");
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error with initializing test data.");
        }
    }

}
