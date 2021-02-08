package com.gymer.api.config;

import com.gymer.api.address.entity.Address;
import com.gymer.api.credential.entity.Credential;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Component
public class SampleDataGenerator {

    private final PartnerService partnerService;

    @Autowired
    public SampleDataGenerator(PartnerService partnerService) {
        this.partnerService = partnerService;
    }

    @PostConstruct
    public void init() {
        Address address = new Address(0L, "city", "street", "number", "zipCode");
        Address address2 = new Address(0L, "city2", "street2", "number2", "zipCode2");
        Credential credential = new Credential(0L, "email", "password", "phone", true);
        Credential credential2 = new Credential(0L, "email2", "password2", "phone2", true);
        Partner partner = new Partner(0L, "name", "logo", "desc", "website", credential, address, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        Partner partner2 = new Partner(0L, "name2", "logo2", "desc2", "website2", credential2, address2, Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        partnerService.updatePartner(partner);
        partnerService.updatePartner(partner2);
    }

}
