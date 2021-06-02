package com.thirdcc.webapp.service.dto;
import java.time.Instant;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Lob;
import com.thirdcc.webapp.domain.enumeration.EventStatus;
import org.springframework.web.multipart.MultipartFile;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.Event} entity.
 */
public class EventDTO implements Serializable {

    private Long id;

    private String name;

    @Lob
    private String description;

    private String remarks;

    private String venue;

    private Instant startDate;

    private Instant endDate;

    private BigDecimal fee;

    private Boolean requiredTransport;

    private EventStatus status;

    private Long imageStorageId;

    private MultipartFile multipartFile;

    private ImageStorageDTO imageStorageDTO;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Boolean isRequiredTransport() {
        return requiredTransport;
    }

    public void setRequiredTransport(Boolean requiredTransport) {
        this.requiredTransport = requiredTransport;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EventDTO eventDTO = (EventDTO) o;
        if (eventDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), eventDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EventDTO{" +
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

    public ImageStorageDTO getImageStorageDTO() {
        return imageStorageDTO;
    }

    public void setImageStorageDTO(ImageStorageDTO imageStorageDTO) {
        this.imageStorageDTO = imageStorageDTO;
    }

    public Long getImageStorageId() {
        return imageStorageId;
    }

    public void setImageStorageId(Long imageStorageId) {
        this.imageStorageId = imageStorageId;
    }

    public MultipartFile getMultipartFile() {
        return multipartFile;
    }

    public void setMultipartFile(MultipartFile multipartFile) {
        this.multipartFile = multipartFile;
    }
}
