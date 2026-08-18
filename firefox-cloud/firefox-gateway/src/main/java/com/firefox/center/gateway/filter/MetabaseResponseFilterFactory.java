package com.firefox.center.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @program: java-firefox_center
 * @description: metabse 修改响应数据过滤器
 * @author: yungeng
 * @created: 2021/08/09 11:45
 */
@Slf4j
@Component
public class MetabaseResponseFilterFactory extends ModifyResponseBodyGatewayFilterFactory implements Ordered {
    @Override
    public GatewayFilter apply(Config config) {
        return new ModifyResponseGatewayFilter(this.getConfig());
    }

    private Config getConfig() {
        Config cf = new Config();
        cf.setRewriteFunction(byte[].class, byte[].class, getRewriteFunction());
        return cf;
    }

    @Override
    public int getOrder() {
        return 21;
    }

    /** * 重写 Response 返回体 去除Content-Encoding 解压压缩的body */
    private RewriteFunction<byte[], byte[]> getRewriteFunction() {

        return (exchange, resp) -> {
            ServerHttpResponse response = exchange.getResponse();
            if (response.getStatusCode() == HttpStatus.OK && "gzip".equalsIgnoreCase(response.getHeaders().getFirst("Content-Encoding"))) {
                response.getHeaders().remove("Content-Encoding");

                // 设置 HTTP 状态为 500
                byte[] respData = resp;

                //Content-Encoding: gzip 需要解压缩数据
                try {
                    String data = "{\"code\":" + 200 + ",\"data\": " + uncompress(resp) + "}";
                    //respData = compress(data, "utf-8");
                    respData = data.getBytes("UTF-8");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return Mono.just(respData);

            }



            return Mono.just(resp);
        };

    }

    /**
     * GZIP解压字符串
     * 解决Content-Encoding: gzip 的问题
     * @param src 源字符串
     * @return
     * @throws IOException
     */
    public static String uncompress(byte[] src) throws IOException {
        if (src == null || src.length == 0) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(src);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString();
    }

    /**

     * 将字符串进行gzip压缩，输出压缩后的字节数组

     */

    public static byte[] compress(String str, String encoding) throws IOException {
        if (str == null || str.length() == 0) {
            return null;

        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        GZIPOutputStream gzip;

        gzip = new GZIPOutputStream(out);

        gzip.write(str.getBytes(encoding));

        gzip.close();

        return out.toByteArray();

    }


    @Override
    public String name() {
        return "MetabaseResponseFilter";
    }
}


