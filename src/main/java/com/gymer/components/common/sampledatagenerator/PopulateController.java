package com.gymer.components.common.sampledatagenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController("/populate")
public class PopulateController {

    private final RandomDataGenerator dataGenerator;

    @Autowired
    public PopulateController(RandomDataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @GetMapping
    public void populateDB() {
        try {
            dataGenerator.init();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }

}
