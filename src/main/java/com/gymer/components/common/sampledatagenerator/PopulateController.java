package com.gymer.components.common.sampledatagenerator;

import com.gymer.components.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
            return new JsonResponse("Test data initialized.", false);
        } catch (FileNotFoundException e) {
            return new JsonResponse("Error with initializing test data.", true);
        }
    }

}
