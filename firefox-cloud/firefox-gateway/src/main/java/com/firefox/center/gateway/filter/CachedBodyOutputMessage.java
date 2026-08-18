package com.firefox.center.gateway.filter;

import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @Description
 * @Author 苏杰
 * @CreateTime 2021/9/7 17:45
 */
public class CachedBodyOutputMessage implements ReactiveHttpOutputMessage {
    private final DataBufferFactory bufferFactory;
    private final HttpHeaders httpHeaders;
    private Flux<DataBuffer> body = Flux.error(new IllegalStateException("The body is not set. Did handling complete with success?"));

    CachedBodyOutputMessage(ServerWebExchange exchange, HttpHeaders httpHeaders) {
        this.bufferFactory = exchange.getResponse().bufferFactory();
        this.httpHeaders = httpHeaders;
    }

    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
    }

    public boolean isCommitted() {
        return false;
    }

    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }

    public DataBufferFactory bufferFactory() {
        return this.bufferFactory;
    }

    public Flux<DataBuffer> getBody() {
        return this.body;
    }

    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        this.body = Flux.from(body);
        return Mono.empty();
    }

    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.writeWith(Flux.from(body).flatMap((p) -> {
            return p;
        }));
    }

    public Mono<Void> setComplete() {
        return this.writeWith(Flux.empty());
    }
}
