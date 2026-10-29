package com.rylanhurley.tripplanner;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Trip {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private int targetSize;
    private LocalDate commitDeadline;

    public Trip(){}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public int getTargetSize() {
        return targetSize;
    }

    public LocalDate getCommitDeadline() {
        return commitDeadline;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setTargetSize(int targetSize) {
        this.targetSize = targetSize;
    }

    public void setCommitDeadline(LocalDate commitDeadline) {
        this.commitDeadline = commitDeadline;
    }
}
