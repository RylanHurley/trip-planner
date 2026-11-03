package com.rylanhurley.tripplanner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    List<Participant> findByTrip(Trip trip);
    boolean existsByTripAndNameIgnoreCase(Trip trip, String name);
}
