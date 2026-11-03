package com.rylanhurley.tripplanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TripGroupTest {

    @Test
    void onlyTheOrganizerTokenMatches() {
        TripGroup group = new TripGroup();

        assertTrue(group.isOrganizer(group.getOrganizerToken()));
        assertFalse(group.isOrganizer(group.getInviteToken()));
        assertFalse(group.isOrganizer(null));
        assertNotEquals(group.getInviteToken(), group.getOrganizerToken());
    }
}
