package com.rylanhurley.tripplanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ParticipantRepositoryTest {
    @Autowired TripGroupRepository groupRepository;
    @Autowired TripRepository tripRepository;
    @Autowired ParticipantRepository participantRepository;

    @Test
    void findsParticipantsAndChecksNamesIgnoringCase() {
        TripGroup group = new TripGroup();
        group.setName("Ski crew");
        groupRepository.save(group);

        Trip trip = new Trip();
        trip.setName("Breck");
        trip.setTripGroup(group);
        tripRepository.save(trip);

        Participant rylan = new Participant();
        rylan.setName("Rylan");
        rylan.setTrip(trip);
        participantRepository.save(rylan);

        assertEquals(1, participantRepository.findByTrip(trip).size());
        assertTrue(participantRepository.existsByTripAndNameIgnoreCase(trip, "rylan"));
        assertFalse(participantRepository.existsByTripAndNameIgnoreCase(trip, "Sam"));
    }
}
