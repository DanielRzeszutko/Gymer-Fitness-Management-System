package com.gymer.security.verification;

import com.gymer.common.entity.JsonResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;

    @GetMapping("/api/verify")
    public JsonResponse verifyAccount(@RequestParam("code") String code) {
        return verificationService.verify(code);
    }

}
