package com.sporty.cache;

import java.util.List;

import com.sporty.model.Bet;

public interface BetCache {

    void save(Bet bet);

    List<Bet> getBetsBySessionKey(int sessionKey);

    List<Bet> getBetsByUserId(int userId);
}
