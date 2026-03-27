package com.sporty.cache;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;

import com.sporty.model.Bet;

@Service
public class BetCacheInMemory implements BetCache {

    private final CopyOnWriteArrayList<Bet> bets = new CopyOnWriteArrayList<>();

    @Override
    public void save(Bet bet) {
        bets.add(bet);
    }

    @Override
    public List<Bet> getBetsBySessionKey(int sessionKey) {
        return bets.stream()
                .filter(b -> b.sessionKey() == sessionKey)
                .toList();
    }

    @Override
    public List<Bet> getBetsByUserId(int userId) {
        return bets.stream()
                .filter(b -> b.userId() == userId)
                .toList();
    }

    @Override
    public void clearBetsBySessionKey(int sessionKey) {
        bets.removeIf(b -> b.sessionKey() == sessionKey);
    }
}
