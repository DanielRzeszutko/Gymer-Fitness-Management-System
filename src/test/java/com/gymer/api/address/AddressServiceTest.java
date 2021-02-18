package com.gymer.api.address;

import com.gymer.api.address.entity.Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final Page<Address> page = getTestPageData();

    @Test
    public void should_returnPageOfAddresses_when_findAllContainingWithValidData() {
        given(addressRepository.findAllByCityContainsOrStreetContainsOrZipCodeContains("", "", "", pageable)).willReturn(page);

        Page<Address> obtainedPage = addressService.findAllContaining(pageable, "");

        assertArrayEquals(page.getContent().toArray(new Address[0]), obtainedPage.getContent().toArray(new Address[0]));
    }

    @Test
    public void should_returnPageWithAddresses_when_getAllElementsWithValidData() {
        given(addressRepository.findAll(pageable)).willReturn(page);

        Page<Address> obtainedPage = addressService.getAllElements(pageable);

        assertArrayEquals(page.getContent().toArray(new Address[0]), obtainedPage.getContent().toArray(new Address[0]));
    }

    @Test
    public void should_returnValidAddress_when_getElementByIdWithValidId() {
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        given(addressRepository.findById(1L)).willReturn(Optional.of(address));

        Address obtainedAddress = addressService.getElementById(1L);

        assertEquals(address, obtainedAddress);
    }

    @Test
    public void should_returnNotFoundError_when_getElementByIdWithNonExistingId() {
        given(addressRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> addressService.getElementById(1L));
    }

    @Test
    public void should_returnAddressWithUpdatedId_when_updateElementWithNewAddress() {
        Address expectedAddress = new Address("City", "Street", "Number", "NewZipCode");
        expectedAddress.setId(1L);
        Address address = new Address("City", "Street", "Number", "ZipCode");
        address.setId(1L);
        given(addressRepository.findById(1L)).willReturn(Optional.of(address));

        address.setZipCode("NewZipCode");
        addressService.updateElement(address);
        Address obtainedAddress = addressService.getElementById(1L);

        assertEquals(expectedAddress, obtainedAddress);
    }

    @Test
    public void should_returnTrue_when_isElementExistByIdWithValidData() {
        given(addressRepository.existsById(1L)).willReturn(true);

        boolean obtainedStatus = addressService.isElementExistById(1L);

        assertTrue(obtainedStatus);
    }

    @Test
    public void should_returnFalse_when_isElementExistByIdWithNonExistingAddress() {
        given(addressRepository.existsById(1L)).willReturn(false);

        boolean obtainedStatus = addressService.isElementExistById(1L);

        assertFalse(obtainedStatus);
    }

    private Page<Address> getTestPageData() {
        List<Address> addresses = new LinkedList<>();
        addresses.add(new Address("City1", "Street1", "Number1", "ZipCode1"));
        addresses.add(new Address("City2", "Street2", "Number2", "ZipCode2"));
        addresses.add(new Address("City3", "Street3", "Number3", "ZipCode3"));
        return new PageImpl<>(addresses);
    }

}
