package com.se191116.studymanagement.service.impl;

import com.se191116.studymanagement.service.UserPresenceService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UserPresenceServiceImpl implements UserPresenceService {

    // Thread-safe map tracking userId -> Instant of last heartbeat/activity
    private final ConcurrentHashMap<Integer, Instant> lastSeenMap = new ConcurrentHashMap<>();

    // User is considered online if heartbeat was within the last 2 minutes
    private static final long ONLINE_TIMEOUT_MINUTES = 2;

    @Override
    public void recordHeartbeat(Integer userId) {
        if (userId != null) {
            lastSeenMap.put(userId, Instant.now());
        }
    }

    @Override
    public boolean isUserOnline(Integer userId) {
        if (userId == null) return false;
        Instant lastSeen = lastSeenMap.get(userId);
        if (lastSeen == null) return false;
        return lastSeen.isAfter(Instant.now().minus(ONLINE_TIMEOUT_MINUTES, ChronoUnit.MINUTES));
    }

    @Override
    public Instant getLastSeenInstant(Integer userId) {
        if (userId == null) return null;
        return lastSeenMap.get(userId);
    }

    @Override
    public LocalDateTime getLastSeen(Integer userId) {
        Instant instant = getLastSeenInstant(userId);
        if (instant == null) return null;
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    @Override
    public List<Integer> getOnlineUserIds(Collection<Integer> userIds) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        Instant threshold = Instant.now().minus(ONLINE_TIMEOUT_MINUTES, ChronoUnit.MINUTES);
        return userIds.stream()
                .filter(id -> id != null && lastSeenMap.containsKey(id) && lastSeenMap.get(id).isAfter(threshold))
                .collect(Collectors.toList());
    }
}
