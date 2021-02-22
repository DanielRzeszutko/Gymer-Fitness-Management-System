package com.gymer.components.common.sampledatagenerator;

import com.gymer.components.common.entity.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.spring.web.json.Json;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/populate")
public class PopulateController {

    private final RandomDataGenerator dataGenerator;

    @Autowired
    public PopulateController(RandomDataGenerator dataGenerator) {
        this.dataGenerator = dataGenerator;
    }

    @GetMapping
    public JsonResponse populateDB() {
        try {
            dataGenerator.init();
            return new JsonResponse("Test data initialized.", false);
        } catch (FileNotFoundException e) {
            return new JsonResponse("Error with initializing test data.", true);
        }
    }

}
