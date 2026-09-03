package com.eventmanagement.service;

import com.eventmanagement.exception.EventNotFoundException;
import com.eventmanagement.exception.UserNotFoundException;

public interface ReportService {

    String generateSystemReport();

    String generateEventReport(int eventId) throws EventNotFoundException, UserNotFoundException;

    String generateUserReport(int userId) throws UserNotFoundException;

    String generateOrganizerReport(int organizerId) throws UserNotFoundException;

    String generateOverallUserReport();

    String generateOverallOrganizerReport();


}
