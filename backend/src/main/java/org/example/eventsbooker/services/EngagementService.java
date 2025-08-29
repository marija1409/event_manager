package org.example.eventsbooker.services;

import org.example.eventsbooker.entites.enums.Engagement;
import org.example.eventsbooker.repositories.engagement.EngagementRepository;

import javax.inject.Inject;
import java.util.Map;

public class EngagementService {

    @Inject
    private EngagementRepository engagementRepository;

    public Engagement engage(Long id, String type, Boolean like, String cookie) {
        return this.engagementRepository.engage(id, type, like, cookie);
    }

    public Integer countEngagements(Long id, String type) {
        return this.engagementRepository.countEngagements(id, type);
    }

    public Map<Engagement, Integer> countEngagementsSeparate(Long id, String type) {
        return this.engagementRepository.countEngagementSeparate(id, type);
    }

}
