package com.rylanhurley.tripplanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class CostLinkTest {

    @Autowired MockMvc mvc;
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;
    @Autowired CostItemRepository costItemRepository;

    private TripGroup group;
    private Trip trip;

    private void makeTrip() {
        group = new TripGroup();
        group.setName("Ski crew");
        groupRepository.save(group);
        trip = new Trip();
        trip.setName("Breck");
        trip.setStartDate(LocalDate.of(2027, 1, 8));
        trip.setEndDate(LocalDate.of(2027, 1, 10));
        trip.setTargetSize(6);
        trip.setCommitDeadline(LocalDate.of(2027, 1, 1));
        trip.setTripGroup(group);
        tripRepository.save(trip);
    }

    private void addCost(String link) throws Exception {
        mvc.perform(post("/g/" + group.getInviteToken() + "/trips/" + trip.getId() + "/costs")
                .param("org", group.getOrganizerToken())
                .param("name", "Cabin")
                .param("amount", "100")
                .param("splitType", "SPLIT")
                .param("link", link));
    }

    @Test
    void httpsLinkIsSaved() throws Exception {
        makeTrip();
        addCost("https://venmo.com/u/rylan");
        assertEquals("https://venmo.com/u/rylan", costItemRepository.findByTrip(trip).get(0).getLink());
    }

    @Test
    void javascriptLinkIsRejected() throws Exception {
        makeTrip();
        addCost("javascript:alert(1)");
        assertEquals(0, costItemRepository.findByTrip(trip).size());
    }
}