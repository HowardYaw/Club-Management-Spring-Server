package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Lob;
import com.thirdcc.webapp.domain.enumeration.TransactionType;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.Budget} entity.
 */
public class BudgetDTO implements Serializable {

    private Long id;

    private Long eventId;

    private BigDecimal amount;

    private TransactionType type;

    private String name;

    @Lob
    private String details;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BudgetDTO budgetDTO = (BudgetDTO) o;
        if (budgetDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), budgetDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BudgetDTO{" +
            "id=" + getId() +
            ", eventId=" + getEventId() +
            ", amount=" + getAmount() +
            ", type='" + getType() + "'" +
            ", name='" + getName() + "'" +
            ", details='" + getDetails() + "'" +
            "}";
    }
}
