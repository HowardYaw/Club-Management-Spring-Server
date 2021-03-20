package com.thirdcc.webapp.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountDetailsDTO {

    private Long id;

    private String firstName;

    private String email;

    private String imageUrl;

    private Set<String> authorities;

    private Boolean isCurrentCCHead;

    private Boolean isCurrentAdministrator;

    private Set<Long> eventHeadIds;

    private Set<Long> eventCrewIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    @JsonProperty("isCurrentCCHead")
    public Boolean getCurrentCCHead() {
        return isCurrentCCHead;
    }

    public void setCurrentCCHead(Boolean currentCCHead) {
        isCurrentCCHead = currentCCHead;
    }

    @JsonProperty("isCurrentAdministrator")
    public Boolean getCurrentAdministrator() {
        return isCurrentAdministrator;
    }

    public void setCurrentAdministrator(Boolean currentAdministrator) {
        isCurrentAdministrator = currentAdministrator;
    }

    public Set<Long> getEventHeadIds() {
        return eventHeadIds;
    }

    public void setEventHeadIds(Set<Long> eventHeadIds) {
        this.eventHeadIds = eventHeadIds;
    }

    public Set<Long> getEventCrewIds() {
        return eventCrewIds;
    }

    public void setEventCrewIds(Set<Long> eventCrewIds) {
        this.eventCrewIds = eventCrewIds;
    }
}
