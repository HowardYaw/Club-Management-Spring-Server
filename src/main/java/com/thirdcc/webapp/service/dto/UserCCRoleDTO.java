package com.thirdcc.webapp.service.dto;

import com.thirdcc.webapp.domain.enumeration.CCRoleType;

import java.io.Serializable;

/**
 * A general DTO for user CC Roles. Generalize from
 * {@link com.thirdcc.webapp.domain.Administrator}
 * {@link com.thirdcc.webapp.domain.EventCrew}
 * {@link com.thirdcc.webapp.domain.UserCCInfo}
 */
public class UserCCRoleDTO implements Serializable {
    private Long userId;

    private CCRoleType type;

    private String role;

    private String yearSession;

    private Long eventId;

    private String eventName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public CCRoleType getType() {
        return type;
    }

    public void setType(CCRoleType type) {
        this.type = type;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getYearSession() {
        return yearSession;
    }

    public void setYearSession(String yearSession) {
        this.yearSession = yearSession;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public String toString() {
        return "UserCCRoleDTO{" +
            ", userId=" + getUserId() +
            ", type='" + getType() + "'" +
            ", role='" + getRole() + "'" +
            ", yearSession='" + getYearSession() + "'" +
            ", eventId='" + getEventId() + "'" +
            ", eventName='" + getEventName() + "'" +
            "}";
    }
}
