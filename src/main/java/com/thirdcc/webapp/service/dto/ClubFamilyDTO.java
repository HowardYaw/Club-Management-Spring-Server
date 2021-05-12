package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Lob;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.ClubFamily} entity.
 */
public class ClubFamilyDTO implements Serializable {

    private Long id;

    private String name;

    @Lob
    private String slogan;

    private String description;


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

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ClubFamilyDTO clubFamilyDTO = (ClubFamilyDTO) o;
        if (clubFamilyDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), clubFamilyDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClubFamilyDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", slogan='" + getSlogan() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
