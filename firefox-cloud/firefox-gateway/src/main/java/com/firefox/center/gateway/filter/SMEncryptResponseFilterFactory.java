package com.firefox.center.gateway.filter;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.firefox.center.common.enums.CodeEnum;
import com.firefox.center.common.utils.sm4.SM4Util;
import com.firefox.center.gateway.properties.MyGatewayProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @program: java-firefox_center
 * @description: metabse 修改响应数据过滤器
 * @author: yungeng
 * @created: 2021/08/09 11:45
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SMEncryptResponseFilterFactory extends ModifyResponseBodyGatewayFilterFactory implements Ordered {

    private final MyGatewayProperties myGatewayProperties;

    @Override
    public String name() {
        return "SMEncryptResponseFilter";
    }

    @Override
    public int getOrder() {
        return 20;
    }

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
            if (response.getStatusCode() == HttpStatus.OK) {
                byte[] respData = resp;
                int code = -1;
                JSONObject jsonBody=null;
                try {
                    String data = new String(respData, "UTF-8");
                    try {
                        jsonBody = JSONObject.parseObject(data);
                        code = jsonBody.getInteger("code");
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    if(jsonBody!=null && (CodeEnum.OK.getCode() == code || CodeEnum.SUCCESS.getCode() ==code)) {
                        String encryptData="";
                        if(jsonBody.getString("data").startsWith("{")){
                            JSONObject jsonData=jsonBody.getJSONObject("data");
                            String encryptStr=jsonData.toJSONString();
                            encryptStr=encryptStr.substring(0, encryptStr.lastIndexOf("}")+1);
                            encryptData=SM4Util.encrypt(encryptStr, myGatewayProperties.getSm4().getKey());
                            jsonBody.put("data", encryptData);
                        }else if(jsonBody.getString("data").startsWith("[")){
                            JSONArray jsonDataArr=jsonBody.getJSONArray("data");
                            String encryptStr=jsonDataArr.toJSONString();
                            encryptStr=encryptStr.substring(0, encryptStr.lastIndexOf("]")+1);
                            encryptData=SM4Util.encrypt(encryptStr, myGatewayProperties.getSm4().getKey());
                            jsonBody.put("data", encryptData);
                        }
                        respData=jsonBody.toJSONString().getBytes("UTF-8");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Mono.just(respData);
            }
            return Mono.just(resp);
        };

    }

    public static void main(String[] args) {

        /*
        String encryptStr = "{\n"
            + "\t\"client_secret\": \"4a3bb1de48cf4d0cb4e6a96db8d55e26\",\n"
            + "\t\"username\": \"admin\",\n" + "    \"password\":\"8hvHtQ6ta2aTGIzU\",\n" + "    \"key\":\"1234\",\n"
            + "    \"code\":\"9\",\n" + "    \"center_id\":\"8\"\n" + "}";

         */
        //zx@SDasdf123

        BCryptPasswordEncoder encode = new BCryptPasswordEncoder();
        String password=encode.encode("kp123123");

        System.out.println(password);

        String encryptStr = "{\n"
            + "\t\"detail_id\": \"2006\"}";


        encryptStr = "{center_id: \"18\"" + "client_secret: \"4a3bb1de48cf4d0cb4e6a96db8d55e26\"\n"
            + "code: \"10\"\n" + "key: 1234\n" + "password: \"zx@SDasdf123\"\n" + "username: \"adminzx\"}";


        String key = "313a779e9968d4dd0e95bfb992a3af5a";
        String cipherTxt=SM4Util.encrypt(encryptStr, key);
        System.out.println(cipherTxt);
        cipherTxt="3b4b0d1b71b24e104b2e8418d4f109b5203d20236bc28bbce732466f8f9f310dbf912752e05f92784e57fba106bbe3f20ba191d92babbb60a6659d529146a4bf5a5c3397f84601398eeec0a57d093a57a318667b198ab3eda843b6878def85fa463ba7e2dc0d65b39ba20ffef75075ee60968e22dcdf082af938dbfd6fe32fcbf7ceccd72b893e58817ef5162e0efc2185ca5a35b5486c782e0a93ff2cdde8719c6cc0c2acf9f30dca2adbe4f4722685";

        //cipherTxt="35f171e6496c296d6519d1de382c4f0d901ef3c76b133f112cf4f6e2e938532c049f45959020dc8c60598926bd7922fd37cfc9dcb882ff851cbad100004c01ca1eb587821b17d23222729bcbc2ebac52a8341672b112bd72db50a4d3f99defa7ceedb8168d1a5bf94f4d1a76e40ae08cdaa8e9b2f7aef69d1cdd6d1972e495d02a379e6894f0ea7465c1c1aeb86d05a57f60e2fd5f8af7081e481703ba9b3ecb727422e03a43a1c2b7069aca90a50ad51d131b6066c5467af675a07fd6da4effc270c3c3081fcbfc3183636438845c685d2b50696c8be8b791c1469ae05885c4253d6f790b4f51c48b90ac0145756cf9ff03e78c0b809e4a450624e215e2559ebd284ac4bbaf18fc01216a96923bccfa7e1022405d3eda9b4edb52dc23fecb8a6424e5c82135ca33ca1852965267a54d5c467063432fc0ebbdf71ba852285e415461870a3e8e81d8679d61d6892968a9f8d557340a5728e25721a09a576606af49d5eeb2af0359694cc6bf7e28ffc9d8da13b3c6efd1684a7cbe4f1a19a7981bc38cb5b185d2fcef3bc3277b72fa9b658e38731a08714834e65d05b5c0d2fd691ce84ed7e4b9f91ae812df1f5edea1e04a98daf4ad455a9ec7c27df07e86536e774e5c48db7e6c69c7ffd4c38e7f0f28ec4c6b8723940f5647cf3ee7772162a2db874026862f2031897d689609cabf05e079ebc87d130bd21e3ec45a920522d39a93100999bd9a1d6f926b4946019d5b4e7002bef9d6345cc010ee8d6054d4ad78c19140d2c86bbe2c98c8d88054950dcf0ee908d1f2de4d784fa31b46dbfe6c474dcdd1b1406eb8fd94489958b87ac6606802ad26fd009e4e539334a5b39b559ff84434b6018e908c2eeec1b44b3f621281a89fc859125eaf0bfc9d84cfbeb1610b51877910bebbe1a93c2b6ac5035a2ab66a5c48c3d1b96b3ea27d7892c7f68cf804733d5c0f72c075958a690446e83a44a88c6361b1c0b737cfcc65a0d699c17575c2ea25f29c31c9b23bc1461c13c5d95f241a4ecffd83ac22e10913aad0b6343e2f7eaea1d2b879e21736f88f410030b53b323fdd0ec326d24b987f3e71d05fddb53de081825bd281e776e61f32cf4664143180f1a358628c9697f6b4a4f81ad25d140be1ca37fcebc7dce65facd2749d8758f3cd82723432037cc42557ff9b19bed981e03ff7ca3f08a4ba073f5efb4552a876e8a06689dfd2d56256b74896cefa3d732969e9176c84bd37ffa35d9d0845a63f5d23b01098d7adb1e1ba12eaeffe94c2c06fdbd462452267445167e2e84d78d6390bd08a4a09496fe88a4b0a2f60fa5b377326fee189b1ff2b1a5115abc9afffb399f2effc35447cfd691ac5a0ea8aa686508ce8888cac0df7c97562c8088713859c367bcb4b46270cceabaf43be52a00efef2b9bd759186acd9b5717c72c3a885d04c7454d5464594b588346a14d2b9bad005d8675c19d9d2ef48db950b26fcb6b3e95aef417fa20b8dc4e58b8f25a6c9d9f1007bbe01eae86372e4cfa1098932ae38d83f2014dcc0a07de358a4ed22106a06b727027fb617a229122a9fe5f412be948134587119845bce72ed6ba792ca4169050f26c55ba423ccc5da9363f1f83c39de8e334b55c95ab91e6df147644ef340c717b323f4f160c50dfed91973e547b616c66a2fdf75d6ca87cb88838fcad2c5fd8face6ed12736e4777bc165a42062521d6563a6a6c7a17f22883e2357856801d65f5dc81c283807dd804026e8bdc84df39fc983f3d0fc722d02f827c63d3eae81fc420ee0ab00de821051213b3e10db4b5921d50f17c02ba21be2dbd9dea33a4dbf72606d57c67dd65a92ab6bfaa927132f1802f52db406392aa0c1ac9dd5e66aeba045332f73fff53fcab71dadf17e084f2d866ad4bbe91ffe83fbfaa2985798b4bf35eb37820ac75177e756d252c8c6e36dab6ef895893523b125b905d5a18990101b11a583d263fb9cfebe6c24839e0b1de31e010c0eda4b80f7f8b2a9fe1fada06061209b297b93d6a22dbe61aa03f10019db63341819eba1745f1a97270eb9d67aca0d19777fb011231a56afa97c1e6c6daa3d7eb1fa073b08b6537a82acff9dff770c6eaaf686aa63f3d385131364aab685803def6467a0a2741051702c2f839e9e3e8c544447d13e2acd0e53b28121b71ec33a171863043851b65eb2d88a34da02bb61f6694cc7bf7fb9944a4d06dd56bae16726c11b141de5d3ef9c9f6e79d4d20f75e7ddd980efb7a270e7441502507432b47cebe7cfe43269717596ee8dcae4b83a008ec1e33072b57cae260f1d88c65fed529800e6e9ac5222ee04539d5b1e7c66c33bf3e2968aef0a5d9a6b26974635af1eb779263c73b89a72bfbbf61f506683b9c8736e3bcc43abd603cb24875e6d93048bcd6aa7c66e9f2151af7bd33b4a639351c40e19f3e133d92cf2b8e69383ef5cdbe5478db101874838c16f1b7a3d9a039cf7b0c41cbf36b023d26262e947a7c5cd08a2a64fe27c3155fb29d2769910a05b075d7ab339ad79fc6527c08bdc8b0ccca190ba9c98bcdccbe6b7ca562d6e2d47c9e27a3759ae81a3c1f83b36c728f736a4f9df3cee6481b2f972245bcda40d5a38bc01693ea0ca3b834123ec5164f363d8638571737f690c6824b32bd43c8efd17dd527948bcb7b4dbcafcc323af2090194d15f6e933def5e65256f76fcf624ebb48bcce0142b6b9d0b489bf8ed3bc91657c459e522777abbcc207d922813389dbba10fd3ba14203f27cae70daf498b6ae8802f11aeff9906086ee479805e2b03e4d183f15c08cb3312ed851358725bc743bb534ba6b4750e3448bb1ce659d05445b102a3ded05595fec953b64c84413aa205ec04f71b95780964f38b3412b9eeb53b708f4d21f92b3bc46ef339";
        System.out.println(SM4Util.decrypt(cipherTxt, key));
    }

}


