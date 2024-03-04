package com.alexis.fad.server.handler;

import com.alexis.fad.service.PlayerService;
import domain.RpsPlayer;
import io.netty.channel.*;
import io.netty.util.AttributeKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import rps.enums.RpsMove;
import rps.impl.RpsGame;

import java.util.Arrays;


@Component
@Slf4j
@RequiredArgsConstructor
@ChannelHandler.Sharable
public class RpsGameServerHandler extends ChannelInboundHandlerAdapter {

    private final PlayerService waiterService;

    public static final AttributeKey<RpsGame> GAME_ATTRIBUTE_KEY = AttributeKey.newInstance("GAME");
    public static final AttributeKey<RpsPlayer> PLAYER_ATTRIBUTE_KEY = AttributeKey.newInstance("PLAYER");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        var player = new RpsPlayer(ctx.channel());
        var channel = ctx.channel();
        channel.attr(PLAYER_ATTRIBUTE_KEY).set(player);
        player.sendMessage("Welcome to the game \"rock, paper, scissors\"\n");
        var optionalOpponent = waiterService.findFirstActiveChannel();
        log.info("Opponent {}", optionalOpponent);

        if (optionalOpponent.isPresent()) {
            var opponent = optionalOpponent.get();
            var game = new RpsGame(player, opponent);
            channel.attr(GAME_ATTRIBUTE_KEY).set(game);
            opponent.getChannel().attr(GAME_ATTRIBUTE_KEY).set(game);
            sendMessageToPlayers("Opponent was find, start game\n", player, opponent);
            sendMessageToPlayers("Available commands: rock, paper, scissors\n", player, opponent);
            sendMessageToPlayers("Your move -> ", player, opponent);
            return;
        }
        log.info("can't find opponent, add new waiter");
        player.sendMessage("Trying find opponent, please wait\n");
        waiterService.addNewWaiter(player);
        super.channelActive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        var channel = ctx.channel();
        RpsGame game = null;
        if (channel.hasAttr(GAME_ATTRIBUTE_KEY)) {
            game = channel.attr(GAME_ATTRIBUTE_KEY).get();
        }
        if (game == null) {
            return;
        }
        var player = channel.attr(PLAYER_ATTRIBUTE_KEY).get();
        if (player.hasMove()) {
            player.sendMessage("You have already made your move, please wait \n");
            return;
        }
        var move = RpsMove.move((String) msg);
        if (move.isPresent()) {
            player.move(move.get());
        } else {
            player.sendMessage("Incorrect command \n");
            player.sendMessage("Your move -> ");
            return;
        }
        game.play();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.writeAndFlush("Something goes wrong");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        var channel = ctx.channel();
        var player = channel.attr(PLAYER_ATTRIBUTE_KEY).get();
        if(channel.hasAttr(GAME_ATTRIBUTE_KEY)) {
            var game = channel.attr(GAME_ATTRIBUTE_KEY).get();
            if (game != null) {
                var opponent = game.getOpponent(player);
                opponent.getChannel().attr(GAME_ATTRIBUTE_KEY).set(null);
                waiterService.addNewWaiter(opponent);
                opponent.move(null);
                opponent.sendMessage("\nOpponent disconnected, will be find new opponent \n");
            }
        }
        waiterService.removeWaiter(player);
    }

    private void sendMessageToPlayers(String message, RpsPlayer... player) {
        Arrays.stream(player).forEach(p -> p.sendMessage(message));
    }
}
