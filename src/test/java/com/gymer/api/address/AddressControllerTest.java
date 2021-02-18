package com.gymer.api.address;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.api.address.entity.Address;
import com.gymer.api.address.entity.AddressDTO;
import com.gymer.api.partner.PartnerService;
import com.gymer.api.partner.entity.Partner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AddressService addressService;

    @MockBean
    private PartnerService partnerService;

    @InjectMocks
    private AddressController addressController;

    private final Pageable pageable = PageRequest.of(0, 20);
    private final Page<Address> page = getTestPageData();

    @Test
    public void contextLoads() {
        assertThat(addressController).isNotNull();
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecords() throws Exception {
        given(addressService.findAllContaining(pageable, "")).willReturn(page);

        mockMvc.perform(get("/api/addresses")
                .header("Origin", "*")
                .param("contains", "")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecordsWithoutSearchBy() throws Exception {
        given(addressService.getAllElements(pageable)).willReturn(page);

        mockMvc.perform(get("/api/addresses")
                .header("Origin", "*")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOKStatus_when_tryingToGetSpecificRecordWithId() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        given(addressService.getElementById(1L)).willReturn(address);

        mockMvc.perform(get("/api/addresses/1")
                .header("Origin", "*")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordWithNonExistingId() throws Exception {
        given(addressService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/addresses/-1")
                .header("Origin", "*")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetSpecificRecordForPartner() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(addressService.getElementById(1L)).willReturn(address);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(get("/api/partners/1/addresses/1")
                .header("Origin", "*")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordFromPartnerWithNonExistingId() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(addressService.getElementById(1L)).willReturn(address);
        given(partnerService.getElementById(1L)).willReturn(partner);
        given(addressService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));

        mockMvc.perform(get("/api/partners/1/addresses/-1")
                .header("Origin", "*")
                .with(user("partner").roles("PARTNER")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnOkStatus_when_tryingToUpdateExistingAddress() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        AddressDTO addressDTO = new AddressDTO(address);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(put("/api/partners/1/addresses/1")
                .with(user("partner").roles("PARTNER"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addressDTO))
                .header("Origin", "*"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnConflictStatus_when_tryingToUpdateAddressWhenUrlIsNotValid() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        Partner partner = new Partner("", "", "", "", "", null, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        AddressDTO addressDTO = new AddressDTO(address);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(put("/api/partners/1/addresses/2")
                .with(user("partner").roles("PARTNER"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addressDTO))
                .header("Origin", "*"))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_returnBadRequestStatus_when_tryingToUpdateAddressWhenAddressObjectIdNotEqualToUrlId() throws Exception {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        Address address2 = new Address("City", "Street", "Number", "ZipCode");
        address2.setId(3L);
        Partner partner = new Partner("", "", "", "", "", null, address,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        AddressDTO addressDTO = new AddressDTO(address2);
        given(addressService.getElementById(1L)).willReturn(address);
        given(addressService.getElementById(3L)).willReturn(address2);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(put("/api/partners/1/addresses/3")
                .with(user("partner").roles("PARTNER"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(addressDTO))
                .header("Origin", "*"))
                .andExpect(status().isBadRequest());
    }

    private Page<Address> getTestPageData() {
        List<Address> addresses = new LinkedList<>();
        addresses.add(new Address("City1", "Street1", "Number1", "ZipCode1"));
        addresses.add(new Address("City2", "Street2", "Number2", "ZipCode2"));
        addresses.add(new Address("City3", "Street3", "Number3", "ZipCode3"));
        return new PageImpl<>(addresses, pageable, addresses.size());
    }

}
