package com.thirdcc.webapp.projections.interfaces;

import java.io.Serializable;

/**
 * created by refering to https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#projections.interfaces
 * Spring Data interface-based projections for EventAttendee
 */
public interface EventAttendeeCustomInterface extends Serializable {
    Long getId();
    
    Long getUserId();
    
    Long getEventId();
    
    Boolean getProvideTransport();
    
    String getFirstName();
    
    String getLastName();
    
    default String getUserName(){
        return (null == getLastName()? getFirstName() : getFirstName() + " " + getLastName());
    }
    
    String getContactNumber();
    
    String getYearSession();
}
