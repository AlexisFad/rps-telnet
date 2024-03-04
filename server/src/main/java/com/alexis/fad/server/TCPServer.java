package com.alexis.fad.server;

import com.alexis.fad.config.properties.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableConfigurationProperties(NettyProperties.class)
public class TCPServer implements CommandLineRunner {

    private final SimpleGameChannelInitializer simpleGameChannelInitializer;
    private final NettyProperties nettyProperties;
    private final MultithreadEventLoopGroup bossGroup;
    private final MultithreadEventLoopGroup workerGroup;

    public TCPServer(SimpleGameChannelInitializer simpleGameChannelInitializer, NettyProperties nettyProperties) {
        this.simpleGameChannelInitializer = simpleGameChannelInitializer;
        this.nettyProperties = nettyProperties;
        this.bossGroup = new NioEventLoopGroup(nettyProperties.getBossThreads());
        this.workerGroup = new NioEventLoopGroup(nettyProperties.getWorkerThreads());
    }

    @Override
    public void run(String... args) {
        try {
            var server = createServer();
            server.bind(nettyProperties.getServerPort()).sync();
            log.info("Server is started at port: {}", nettyProperties.getServerPort());
        } catch (InterruptedException e) {
            log.warn("InterruptedException occurred {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    private ServerBootstrap createServer() {
        ServerBootstrap server = new ServerBootstrap();
        server.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(simpleGameChannelInitializer);
        server.option(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog());
        return server;
    }

    @PreDestroy
    public void shutdown() {
        log.info("Server is shutting down");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
