package rps.impl;

import lombok.Data;
import domain.RpsPlayer;
import rps.enums.RpsMove;

import java.util.Map;

@Data
public class RpsGame {

    private final RpsPlayer first;
    private final RpsPlayer second;

    private RpsPlayer winner;
    private int round = 1;

    private static Map<RpsMove, RpsMove> WIN_COMBINATIONS = Map.of(
            RpsMove.ROCK, RpsMove.SCISSORS,
            RpsMove.SCISSORS, RpsMove.PAPER,
            RpsMove.PAPER, RpsMove.ROCK
    );

    public RpsGame(RpsPlayer first, RpsPlayer second) {
        this.first = first;
        this.second = second;
    }

    public void play() {
        if (!first.hasMove() || !second.hasMove()) {
            return;
        }

        if (first.getMove() == second.getMove()) {
            round++;
            sendMessageToAllPlayers(String.format("Draw! \nRound: %s\nyour move -> ", round));
            first.move(null);
            second.move(null);
            return;
        }

        var isWinFirstPlayer = WIN_COMBINATIONS.get(first.getMove()) == second.getMove();
        if (isWinFirstPlayer) {
            first.win();
            second.lose();
        } else {
            first.lose();
            second.win();
        }
    }
    public RpsPlayer getOpponent(RpsPlayer rpsPlayer) {
        return rpsPlayer == first ? second : first;
    }

    private void sendMessageToAllPlayers(String message) {
        first.sendMessage(message);
        second.sendMessage(message);
    }

}