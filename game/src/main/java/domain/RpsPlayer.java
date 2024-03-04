package domain;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.*;
import rps.enums.RpsMove;

@Getter
@Setter
@Data
public class RpsPlayer  {

    private final Channel channel;
    private RpsMove move;

    public RpsPlayer(Channel channel) {
        this.channel = channel;
    }

    public void move(RpsMove move) {
        this.move = move;
    }

    public boolean hasMove() {
        return move != null;
    }

    public void win() {
        channel.writeAndFlush("You win!\n").addListener(ChannelFutureListener.CLOSE);
    }

    public void lose() {
        channel.writeAndFlush("You lose!\n").addListener(ChannelFutureListener.CLOSE);
    }

    public void sendMessage(String message) {
        channel.writeAndFlush(message);
    }

}
