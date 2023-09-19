package com.example.mall.order.constant;

import com.alibaba.nacos.common.utils.IPUtil;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "alipay.config")
public class AlipayProperties {
    private String ipAddress = "192.168.137.1";
    private String app_id = "9021000123646775";
    private String gatewayUrl = "https://openapi-sandbox.dl.alipaydev.com/gateway.do";
    private String merchant_private_key = "MIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCS3gpJnJUzp7c5mjP56dA+/qYK0IKDjL1r/rBHjdWXU4kOHrY0AElhF/7Dn43FhgkHERBXcDH1WDoJl0S3WPeJlsTX/wIqgvpBq6l9kvteEnjkBSNOdoYZIQzrul178kSMgun1wpGqZBu5Swbet4FopPTd99IxFH8c4E1z2WtFXp404gc6baEm7WwujT7dftPRx12bbAHY1ATAzClwJLMtTXAu33hiTN7z23vnUs5uEQzENVcoxmHNPweD1PGlBUcP5J4Qyf1+/nVaoERxD1y0I0dlMy1DQQvjbH+wp7AdxiV9C2GBg0FYIbetKu0n62+i4VLY58WEc4h1q+g6nI0PAgMBAAECggEBAJASEqNtYAuBDcMK2svgqnDxWq6nJjCnwjWLbR90Kj+9KWhZhkDyuwjtAfSisu898eMlwRMVM1zKxHweEMGPG5yz8rs0I8rSG26KV8Sgh8cbdc2woJbau+cwvb0MX6Iz4Ty5O4ri6APvGiclS0L1XpPppW+NKHsO8TbBbhkkAg/UHed4OYiX9AhBwApCUlL5vdRJURac8T2SBgbs5gO6yA1iPRNUUmKK5m2htRSglySJQoeGJewTitnjGxsobPNOTfMMGdikhFsFDxa2UOE6zduMzgc4IVHKWPhzFrwB9GBfkhe+3WQwuKRfo5yEaG54YJBmDhnbqd7zSEQ/rw0V5zkCgYEA+YFCig6aj92hQxKC/gXICC4TWjKEJsCGdDoQfrvZWAR+mb+cgdRg6LFr8bnO/WfYbfvvQ/2mRtkk1HDF20GBbVx+qMCMawZigpRgcU962+OaV7ezKqr5Mz1Czd2qKscANz2s7K1LksUkM/tB36L4mnsUtAhA05rAljklDdXQyisCgYEAlrDJjAlq+6e4UamRSQM2nnmtJpts6V5RWV0U2jH/4bwsYJ3KNaNYBq3ize41yCPRYtqSiCa8QacujjzcLEVfxrmdL2+eDGsWrHkISgUnWgpOsF8a+/L9Bm1a62tydpec0ooVcQeoIoKhi7ja3HEB43Wzd8ur62pv41+Yuy36yq0CgYEA6Y+T3zK+gzAgNm5My0hUMTwYh5XEWZZeBs0YBdsJITjL3lcdRfeuZAU6HAUo+9RLKOHu38HA9o9sx6Vtwcq3Qs2UD5p3l/RZIQ2OnmOdidus7rIa+TUlpE6Ti60WD4dL72o/xyqOFS2Qr0xkJNolSE8xWTpknPE/mSCTTDk8gxMCgYEAjg3oYdf6/3NzwZ/9cvvRgIGp5iw89p5QR9MEtlDOLKnsl8QH/JqYf+tqVMFL9/k0CWGNI0aIz8dfjvz2fdGEYV1XPYGWV5SYUkzisIy9NRdkaMWvNXYyat2qCSZISF3Sm3NKlTwnlyjYS1QfFhJ+OkQZxEO/NPUotwrGeyX0Nx0CgYAnNaKwArSJPJ9Td9Ytlxob0R2yL7Ysk71WOWh9Xzl6hxm0FI68E6p3zx1MIqp0SmAgDLLV5ek01b5XYQNEv5URVDMmPoIAQP48O/zSdbHgwrdZyja3bSkWcI+IAzLsNzmf4kThuW4Uj6dlFFP3vGOgelqwHgRaz4jw/n+PuhNgHg==";
    private String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAiZxeBPbNUUg/+YRIBOa3v6yDvjQYJ+1NEHScLrqsTuK0YcMAev7LXO7HCodZGpVtseDrxYP4iOBw2p7SaUIYV/AevEaS6sCwu7iKO02Ki1rUkV0ifqJBXZP1eZNzwFstpMdWtsQgwhN6oHQlZCIzpQKQTYUC6e9vO9Jspf+EAvqJIVqSdh12LrQygHcqIj4bbYFHyvpfkZpWwweGaf72azxGXMK3ye5o3qLFMzatKkIPTDTgZNWg5vb6uBKlhux4JfRw9ZMGKF5UGpTpQLYPMmFe1jR1B8Yn2xPMsZaQjwYgove0dN9npd+IKdmp5I0Zx00gZ01fYu/YA2b82Ej9XwIDAQAB";
    private String notify_url;
    private String notifyPath = "/order/order/paysuccess";
    private String charset = "UTF8";
    private String format = "json";
    private String sign_type = "RSA2";
    private String sellerId = "2088721006089504";
    private String productCode = "FAST_INSTANT_TRADE_PAY";
    private String subject = "商品支付";
    private String payChannel = "balance";
}
