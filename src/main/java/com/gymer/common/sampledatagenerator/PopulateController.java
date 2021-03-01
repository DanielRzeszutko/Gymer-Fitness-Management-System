package com.gymer.common.sampledatagenerator;

import com.gymer.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;

@RestController
@AllArgsConstructor
public class PopulateController {

    private final RandomDataGenerator dataGenerator;

    @GetMapping("/api/populate")
    public JsonResponse populateDB() {
        try {
            dataGenerator.init();
            return JsonResponse.validMessage("Test data initialized.");
        } catch (FileNotFoundException e) {
            return JsonResponse.invalidMessage("Error with initializing test data.");
        }
    }

}
