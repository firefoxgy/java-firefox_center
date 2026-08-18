package com.firefox.center.gateway.filter;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.fisco.bcos.sdk.abi.FunctionReturnDecoder;
import org.fisco.bcos.sdk.abi.TypeReference;
import org.fisco.bcos.sdk.abi.datatypes.*;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @program: java-firefox_center
 * @description: webase 修改响应数据过滤器
 * @author: yungeng
 * @created: 2021/08/09 11:45
 */
@Slf4j
@Component
public class WebaseMgrResponseFilterFactory extends ModifyResponseBodyGatewayFilterFactory {

    public final static String JSON_OUTPUTS = "outputs";
    public final static String JSON_TRANSACTION = "transaction";
    @Override
    public GatewayFilter apply(Config config) {
        return new ModifyResponseGatewayFilter(this.getConfig());
    }

    private Config getConfig() {
        Config cf = new Config();
        cf.setRewriteFunction(byte[].class, byte[].class, getRewriteFunction());
        return cf;
    }

    /** * 重写 Response 返回体 去除Content-Encoding 解压压缩的body */
    private RewriteFunction<byte[], byte[]> getRewriteFunction() {

        return (exchange, resp) -> {
            ServerHttpResponse response = exchange.getResponse();
            if (response.getStatusCode() == HttpStatus.OK ) {
                byte[] respData = resp;

                try {
                    String respStr = new String(resp, "UTF-8");
                    JSONObject respJson = JSONObject.parseObject(respStr);
                    String code = respJson.getString("code");
                    if(code.equalsIgnoreCase("0")
                        && respJson.get("data") != null
                        && respJson.get("data") instanceof JSONObject
                        && !StringUtils.isEmpty(respJson.getJSONObject("data").getString("status"))) {
                        respJson.put(JSON_TRANSACTION, respJson.get("data"));
                        respJson.remove("data");
                        if (respJson.get(JSON_TRANSACTION) != null && respJson.get(JSON_TRANSACTION) instanceof JSONObject) {
                            String statusStr = respJson.getJSONObject(JSON_TRANSACTION).getString("status");
                            int status = Integer.decode(statusStr);
                            respJson.put("code", status);
                            respJson.put("message", respJson.getJSONObject(JSON_TRANSACTION).getString("message"));
                            String output = respJson.getJSONObject(JSON_TRANSACTION).getString("output");
                            JSONObject func = exchange.getAttribute(WebaseMgrFilterFactory.ATTRIBUTE_FUNC);
                            List outs = new ArrayList<TypeReference<?>>();
                            if (func != null) {
                                JSONArray outputs = func.getJSONArray(JSON_OUTPUTS);
                                if (outputs != null && outputs.size() > 0) {
                                    for (Object item : outputs) {
                                        JSONObject jsonItem = (JSONObject)item;
                                        TypeReference<?> type = null;
                                        if (jsonItem.getString("type").startsWith(Uint.TYPE_NAME))
                                            type = new TypeReference<Uint>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Bytes.TYPE_NAME))
                                            type = new TypeReference<Bytes>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Address.TYPE_NAME))
                                            type = new TypeReference<Address>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Bool.TYPE_NAME))
                                            type = new TypeReference<Bool>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Int.TYPE_NAME))
                                            type = new TypeReference<Int>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Utf8String.TYPE_NAME))
                                            type = new TypeReference<Utf8String>() {
                                            };
                                        else if (jsonItem.getString("type").startsWith(Bool.TYPE_NAME))
                                            type = new TypeReference<Bool>() {
                                            };
                                        outs.add(type);
                                    }
                                }
                            }

                            final Function function =
                                new Function(func.getString(WebaseFrontFilterFactory.JSON_NAME), Arrays.<Type>asList(),
                                    outs);
                            List<Type> results = FunctionReturnDecoder.decode(output, function.getOutputParameters());
                            respJson.put("data", results);
                        } else if (respJson.get(JSON_TRANSACTION) instanceof JSONArray)
                            respJson.put("data", respJson.get(JSON_TRANSACTION));

                    }
                    String data = respJson.toJSONString();
                    respData = data.getBytes("UTF-8");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return Mono.just(respData);

            }



            return Mono.just(resp);
        };

    }

    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
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
        return "WebaseMgrResponseFilter";
    }

}


