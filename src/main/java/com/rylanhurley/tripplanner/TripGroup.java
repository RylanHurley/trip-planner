package com.rylanhurley.tripplanner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.util.UUID;


@Entity
public class TripGroup {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @Column(unique = true)
    private String inviteToken = UUID.randomUUID().toString();
    @Column(unique = true)
    private String organizerToken = UUID.randomUUID().toString();

    public TripGroup() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Long getId() {
        return id;
    }

    public String getInviteToken() {
        return inviteToken;
    }

    public String getOrganizerToken() {
        return organizerToken;
    }

    public boolean isOrganizer(String org) {
        return organizerToken.equals(org);
    }
    public static String makeInviteToken(String name) {
        String slug = name.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (slug.isEmpty()) {
            slug = "trip";
        }
        if (slug.length() > 30) {
            slug = slug.substring(0, 30);
        }
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        return slug+"-"+random;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

}
