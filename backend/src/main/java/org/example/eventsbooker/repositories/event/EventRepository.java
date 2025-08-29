package org.example.eventsbooker.repositories.event;

import org.example.eventsbooker.entites.Event;
import org.example.eventsbooker.entites.Tag;
import org.example.eventsbooker.entites.dtos.EventDTO;
import org.example.eventsbooker.entites.dtos.EventDetailsDTO;
import org.example.eventsbooker.entites.dtos.RsvpResponseDTO;

import java.util.List;

public interface EventRepository {

    public Event addEvent(EventDTO eventDTO);
    public List<Event> getAllEvents(Integer page, Integer limit);
    public Event updateEvent(Event event);
    public Event findEventById(Long id);
    public void deleteEvent(Long id);
    public List<Event> searchEvents(String query, Integer page, Integer limit);
    public Tag getTag(String name);
    public Tag addTag(String name);
    public List<Event> getEventsByCategory(Long categoryId,  Integer page, Integer limit);
    public EventDetailsDTO eventDetails(Long id, String cookie);
    public List<Event> topEvents();
    public RsvpResponseDTO rsvp(Long eventId, String email);
    public List<Event> allEventsTag(Integer page, Integer limit, String tag);
    public List<Event> relatedEvents(Long eventId, List<String> tags);
    public List<Event> top3Events();

}
