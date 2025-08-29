package org.example.eventsbooker.services;

import org.example.eventsbooker.entites.Category;
import org.example.eventsbooker.entites.Event;
import org.example.eventsbooker.entites.Tag;
import org.example.eventsbooker.entites.dtos.EventDTO;
import org.example.eventsbooker.entites.dtos.EventDetailsDTO;
import org.example.eventsbooker.entites.dtos.RsvpResponseDTO;
import org.example.eventsbooker.repositories.event.EventRepository;

import javax.inject.Inject;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    @Inject
    EventRepository eventRepository;

    @Inject
    CategoryService categoryService;

    public List<Event> getAllEvents(Integer page, Integer limit){
            return eventRepository.getAllEvents(page, limit);
    }

    public Event addEvent(EventDTO eventDTO) {
        return eventRepository.addEvent(eventDTO);
    }

    public void deleteEvent(Long id){
        eventRepository.deleteEvent(id);
    }

    public List<Event> searchEvents(String query, Integer page, Integer limit) {
        return eventRepository.searchEvents(query, page, limit);
    }


    public Event updateEvent(Long id, EventDTO eventDTO){
        Event existingEvent = findEventById(id);
        if (existingEvent == null) {
            throw new WebApplicationException("Event not found", Response.Status.NOT_FOUND);
        }


        String title = eventDTO.getTitle() != null ? eventDTO.getTitle() : existingEvent.getTitle();
        existingEvent.setTitle(title);

        String description = eventDTO.getDescription() != null ? eventDTO.getDescription() : existingEvent.getDescription();
        existingEvent.setDescription(description);

        LocalDateTime time = eventDTO.getTime() != null ? eventDTO.getTime() : existingEvent.getStartingAt();
        existingEvent.setStartingAt(time);

        String location = eventDTO.getLocation() != null ? eventDTO.getLocation() : existingEvent.getLocation();
        existingEvent.setLocation(location);

        Integer maxCapacity = eventDTO.getMaxCapacity() != null ? eventDTO.getMaxCapacity() : existingEvent.getMaxCapacity();
        existingEvent.setMaxCapacity(maxCapacity);

        if (eventDTO.getCategory() != null) {
            Category category = categoryService.findCategoryByName(eventDTO.getCategory());
            if (category != null) {
                existingEvent.setCategory(category);
            }
        }
        String[] tags = eventDTO.getTags().split(",");
        List<Tag> tagList = new ArrayList<>();


        if(eventDTO.getTags() != null){
            for (String tagName : tags) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    Tag tag = eventRepository.getTag(tagName);
                    if (tag == null) {
                        tag = eventRepository.addTag(tagName);
                    }
                    if (tag != null) {
                        tagList.add(tag);
                    }
                }
            }
            existingEvent.setTags(tagList);
        }

        return eventRepository.updateEvent(existingEvent);
    }

    public Event findEventById(Long id){
        return eventRepository.findEventById(id);
    }

    public List<Event> getEventsByCategory(Long categoryId, Integer page, Integer limit) {
        System.out.println("Category ID: " + categoryId + " Page: " + page + " Limit: " + limit);
        return eventRepository.getEventsByCategory(categoryId, page, limit);
    }

    public EventDetailsDTO getEventDetails(Long eventId, String cookie) {
        return eventRepository.eventDetails(eventId, cookie);
    }

    public List<Event> topEvents(){
        return eventRepository.topEvents();
    }

    public RsvpResponseDTO rsvp(Long eventId, String email) {
        return eventRepository.rsvp(eventId, email);
    }

    public List<Event> allEventsTag(Integer page, Integer limit, String tag){
        return eventRepository.allEventsTag(page, limit, tag);
    }

    public List<Event> relatedEvents(Long eventId, List<String> tags){
        return eventRepository.relatedEvents(eventId, tags);
    }

    public List<Event> top3Events(){
        return eventRepository.top3Events();
    }
}
