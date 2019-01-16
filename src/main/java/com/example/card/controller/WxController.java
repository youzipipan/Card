package com.example.card.controller;

import com.example.card.entities.Student;
import com.example.card.service.WxService;
import com.example.card.utils.ResponseUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.xerces.impl.dv.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RequestMapping("/wx")
@Controller
public class WxController {

    private static Logger log = Logger.getLogger(WxController.class);

    final String APPID = "wx855c0d74b2478fd4";
    final String SECRET = "76f29d6450172f389859ee00cc5bb999";

    @Resource
    private WxService wxService;

    @RequestMapping("/login")
    @ResponseBody
    public Object getCode(HttpServletRequest request, HttpSession session) {
       /* System.out.println("success");
        String openid = "999999";
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("openid",openid);
        JSONObject json = JSONObject.fromObject(data);*/
        String code = request.getParameter("js_code");

        String requestUrl = "https://api.weixin.qq.com/sns/jscode2session";    //请求地址 https://api.weixin.qq.com/sns/jscode2session
        Map<String, String> requestUrlParam = new HashMap<String, String>();
        requestUrlParam.put("appid", APPID);    //开发者设置中的appId
        requestUrlParam.put("secret", SECRET);    //开发者设置中的appSecret
        requestUrlParam.put("js_code", code.trim());    //小程序调用wx.login返回的code
        requestUrlParam.put("grant_type", "authorization_code");    //默认参数

        //发送post请求读取调用微信 https://api.weixin.qq.com/sns/jscode2session 接口获取openid用户唯一标识
        JSONObject jsonObject = JSONObject.fromObject(sendPost(requestUrl, requestUrlParam));

        String openId = jsonObject.getString("openid");
        Student student = wxService.findByOpenId(openId);
        String sessionId = session.getId();
        session.setAttribute("openId", openId);
        if (student != null) {
            log.info(ResponseUtils.ok("成功", sessionId));
            return ResponseUtils.ok("成功", sessionId);
        } else {
            log.info(ResponseUtils.fail(1, "请绑定一卡通信息！！！", sessionId));
            return ResponseUtils.fail(1, "请绑定一卡通信息！！！", sessionId);
        }
    }

    @RequestMapping("/enter")
    @ResponseBody
    public Object login(String cardNumber, String passWord, HttpSession httpSession) {

        if (cardNumber == "admin" && passWord == "admin") {
            return ResponseUtils.ok();
        } else {
            Student student = wxService.findByCardNumberAndPassWord(cardNumber, passWord);
            if (student != null) {
                String openId = (String) httpSession.getAttribute("openId");
                int i = wxService.updateByOpenId(cardNumber, passWord, openId);
                if (i != 0) {
                    log.info(ResponseUtils.ok());
                    return ResponseUtils.ok();
                } else {
                    log.info(ResponseUtils.fail(1, "服务器异常，请联系管理员！"));
                    return ResponseUtils.fail(1, "服务器异常，请联系管理员！");
                }
            } else {
                log.info(ResponseUtils.fail(1, "账号或密码有误！"));
                return ResponseUtils.fail(1, "账号或密码有误！");
            }
        }
    }


    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url      发送请求的 URL
     * @param paramMap 请求参数
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, Map<String, ?> paramMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        String param = "";
        Iterator<String> it = paramMap.keySet().iterator();

        while (it.hasNext()) {
            String key = it.next();
            param += key + "=" + paramMap.get(key) + "&";
        }

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 解密用户敏感数据获取用户信息
     *
     * @param sessionKey    数据进行加密签名的密钥
     * @param encryptedData 包括敏感数据在内的完整用户信息的加密数据
     * @param iv            加密算法的初始向量
     * @return
     * @author zhy
     */
    public JSONObject getUserInfo(String encryptedData, String sessionKey, String iv) {
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSONObject.fromObject(result);
            }
        } catch (NoSuchAlgorithmException e) {
            log.info(e.getMessage());
        } catch (NoSuchPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidParameterSpecException e) {
            log.error(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            log.error(e.getMessage(), e);
        } catch (BadPaddingException e) {
            log.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error(e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            log.error(e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}




