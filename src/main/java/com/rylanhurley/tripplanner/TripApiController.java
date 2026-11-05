package com.rylanhurley.tripplanner;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class TripApiController {

    record TripSummary(Long id, String name, LocalDate startDate, int targetSize, int peopleIn) {}

    private final TripGroupRepository groupRepository;
    private final TripRepository tripRepository;
    private final ParticipantRepository participantRepository;

    public TripApiController(TripGroupRepository groupRepository, TripRepository tripRepository,
                             ParticipantRepository participantRepository) {
        this.groupRepository = groupRepository;
        this.tripRepository = tripRepository;
        this.participantRepository = participantRepository;
    }

    @GetMapping("/api/groups/{token}/trips")
    public ResponseEntity<List<TripSummary>> trips(@PathVariable String token) {
        TripGroup group = groupRepository.findByInviteToken(token);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }
        List<TripSummary> summaries = new ArrayList<>();
        for (Trip trip : tripRepository.findByTripGroup(group)) {
            summaries.add(new TripSummary(trip.getId(),trip.getName(),trip.getStartDate(),trip.getTargetSize(),participantRepository.findByTrip(trip).size()));
        }
        return ResponseEntity.ok(summaries);
    }
}