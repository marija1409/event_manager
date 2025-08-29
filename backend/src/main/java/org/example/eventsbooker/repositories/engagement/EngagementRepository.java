package org.example.eventsbooker.repositories.engagement;

import org.example.eventsbooker.entites.enums.Engagement;

import java.util.Map;

public interface EngagementRepository {
    public Engagement engage(Long id, String type, Boolean like, String cookie);
    public Integer countEngagements(Long id, String type);
    public Map<Engagement, Integer> countEngagementSeparate(Long id, String type);
}
