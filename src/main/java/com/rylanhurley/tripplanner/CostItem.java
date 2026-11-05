package com.rylanhurley.tripplanner;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.math.BigDecimal;

@Entity
public class CostItem {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private SplitType splitType;
    @ManyToOne
    private Trip trip;
    private String link;
    public CostItem() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public SplitType getSplitType() {
        return splitType;
    }

    public void setSplitType(SplitType splitType) {
        this.splitType = splitType;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    public String getLink() {return link;}

    public void setLink(String link) {this.link = link;}
}
