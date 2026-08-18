package com.firefox.center.gateway.util;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: ZJL
 * @Description: 数据流工具类
 * @Date: 2020/06/22
 */
public class DataBufferUtilFix {
    public static Mono<DataBufferWrapper> join(Flux<DataBuffer> dataBuffers) {
        return dataBuffers.collectList()
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0).factory().join(list))
                .map(buf -> {
                    InputStream source = buf.asInputStream();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    byte[] buff = new byte[4096];

                    try {
                        int n;
                        while ((n = source.read(buff)) != -1) {
                            stream.write(buff, 0, n);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    DataBufferWrapper wrapper = new DataBufferWrapper(stream.toByteArray(), buf.factory());
                    DataBufferUtils.release(buf);

                    return wrapper;
                }).defaultIfEmpty(new DataBufferWrapper());
    }
}
