package com.firefox.center.gateway.decorator;

import com.firefox.center.gateway.util.DataBufferUtilFix;
import com.firefox.center.gateway.util.DataBufferWrapper;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author: ZJL
 * @Description: 读取response
 * @Date: 2020/06/22
 */
public class RecorderServerHttpResponseDecorator extends ServerHttpResponseDecorator {
    private DataBufferWrapper data = new DataBufferWrapper();

    public RecorderServerHttpResponseDecorator(ServerHttpResponse delegate) {
        super(delegate);
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return DataBufferUtilFix.join(Flux.from(body))
                .doOnNext(d -> this.data = d)
                .flatMap(d -> super.writeWith(copy()));
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return writeWith(Flux.from(body)
                .flatMapSequential(p -> p));
    }

    public Flux<DataBuffer> copy() {
        DataBuffer buffer = this.data.newDataBuffer();
        if (buffer == null){
            return Flux.empty();
        }else{
            return Flux.just(buffer);
        }
    }
}
