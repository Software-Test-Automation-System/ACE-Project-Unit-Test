package com.example.GateWay;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

@Configuration
public class GatewayConfig {

    @Bean
    public HttpClient customHttpClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom")
                .maxConnections(500)
                .build();

        return HttpClient.create(connectionProvider)
                .responseTimeout(Duration.ofSeconds(60))
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000)
                .doOnConnected(conn ->
                        conn.addHandlerLast(new io.netty.handler.timeout.ReadTimeoutHandler(60))
                                .addHandlerLast(new io.netty.handler.timeout.WriteTimeoutHandler(60))
                ).httpResponseDecoder(spec -> spec
                        .maxInitialLineLength(16 * 1024) // Allow longer initial HTTP lines (16 KB)
                        .maxHeaderSize(32 * 1024)       // Allow larger headers (32 KB)
                        .maxChunkSize(64 * 1024)); // Allow larger chunks (64 KB)
    }
}

