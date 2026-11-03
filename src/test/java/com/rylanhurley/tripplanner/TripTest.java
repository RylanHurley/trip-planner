package com.rylanhurley.tripplanner;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TripTest {

    @Test
    void commitIsOpenThroughTheDeadlineDay() {
        Trip trip = new Trip();
        trip.setCommitDeadline(LocalDate.of(2026, 12, 1));

        assertTrue(trip.isCommitOpen(LocalDate.of(2026, 11, 30)));
        assertTrue(trip.isCommitOpen(LocalDate.of(2026, 12, 1)));
        assertFalse(trip.isCommitOpen(LocalDate.of(2026, 12, 2)));
    }

    @Test
    void tripIsOnWhenTargetIsReached() {
        Trip trip = new Trip();
        trip.setTargetSize(6);

        assertFalse(trip.isOn(5));
        assertTrue(trip.isOn(6));
        assertTrue(trip.isOn(7));
    }
    @Test
    void spotsLeftNeverGoesNegative(){
        Trip trip = new Trip();
        trip.setTargetSize(6);
        assertEquals(2,trip.spotsLeft(4));
        assertEquals(0,trip.spotsLeft(8));
    }
}
