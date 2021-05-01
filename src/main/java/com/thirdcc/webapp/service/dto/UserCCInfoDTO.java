package com.thirdcc.webapp.service.dto;
import java.io.Serializable;
import java.util.Objects;
import com.thirdcc.webapp.domain.enumeration.ClubFamilyRole;
import com.thirdcc.webapp.domain.enumeration.FishLevel;

/**
 * A DTO for the {@link com.thirdcc.webapp.domain.UserCCInfo} entity.
 */
public class UserCCInfoDTO implements Serializable {

    private Long id;

    private Long userId;

    private Long clubFamilyId;

    private ClubFamilyRole familyRole;

    private String yearSession;

    private FishLevel fishLevel;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getClubFamilyId() {
        return clubFamilyId;
    }

    public void setClubFamilyId(Long clubFamilyId) {
        this.clubFamilyId = clubFamilyId;
    }

    public ClubFamilyRole getFamilyRole() {
        return familyRole;
    }

    public void setFamilyRole(ClubFamilyRole familyRole) {
        this.familyRole = familyRole;
    }

    public String getYearSession() {
        return yearSession;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserCCInfoDTO userCCInfoDTO = (UserCCInfoDTO) o;
        if (userCCInfoDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), userCCInfoDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserCCInfoDTO{" +
            "id=" + getId() +
            ", userId=" + getUserId() +
            ", clubFamilyId=" + getClubFamilyId() +
            ", familyRole='" + getFamilyRole() + "'" +
            ", yearSession='" + getYearSession() + "'" +
            "}";
    }

    public FishLevel getFishLevel() {
        return fishLevel;
    }

    public void setFishLevel(FishLevel fishLevel) {
        this.fishLevel = fishLevel;
    }
}
