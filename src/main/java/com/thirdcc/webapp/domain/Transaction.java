package com.thirdcc.webapp.domain;
import com.thirdcc.webapp.domain.enumeration.TransactionStatus;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

import com.thirdcc.webapp.domain.enumeration.TransactionType;

/**
 * A Transaction.
 */
@Entity
@Table(name = "transaction")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Transaction extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "transaction_date")
    private Instant transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "transaction_amount", precision = 21, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "image_link")
    private String imageLink;

    @Column(name = "closed_by")
    private String closedBy;


    @Column(name = "description")
    private String description;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove

    // id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // id

    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", title='" + getTitle() + '\'' +
            ", transactionDate=" + getTransactionDate() +
            ", transactionType=" + getTransactionType() +
            ", transactionStatus=" + getTransactionStatus() +
            ", eventId=" + getEventId() +
            ", transactionAmount=" + getTransactionAmount() +
            ", imageLink='" + getImageLink() + '\'' +
            ", createdBy='" + getCreatedBy() + '\'' +
            ", closedBy=" + getClosedBy() +
            ", description='" + getDescription() + '\'' +
            '}';
    }

    // title
    public String getTitle() {
        return title;
    }

    public Transaction title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    // title

    // transaction_date
    public Instant getTransactionDate() {
        return transactionDate;
    }

    public Transaction transactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
        return this;
    }

    public void setTransactionDate(Instant transactionDate) {
        this.transactionDate = transactionDate;
    }
    // transaction_date

    // transaction_type
    public TransactionType getTransactionType() {
        return transactionType;
    }

    public Transaction transactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
        return this;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }
    // transaction_type

    // transaction_status
    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public Transaction transactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
        return this;
    }

    public void setTransactionStatus(TransactionStatus status) {
        this.transactionStatus = status;
    }
    // transaction_status

    // event_id
    public Long getEventId() {
        return eventId;
    }

    public Transaction eventId(Long eventId) {
        this.eventId = eventId;
        return this;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
    // event_id

    // transaction_amount
    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public Transaction transactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
    // transaction_amount

    // image_link
    public String getImageLink() {
        return imageLink;
    }

    public Transaction imageLink(String imageLink) {
        this.imageLink = imageLink;
        return this;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }
    // image_link

    // closed_by
    public String getClosedBy() {
        return closedBy;
    }

    public Transaction closedBy(String closedBy) {
        this.closedBy = closedBy;
        return this;
    }

    public void setClosedBy(String closedBy) {
        this.closedBy = closedBy;
    }
    // closed_by

    // description
    public String getDescription() {
        return description;
    }

    public Transaction description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    // description

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

}
