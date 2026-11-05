package com.rylanhurley.tripplanner;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
class JoinLeaveTest {

    @Autowired MockMvc mvc;
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;
    @Autowired ParticipantRepository participantRepository;

    private TripGroup group;
    private Trip trip;

    private void makeTrip() {
        group = new TripGroup();
        group.setName("Colorado");
        groupRepository.save(group);
        trip = new Trip();
        trip.setName("Breck");
        trip.setStartDate(LocalDate.now().plusDays(60));
        trip.setEndDate(LocalDate.now().plusDays(64));
        trip.setTargetSize(6);
        trip.setCommitDeadline(LocalDate.now().plusDays(30));
        trip.setTripGroup(group);
        tripRepository.save(trip);
    }

    private String tripUrl() {
        return "/g/" + group.getInviteToken() + "/trips/" + trip.getId();
    }

    private Cookie join(String name) throws Exception {
        return mvc.perform(post(tripUrl() + "/join").param("name", name))
                .andReturn().getResponse().getCookie("trip-" + trip.getId());
    }

    private int peopleIn() {
        return participantRepository.findByTrip(trip).size();
    }

    @Test
    void afterJoiningTheNameBoxIsGone() throws Exception {
        makeTrip();
        Cookie me = join("Jake");
        String page = mvc.perform(get(tripUrl()).cookie(me)).andReturn().getResponse().getContentAsString();
        assertTrue(page.contains("You're in as"));
        assertFalse(page.contains("participantName"));
    }

    @Test
    void leavingOnlyRemovesMe() throws Exception {
        makeTrip();
        join("Sam");
        Cookie me = join("Jake");
        mvc.perform(post(tripUrl() + "/leave").cookie(me));
        assertEquals(1, peopleIn());
        assertEquals("Sam", participantRepository.findByTrip(trip).get(0).getName());
    }

    @Test
    void aFakeCookieRemovesNobody() throws Exception {
        makeTrip();
        join("Jake");
        mvc.perform(post(tripUrl() + "/leave").cookie(new Cookie("trip-" + trip.getId(), "made-up")));
        assertEquals(1, peopleIn());
    }

    @Test
    void organizerCanRemoveSomeone() throws Exception {
        makeTrip();
        join("Jake");
        Long id = participantRepository.findByTrip(trip).get(0).getId();
        mvc.perform(post(tripUrl() + "/participants/" + id + "/remove").param("org", group.getOrganizerToken()));
        assertEquals(0, peopleIn());
    }

    @Test
    void aFriendCannotRemoveSomeone() throws Exception {
        makeTrip();
        join("Jake");
        Long id = participantRepository.findByTrip(trip).get(0).getId();
        mvc.perform(post(tripUrl() + "/participants/" + id + "/remove").param("org", "wrong-token"));
        assertEquals(1, peopleIn());
    }
}