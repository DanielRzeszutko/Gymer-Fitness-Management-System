package com.gymer.resources.credential;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymer.resources.credential.entity.Credential;
import com.gymer.resources.credential.entity.CredentialDTO;
import com.gymer.resources.credential.entity.Role;
import com.gymer.resources.partner.PartnerService;
import com.gymer.resources.partner.entity.Partner;
import com.gymer.resources.user.UserService;
import com.gymer.resources.user.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
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
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
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
public class CredentialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CredentialService credentialService;

    @MockBean
    private PartnerService partnerService;

    @MockBean
    private UserService userService;

    @InjectMocks
    private CredentialController credentialController;

    private final Pageable pageable = PageRequest.of(0, 20);
    private final Page<Credential> page = getTestPageData();
    private final Timestamp timestamp = Timestamp.valueOf("2021-10-20 10:15:10");

    @Test
    public void contextLoads() {
        assertThat(credentialController).isNotNull();
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecords() throws Exception {
        given(credentialService.findAllContaining(pageable, "")).willReturn(page);

        mockMvc.perform(get("/api/credentials")
                .header("Origin", "*")
                .param("contains", "")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetPageWithRecordsWithoutSearchBy() throws Exception {
        given(credentialService.getAllElements(pageable)).willReturn(page);

        mockMvc.perform(get("/api/credentials")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnOKStatus_when_tryingToGetSpecificRecordWithId() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);

        given(credentialService.getElementById(1L)).willReturn(credential);

        mockMvc.perform(get("/api/credentials/1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordWithNonExistingId() throws Exception {
        given(credentialService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/credentials/-1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetSpecificRecordForPartner() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        Partner partner = new Partner("", "", "", "", "", credential, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(get("/api/partners/1/credentials/1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordFromPartnerWithNonExistingId() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(5L);
        Partner partner = new Partner("", "", "", "", "", credential, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(credentialService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        given(partnerService.getElementById(1L)).willReturn(partner);

        mockMvc.perform(get("/api/partners/1/credentials/-1")
                .header("Origin", "*")
                .with(user("partner").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnOkStatus_when_tryingToUpdateExistingCredentialsForPartner() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        Partner partner = new Partner("", "", "", "", "", credential, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(partnerService.getElementById(1L)).willReturn(partner);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/partners/1/credentials/1")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnConflictStatus_when_tryingToUpdateCredentialWhenUrlIsNotValidForPartner() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        Partner partner = new Partner("", "", "", "", "", credential, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(partnerService.getElementById(1L)).willReturn(partner);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/partners/1/credentials/2")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_returnBadRequestStatus_when_tryingToUpdateCredentialWhenObjectIdNotEqualToUrlIdForPartner() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        Credential credential2 = new Credential("", "", "", Role.USER, true, timestamp);
        credential2.setId(2L);
        Partner partner = new Partner("", "", "", "", "", credential2, null,
                Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(partnerService.getElementById(1L)).willReturn(partner);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/partners/1/credentials/1")
                .with(user("partner").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnOkStatus_when_tryingToGetSpecificRecordForUser() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        User user = new User("", "", credential);
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(userService.getElementById(1L)).willReturn(user);

        mockMvc.perform(get("/api/users/1/credentials/1")
                .header("Origin", "*")
                .with(user("user").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnNotFoundStatus_when_tryingToGetSpecificRecordFromUserWithNonExistingId() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(5L);
        User user = new User("", "", credential);
        given(credentialService.getElementById(-1L)).willThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        given(userService.getElementById(1L)).willReturn(user);

        mockMvc.perform(get("/api/users/1/credentials/-1")
                .header("Origin", "*")
                .with(user("user").roles("ADMIN")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_returnOkStatus_when_tryingToUpdateExistingCredentialsForUser() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        User user = new User("", "", credential);
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(userService.getElementById(1L)).willReturn(user);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/users/1/credentials/1")
                .with(user("user").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isOk());
    }

    @Test
    public void should_returnConflictStatus_when_tryingToUpdateCredentialWhenUrlIsNotValidForUser() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        User user = new User("", "", credential);
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(userService.getElementById(1L)).willReturn(user);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/users/1/credentials/2")
                .with(user("user").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isConflict());
    }

    @Test
    public void should_returnBadRequestStatus_when_tryingToUpdateCredentialWhenObjectIdNotEqualToUrlIdForUser() throws Exception {
        Credential credential = new Credential("", "", "", Role.USER, true, timestamp);
        credential.setId(1L);
        Credential credential2 = new Credential("", "", "", Role.USER, true, timestamp);
        credential2.setId(2L);
        User user = new User("", "", credential2);
        given(credentialService.getElementById(1L)).willReturn(credential);
        given(userService.getElementById(1L)).willReturn(user);
        CredentialDTO credentialDTO = new CredentialDTO(credential);

        mockMvc.perform(put("/api/users/1/credentials/1")
                .with(user("user").roles("ADMIN"))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(credentialDTO))
                .header("Origin", "*"))
                .andExpect(status().isBadRequest());
    }

    private Page<Credential> getTestPageData() {
        List<Credential> credentials = new LinkedList<>();
        credentials.add(new Credential("", "", "", Role.USER, true, timestamp));
        credentials.add(new Credential("", "", "", Role.USER, true, timestamp));
        credentials.add(new Credential("", "", "", Role.USER, true, timestamp));
        return new PageImpl<>(credentials);
    }

}
