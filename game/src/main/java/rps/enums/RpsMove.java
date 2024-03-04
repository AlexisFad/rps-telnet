package rps.enums;

import java.util.Optional;

public enum RpsMove {
    ROCK,
    PAPER,
    SCISSORS;

    public static Optional<RpsMove> move(String move) {
        if (move == null || move.isBlank()) {
            return Optional.empty();
        }
        for (var rpsMove : RpsMove.values()) {
            if (rpsMove.name().equals(move.toUpperCase())) {
                return Optional.of(rpsMove);
            }
        }
        return Optional.empty();
    }
}
