package com.alexis.fad.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

    @NotNull
    @Value(value = "${server.port}")
    private Integer serverPort;
    @Min(0)
    @NotNull
    private Integer bossThreads;
    @NotNull
    @Min(0)
    private Integer workerThreads;
    @NotNull
    private Integer backlog;
    private boolean keepAlive;
    @NotNull
    @Min(1)
    private Integer maxFrameLengthKb;
}
