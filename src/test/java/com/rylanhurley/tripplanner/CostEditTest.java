package com.rylanhurley.tripplanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CostEditTest {

    @Autowired MockMvc mvc;
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;
    @Autowired CostItemRepository costItemRepository;

    private TripGroup group;
    private Trip trip;
    private CostItem cost;

    private void makeTripWithCost() {
        group = new TripGroup();
        group.setName("Colorado");
        groupRepository.save(group);
        trip = new Trip();
        trip.setName("Breck");
        trip.setStartDate(LocalDate.of(2027, 1, 8));
        trip.setEndDate(LocalDate.of(2027, 1, 10));
        trip.setTargetSize(6);
        trip.setCommitDeadline(LocalDate.of(2027, 1, 1));
        trip.setTripGroup(group);
        tripRepository.save(trip);
        cost = new CostItem();
        cost.setName("Airbnb");
        cost.setAmount(new BigDecimal("1200.00"));
        cost.setSplitType(SplitType.SPLIT);
        cost.setTrip(trip);
        costItemRepository.save(cost);
    }

    private void edit(String org, String amount) throws Exception {
        mvc.perform(post("/g/" + group.getInviteToken() + "/trips/" + trip.getId() + "/costs/" + cost.getId())
                .param("org", org)
                .param("name", "Airbnb")
                .param("amount", amount)
                .param("splitType", "SPLIT"));
    }

    private BigDecimal savedAmount() {
        return costItemRepository.findById(cost.getId()).get().getAmount();
    }

    @Test
    void organizerCanChangeTheAmount() throws Exception {
        makeTripWithCost();
        edit(group.getOrganizerToken(), "900.00");
        assertEquals(0, new BigDecimal("900.00").compareTo(savedAmount()));
    }

    @Test
    void tripPageShowsForTheOrganizer() throws Exception {
        makeTripWithCost();
        mvc.perform(get("/g/" + group.getInviteToken() + "/trips/" + trip.getId()).param("org", group.getOrganizerToken()))
                .andExpect(status().isOk());
    }

    @Test
    void anyoneElseCannotChangeTheAmount() throws Exception {
       makeTripWithCost();
       edit(group.getOrganizerToken(), "900.00");
       edit("fake key", "90.00");
       assertEquals(0, new BigDecimal("900.00").compareTo(savedAmount()));
    }
}