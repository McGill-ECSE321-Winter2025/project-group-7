package ca.mcgill.ecse321.boardgamesharingsystem.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardgamesharingsystem.dto.RegistrationResponseDto;
import ca.mcgill.ecse321.boardgamesharingsystem.model.Registration;
import ca.mcgill.ecse321.boardgamesharingsystem.service.EventService;

@CrossOrigin(origins = "http://localhost:8090")
@RestController
public class RegistrationController {
    @Autowired
    EventService eventService;

    /**
     * Registers user to event
     * @param eventId the event to register to
     * @param participantId the user to register
     * @return the registration of the user including the response's timestamp
     */
    @PutMapping("/registrations/{eventId}/{participantId}")
    @ResponseStatus(HttpStatus.CREATED)
    public RegistrationResponseDto registerUserToEvent(@PathVariable int eventId, @PathVariable int participantId)
    {
        return new RegistrationResponseDto(eventService.registerUserToEvent(participantId, eventId));
    }

    /**
     * Returns the Registration associated with the specified Event and UserAccount
     * @param eventId the Event associated to the registration
     * @param participantId the UserAccount associated to the registration
     * @return the Registration including the timestamp of the response
     */
    @GetMapping("/registrations/{eventId}/{participantId}")
    public RegistrationResponseDto findRegistrationByEventAndParticipant(@PathVariable("eventId") int eventId, @PathVariable("participantId") int participantId)
    {
        return new RegistrationResponseDto(eventService.findRegistrationByEventAndParticipant(participantId, eventId));
    }

    /**
     * Returns all Registrations associated with the specified Event
     * @param eventId the Event associated to the Registration
     * @return the Registration including the timestamp of the response
     */
    @GetMapping("/registrations/fromEvent")
    public List<RegistrationResponseDto> findRegistrationByEvent(@RequestParam("eventId") int eventId)
    {
        List<Registration> registrationsFound = eventService.findRegistrationsByEvent(eventId);
        List<RegistrationResponseDto> responses = new ArrayList<>();
        registrationsFound.forEach(registration -> responses.add(new RegistrationResponseDto(registration)));
        return responses;
    }

    /**
     * Returns all Registrations associated with the specified UserAccount
     * @param participantId the UserAccount associated to the Registration
     * @return the Registration including the timestamp of the response
     */
    @GetMapping("/registrations/fromParticipant")
    public List<RegistrationResponseDto> findRegistrationByParticipant(@RequestParam("participantId") int participantId)
    {
        List<Registration> registrationsFound = eventService.findRegistrationsByParticipant(participantId);
        List<RegistrationResponseDto> responses = new ArrayList<>();
        registrationsFound.forEach(registration -> responses.add(new RegistrationResponseDto(registration)));
        return responses;
    }

    /**
     * Deregisters the specified UserAccount from the specified Event
     * @param eventId the Event to deregister from
     * @param participantId the UserAccount to deregister
     * @return the Registration deleted including the timestamp of the response
     */
    @DeleteMapping("/registrations/{eventId}/{participantId}")
    public RegistrationResponseDto deregisterParticipantFromEvent(@PathVariable int eventId, @PathVariable int participantId)
    {
        return new RegistrationResponseDto(eventService.deregisterParticipantFromEvent(participantId, eventId));
    }
}
