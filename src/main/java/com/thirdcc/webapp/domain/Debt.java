package com.thirdcc.webapp.domain;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import com.thirdcc.webapp.domain.enumeration.DebtStatus;

/**
 * A Debt.
 */
@Entity
@Table(name = "debt")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Debt extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "event_attendee_id")
    private Long eventAttendeeId;

    @Column(name = "amount", precision = 21, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DebtStatus status;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReceiptId() {
        return receiptId;
    }

    public Debt receiptId(Long receiptId) {
        this.receiptId = receiptId;
        return this;
    }

    public void setReceiptId(Long receiptId) {
        this.receiptId = receiptId;
    }

    public Long getEventAttendeeId() {
        return eventAttendeeId;
    }

    public Debt eventAttendeeId(Long eventAttendeeId) {
        this.eventAttendeeId = eventAttendeeId;
        return this;
    }

    public void setEventAttendeeId(Long eventAttendeeId) {
        this.eventAttendeeId = eventAttendeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Debt amount(BigDecimal amount) {
        this.amount = amount;
        return this;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public DebtStatus getStatus() {
        return status;
    }

    public Debt status(DebtStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(DebtStatus status) {
        this.status = status;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Debt)) {
            return false;
        }
        return id != null && id.equals(((Debt) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Debt{" +
            "id=" + getId() +
            ", receiptId=" + getReceiptId() +
            ", eventAttendeeId=" + getEventAttendeeId() +
            ", amount=" + getAmount() +
            ", status='" + getStatus() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            "}";
    }
}
