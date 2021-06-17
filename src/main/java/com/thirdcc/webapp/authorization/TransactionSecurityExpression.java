package com.thirdcc.webapp.authorization;

import org.springframework.stereotype.Service;

@Service
public class TransactionSecurityExpression {

    private final ManagementTeamSecurityExpression managementTeamSecurityExpression;

    public TransactionSecurityExpression(ManagementTeamSecurityExpression managementTeamSecurityExpression){
        this.managementTeamSecurityExpression = managementTeamSecurityExpression;
    }

    /**
     * Security expression to check on Transaction related endpoint:
     *  <ul>
     *      <li>This is created as a separate service to reduce marshalling event based preauthorise logic with management team preauthorise logic.</li>
     *      <li>This is because ManagementTeamSecurityExpression does not concern about the eventId null or not, whereby Transaction related endpoint accepts null eventId under certain circumstances.</li>
     *  </ul>
     *
     * @param eventId eventId of current transaction
     * @return allow or disallow transaction related request
     */
    public boolean transactionAccess(Long eventId){
        if (eventId == null){
            return managementTeamSecurityExpression.isCurrentAdministrator();
        }else{
            return managementTeamSecurityExpression.isEventCrew(eventId) || managementTeamSecurityExpression.isCurrentAdministrator();
        }
    }
}
