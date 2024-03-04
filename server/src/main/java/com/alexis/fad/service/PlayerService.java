package com.alexis.fad.service;

import domain.RpsPlayer;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class PlayerService {

    private final Queue<RpsPlayer> waiters = new ConcurrentLinkedQueue<>();

    public boolean addNewWaiter(RpsPlayer channel) {
        return waiters.add(channel);
    }

    public Optional<RpsPlayer> findFirstActiveChannel() {
        while (!waiters.isEmpty()) {
            var waiter = waiters.poll();
            if (waiter.getChannel().isActive()) {
                return Optional.of(waiter);
            }
        }
        return Optional.empty();
    }

    public boolean removeWaiter(RpsPlayer rpsPlayer) {
        return waiters.remove(rpsPlayer);
    }

}
