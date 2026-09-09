package com.se191116.studymanagement.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface UserPresenceService {
    void recordHeartbeat(Integer userId);
    boolean isUserOnline(Integer userId);
    Instant getLastSeenInstant(Integer userId);
    LocalDateTime getLastSeen(Integer userId);
    List<Integer> getOnlineUserIds(Collection<Integer> userIds);
}
