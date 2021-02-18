package com.gymer.api.credential;

import com.gymer.api.credential.entity.Credential;
import com.gymer.api.credential.entity.Role;
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

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;

    @InjectMocks
    private CredentialService credentialService;

    private final Pageable pageable = PageRequest.of(0, 10);
    private final Page<Credential> page = getTestPageData();
    private final Timestamp timestamp = Timestamp.valueOf("2021-10-20 10:15:10");

    @Test
    public void should_returnPageOfCredentials_when_findAllContainingWithValidData() {
        given(credentialRepository.findAllByEmailContainsOrPhoneNumberContains("", "", pageable)).willReturn(page);

        Page<Credential> obtainedPage = credentialService.findAllContaining(pageable, "");

        assertArrayEquals(page.getContent().toArray(new Credential[0]), obtainedPage.getContent().toArray(new Credential[0]));
    }

    @Test
    public void should_returnPageOfCredentials_when_getCredentialFromEmailPhoneAndRoleOrCreateNewOneWithValidData() {
        Credential credential = new Credential("", "", "", Role.USER, true, false, timestamp);
        given(credentialRepository.findByEmailAndPhoneNumberAndRole("", "", Role.USER)).willReturn(Optional.of(credential));

        Credential obtainedCredential = credentialService.getCredentialFromEmailPhoneAndRoleOrCreateNewOne("", "", Role.USER);

        assertEquals(credential, obtainedCredential);
    }

    @Test
    public void should_returnPageWithCredentials_when_getAllElementsWithValidData() {
        given(credentialRepository.findAll(pageable)).willReturn(page);

        Page<Credential> obtainedPage = credentialService.getAllElements(pageable);

        assertArrayEquals(page.getContent().toArray(new Credential[0]), obtainedPage.getContent().toArray(new Credential[0]));
    }

    @Test
    public void should_returnValidCredential_when_getElementByIdWithValidId() {
        Credential credential = new Credential("", "", "", Role.USER, true, false, timestamp);
        credential.setId(1L);
        given(credentialRepository.findById(1L)).willReturn(Optional.of(credential));

        Credential obtainedCredential = credentialService.getElementById(1L);

        assertEquals(credential, obtainedCredential);
    }

    @Test
    public void should_returnNotFoundError_when_getElementByIdWithNonExistingId() {
        given(credentialRepository.findById(1L)).willReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> credentialService.getElementById(1L));
    }

    @Test
    public void should_returnCredentialWithUpdatedId_when_updateElementWithNewAddress() {
        Credential expectedCredential = new Credential("", "", "123", Role.USER, true, false, timestamp);
        expectedCredential.setId(1L);
        Credential credential = new Credential("", "", "", Role.USER, true, false, timestamp);
        credential.setId(1L);
        given(credentialRepository.findById(1L)).willReturn(Optional.of(credential));

        credential.setPhoneNumber("123");
        credentialService.updateElement(credential);
        Credential obtainedCredential = credentialService.getElementById(1L);

        assertEquals(expectedCredential, obtainedCredential);
    }

    @Test
    public void should_returnTrue_when_isElementExistByIdWithValidData() {
        given(credentialRepository.existsById(1L)).willReturn(true);

        boolean obtainedStatus = credentialService.isElementExistById(1L);

        assertTrue(obtainedStatus);
    }

    @Test
    public void should_returnFalse_when_isElementExistByIdWithNonExistingAddress() {
        given(credentialRepository.existsById(1L)).willReturn(false);

        boolean obtainedStatus = credentialService.isElementExistById(1L);

        assertFalse(obtainedStatus);
    }

    private Page<Credential> getTestPageData() {
        List<Credential> credentials = new LinkedList<>();
        credentials.add(new Credential("", "", "", Role.USER, true, false, timestamp));
        credentials.add(new Credential("", "", "", Role.USER, true, false, timestamp));
        credentials.add(new Credential("", "", "", Role.USER, true, false, timestamp));
        return new PageImpl<>(credentials);
    }

}
