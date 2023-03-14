package com.yjq.programmer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author 杨杨吖
 * @QQ 823208782
 * @WX yjqi12345678
 * @create 2021-11-07 10:25
 */
@Configuration
@Component
public class AliPayConfig {
    /**
     * 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
     */
    private String appId = "2016110300790563";

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private String privateKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCCsWvZktzLxZhB8yFUp0f98s3uPegxu0p95sYwwSurm9FrCqn8jEJaR92git/i19QsODjE4KFY85O+AVrcZEDJoqubf8Pc5tCGcaCI1Ho+OD2LHW31cmDj29tlLHi2SlsL9EynSD/LL2DsG1Ufj12/TkuWPVAzjwsf/NJwiKV9mTIX/I88RvCJV2tqX8vT8V5LuHvI27vmGeKTgioExymrv6GQlhM1aHv3w2GMShp/n5wY1S0bhgMLL5zzO3FJSOKrOBPTYh0sCLAhvcD7vDXDH3ruBVq+jbtqXkQMdHww1nmaTz/t06uqNEoofGYCKfZWSQgXnPwEQBpqdSEs8Nl/AgMBAAECggEAbYbY9pbwuUkYR7TFKxeh6r3wPQfersbiL88WK0fIHGJSR93BBahZ9yUhJGkeJhJwerqHQN9r+IrxNxwVlHvkG9da789MSrWY7gMN4X4IEFq7oPmB7F/1uR/Ipup1I3f/A26ySsLuy44iMNzbIr7YdvnbuMYbj4oCbcVIawzpe+6ThkSERg+kaqZbg9DsAjLEkUFLRLE37R2F5BgdM4Gc5Dsw6e7ZuRc1fyBwI4bbdC11oxpLTOkFQFL9aAR1iyKvaPhbV7fswYbMBmJXDTplfDLXufcZPbm7MgfbV1sIhipjf6HpNOB3Qc0WI3iWwBNrb5EGD6PbqAtZyLDBPOVz+QKBgQC4s3Xnt18XoQXqHU5cgg+XCZSkP6z5z/NB3rmvCfThFz1mMEhscw3YruvL2ohXxB0cqoI0eiH2LFMG2tw3g4aDavOLx4H2o+VjcT3rFHoePmTTXv/H+R8roE0IMOKpVQ8Mc65EEBHaZtbLAD2vEZpdv8CjBSx9hCVVC3ISF25otQKBgQC1JMfvT5ofKGUqKoqgz3+0sqd8nfkx5Q3BmeS02nhEXgLW65KThfJnez9MB+0V3f4lxJnRTwYGwh7RFL3bViAkFcO1ETwM9rW73n9tkmuAjY0OPQGiPfxnMUgmGUyR7cov8j7t/cCRUREx/GUXxAOngheDxXsZ8pSX8bPhs2ed4wKBgBwAmU+YLYKXlx33c3ny/goYGcENJlx5epfx/JTdHZNCwrxLda97gg3n9zfg2er0gLVn5HWwTpXUAoJ3jAuelTY3cNUwWjPahVyePpT1dc5AB2lEbeMTkfY7R3KGdtbVXoK0xybqiMpj7Qy57KAuOwkbmUkd77DX6BEMuy6i/spBAoGBAKAI+sqmnRdh7N6v7wHb0IuoOuiR+JLoNpsQh0pwqUhddkzCmyDSejKKZIWbiw6CrgJYCz0sf7qAzhESLhoVoRfAUsMpktAxNqXgENyRO4C7jh9CLqtT8CaLF57xQwC6AKMTdh10ziiUYoiy3+17M29OM9ArLHLkKfuDgtSoV6bZAoGATIOCxQrqrZFVe4zLCnzcAFHFQvqxeNKdeLo7fMfQhU5MnDn9TVmzHhVtNg0SfdNA4WwbVX2Per019M/UcKSjJc5uYJ8G5BZaBZ7ZNJ1JAG1TtOi0m4zAFJJG3SznJVgKOFCDeMc7shMpE2FnTXBJx39LCweTnz8PsF4T4A2/GLY=";

    /**
     * 支付宝公钥,
     */
    private String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhdqk8i1YrbG/6bxhrSRI7IcoezOAb+fMzc4XypuM3mFV8VWs4VgurDj2latDcmcWs2vvs3abgfLrWKjiHJPnnLZ6+/gzKkcJnQqikWRUQIQH/miAPoroDE4G31U3SGyfG53c+84b3H9zCzvGDbHBOda4dxjHUqi0oOGrTA8ETtJGfJ0j73cBhlmCjJLjzMZLV25pXUTI6drGuPls6I/FnlFhnPNOP8qav9S4syadYtYpJe2SzEr6eRla0Gfz9VXXKCFWCZTm1Y6WHI2685nc/2uPpSf42izFw0DPjf0/Lhtx7SmnQaFbd55pnsL07d77/pdLzTFUQWM7CGDiT/TjHwIDAQAB";

    /**
     * 服务器异步通知页面路径需http://格式的完整路径，不能加?id=123这类自定义参数
     */
    private String notifyUrl = "http://localhost:8080/user/order";

    /**
     * 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数
     */
    private String returnUrl = "http://localhost:8080/user/order";

    /**
     * 签名方式
     */
    private String signType = "RSA2";

    /**
     * 字符编码格式
     */
    private String charset = "UTF-8";

    /**
     * 支付宝网关
     */
    private String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    /**
     * 支付宝网关
     */
    private String logPath;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getGatewayUrl() {
        return gatewayUrl;
    }

    public void setGatewayUrl(String gatewayUrl) {
        this.gatewayUrl = gatewayUrl;
    }

    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }
}
