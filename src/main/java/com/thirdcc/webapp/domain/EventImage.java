package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;

/**
 * A EventImage.
 */
@Entity
@Table(name = "event_image")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class EventImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "image_storage_id")
    private Long imageStorageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public EventImage eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getImageStorageId() {
        return imageStorageId;
    }

    public EventImage imageStorageId(Long imageStorageId) {
        this.imageStorageId = imageStorageId;
        return this;
    }

    public void setImageStorageId(Long imageStorageId) {
        this.imageStorageId = imageStorageId;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventImage)) {
            return false;
        }
        return id != null && id.equals(((EventImage) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "EventImage{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", imageStorageId=" + getImageStorageId() +
            "}";
    }
}
