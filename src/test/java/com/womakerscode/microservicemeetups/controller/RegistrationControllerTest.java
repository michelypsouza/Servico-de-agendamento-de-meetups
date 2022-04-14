package com.womakerscode.microservicemeetups.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.womakerscode.microservicemeetups.controller.dto.RegistrationDTO;
import com.womakerscode.microservicemeetups.controller.resource.RegistrationController;
import com.womakerscode.microservicemeetups.exception.BusinessException;
import com.womakerscode.microservicemeetups.model.entity.Registration;
import com.womakerscode.microservicemeetups.service.RegistrationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Calendar;
import java.util.Locale;
import java.util.Optional;

import static com.womakerscode.microservicemeetups.util.DateUtil.formatDateToString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = {RegistrationController.class})
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    static String REGISTRATION_API = "/api/registration";

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RegistrationService registrationService;

    @Test
    @DisplayName("Should create a registration with success")
    public void createRegistrationTest() throws Exception {

        // cenario
        RegistrationDTO registrationDTOBuilder = createNewRegistration();
        Registration savedRegistration = Registration.builder()
                .id(101)
                .name("Michely Souza")
                //.dateOfRegistration(LocalDate.of(2022,4,1))
                //.dateOfRegistration(LocalDate.now())
                .dateOfRegistration(Calendar.getInstance(Locale.getDefault()).getTime())
                .registration("001").build();

        // execucao
        BDDMockito.given(registrationService.save(any(Registration.class))).willReturn(savedRegistration);

        String json = new ObjectMapper().writeValueAsString(registrationDTOBuilder);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        // verificacao, assert
        mockMvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(101))
                .andExpect(jsonPath("name").value(registrationDTOBuilder.getName()))
                //.andExpect(jsonPath("dateOfRegistration").value(registrationDTOBuilder.getDateOfRegistration()))
                .andExpect(jsonPath("dateOfRegistration")
                        .value(formatDateToString(registrationDTOBuilder.getDateOfRegistration())))
                .andExpect(jsonPath("registration").value(registrationDTOBuilder.getRegistration()));

    }

    @Test
    @DisplayName("Should throw an exception when not have data enough for the test")
    public void createInvalidRegistrationTest() throws  Exception{

        String json = new ObjectMapper().writeValueAsString(new Registration());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should throw an exception when try to create a new registration with an registration already created.")
    public void createRegistrationWithDuplicatedRegistration() throws Exception {

        RegistrationDTO dto = createNewRegistration();
        String json  = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(registrationService.save(any(Registration.class)))
                .willThrow(new BusinessException("Registration already created!"));

        MockHttpServletRequestBuilder request  = MockMvcRequestBuilders
                .post(REGISTRATION_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mockMvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Registration already created!"));
    }

    @Test
    @DisplayName("Should get registration information")
    public void getRegistrationTest() throws Exception {

        Integer id = 11;

        Registration registration = Registration.builder()
                .id(id)
                .name(createNewRegistration().getName())
                //.dateOfRegistration(createNewRegistration().getDateOfRegistration())
                //.dateOfRegistration(LocalDate.now())
                .dateOfRegistration(Calendar.getInstance(Locale.getDefault()).getTime())
                .registration(createNewRegistration().getRegistration())
                .build();

        BDDMockito.given(registrationService.getRegistrationById(id)).willReturn(Optional.of(registration));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
                .perform(requestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(registration.getId()))
                .andExpect(jsonPath("name").value(registration.getName()))
                .andExpect(jsonPath("dateOfRegistration")
                        .value(formatDateToString(registration.getDateOfRegistration())))
                .andExpect(jsonPath("registration").value(registration.getRegistration()));

    }

    @Test
    @DisplayName("Should return NOT FOUND when the registration doesn't exists")
    public void registrationNotFoundTest() throws Exception {

        BDDMockito.given(registrationService.getRegistrationById(anyInt())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(REGISTRATION_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(status().isNotFound());

    }

    private RegistrationDTO createNewRegistration() {
        return RegistrationDTO.builder()
                //.id(101)
                .name("Michely Souza")
                .dateOfRegistration(Calendar.getInstance(Locale.getDefault()).getTime())
                //.dateOfRegistration(DateUtil.formatDateToString(LocalDate.now()))
                //.dateOfRegistration(LocalDate.of(2022,4,1))
                //.dateOfRegistration(LocalDate.now())
                //.dateOfRegistration(DateTimeFormatter.ofPattern(DATE_PATTERN_DEFAULT).format(LocalDate.now()))
                .registration("001").build();
    }

}
