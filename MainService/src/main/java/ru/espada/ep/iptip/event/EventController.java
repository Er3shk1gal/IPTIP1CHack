package ru.espada.ep.iptip.event;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.espada.ep.iptip.event.models.requests.CreateEventForStudyGroupsRequest;
import ru.espada.ep.iptip.event.models.requests.CreateEventRequest;
import ru.espada.ep.iptip.event.models.requests.ModifyEventRequest;

import java.security.Principal;

@RestController
@SecurityRequirement(name = "JWT")
@RequestMapping("/event")
public class EventController {

    private EventService eventService;

    @PostMapping("/event")
    public ResponseEntity<?> createEvent(Principal principal, @Valid @RequestBody CreateEventRequest request) {
        EventEntity eventEntity = eventService.createEvent(principal, request);
        return ResponseEntity.ok().body(eventEntity);
    }

    @PostMapping("/eventForStudyGroups")
    public ResponseEntity<?> createEventForStudyGroups(Principal principal, @Valid @RequestBody CreateEventForStudyGroupsRequest request) {
        EventEntity eventEntity = eventService.createEventForStudyGroups(principal, request);
        return ResponseEntity.ok().body(eventEntity);
    }

    @PatchMapping("/event")
    public ResponseEntity<?> modifyEvent(Principal principal, @Valid @RequestBody ModifyEventRequest request) {
        EventEntity eventEntity = eventService.modifyEvent(principal, request);
        return ResponseEntity.ok().body(eventEntity);
    }

    // TODO: getter, setter
    @DeleteMapping("/event")
    public ResponseEntity<?> deleteEvent(Principal principal, Long id) {
        eventService.deleteEvent(principal, id);
        return ResponseEntity.ok().build();
    }

    @Autowired
    public void setEventService(EventService eventService) {
        this.eventService = eventService;
    }
}

