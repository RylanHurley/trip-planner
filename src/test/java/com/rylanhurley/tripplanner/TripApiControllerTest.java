package com.rylanhurley.tripplanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TripApiControllerTest {

    @Autowired MockMvc mvc;
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;

    @Test
    void unknownTokenIs404() throws Exception {
        mvc.perform(get("/api/groups/nope/trips")).andExpect(status().isNotFound());
    }

    @Test
    void returnsTripsWithoutOrganizerToken() throws Exception {
        TripGroup group = new TripGroup();
        group.setName("Ski crew");
        groupRepository.save(group);

        Trip trip = new Trip();
        trip.setName("Breck");
        trip.setStartDate(LocalDate.of(2027, 1, 8));
        trip.setEndDate(LocalDate.of(2027, 1, 10));
        trip.setTargetSize(6);
        trip.setCommitDeadline(LocalDate.of(2027, 1, 1));
        trip.setTripGroup(group);
        tripRepository.save(trip);

        mvc.perform(get("/api/groups/" + group.getInviteToken() + "/trips"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Breck"))
                .andExpect(jsonPath("$[0].organizerToken").doesNotExist());
    }
}