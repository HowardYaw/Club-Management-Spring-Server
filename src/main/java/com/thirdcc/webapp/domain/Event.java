package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import com.thirdcc.webapp.domain.enumeration.EventStatus;

/**
 * A Event.
 */
@Entity
@Table(name = "event")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "venue")
    private String venue;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "fee", precision = 21, scale = 2)
    private BigDecimal fee;

    @Column(name = "required_transport")
    private Boolean requiredTransport;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EventStatus status;

    @Column(name = "image_storage_id")
    private Long imageStorageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Event name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Event description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public Event remarks(String remarks) {
        this.remarks = remarks;
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVenue() {
        return venue;
    }

    public Event venue(String venue) {
        this.venue = venue;
        return this;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public Event startDate(Instant startDate) {
        this.startDate = startDate;
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public Event endDate(Instant endDate) {
        this.endDate = endDate;
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public Event fee(BigDecimal fee) {
        this.fee = fee;
        return this;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Boolean isRequiredTransport() {
        return requiredTransport;
    }

    public Event requiredTransport(Boolean requiredTransport) {
        this.requiredTransport = requiredTransport;
        return this;
    }

    public void setRequiredTransport(Boolean requiredTransport) {
        this.requiredTransport = requiredTransport;
    }

    public EventStatus getStatus() {
        return status;
    }

    public Event status(EventStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Event)) {
            return false;
        }
        return id != null && id.equals(((Event) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", remarks='" + getRemarks() + "'" +
            ", venue='" + getVenue() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", fee=" + getFee() +
            ", requiredTransport='" + isRequiredTransport() + "'" +
            ", status='" + getStatus() + "'" +
            ", imageStorageId='" + getImageStorageId() + "'" +
            "}";
    }

    public Long getImageStorageId() {
        return imageStorageId;
    }

    public void setImageStorageId(Long imageStorageId) {
        this.imageStorageId = imageStorageId;
    }
}
