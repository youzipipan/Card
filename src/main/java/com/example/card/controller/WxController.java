package com.example.card.controller;


import com.example.card.entities.*;
import com.example.card.model.CardModel;
import com.example.card.model.RoomModel;
import com.example.card.model.SaveModel;
import com.example.card.repository.StudentRepository;
import com.example.card.service.*;
import com.example.card.utils.ResponseUtils;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.xerces.impl.dv.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;

@RequestMapping("/wx")
@Controller
public class WxController {

    private static Logger log = Logger.getLogger(WxController.class);

    final String APPID = "wx855c0d74b2478fd4";
    final String SECRET = "76f29d6450172f389859ee00cc5bb999";

    private String openId;
    @Resource
    private WxService wxService;
    @Resource
    private BookService bookService;
    @Resource
    private CardService cardService;
    @Resource
    private RoomService roomService;
    @Resource
    private DealService dealService;

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

        this.openId = jsonObject.getString("openid");
        Student student = wxService.findByOpenId(openId);
        String sessionId = session.getId();
        session.setAttribute("openId", openId);
        if (student != null) {
            JSONObject json = new JSONObject();
            json.put("student",student);
            json.put("sessionId",sessionId);
            log.info(ResponseUtils.ok("成功", json));
            return ResponseUtils.ok("成功", json);
        } else {
            JSONObject json = new JSONObject();
            json.put("student","");
            json.put("sessionId",sessionId);
            log.info(ResponseUtils.fail(1, "请绑定一卡通信息！！！", json));
            return ResponseUtils.fail(1, "请绑定一卡通信息！！！", json);
        }
    }

    @RequestMapping("/enter")
    @ResponseBody
    public Object login(String cardNumber, String passWord, HttpSession httpSession) {

        if ("admin".equals(cardNumber) && "admin".equals(passWord)) {
            return ResponseUtils.ok();
        } else {
            Student student = wxService.findByCardNumberAndPassWord(cardNumber, passWord);
            if (student != null) {
//                this.openId = (String) httpSession.getAttribute("openId");
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

    @RequestMapping("/getUserInfo")
    @ResponseBody
    public Object getUserInfo(HttpSession httpSession, String studentId) {

//        this.openId = (String) httpSession.getAttribute("openId");
        Student student = new Student();
        if (StringUtils.isNotEmpty(studentId)) {
            student = wxService.findByStudentId(studentId);
        } else {
            student = wxService.findByOpenId(openId);
        }
        if (student != null) {
            return ResponseUtils.ok("成功", student);
        } else {
            return ResponseUtils.fail(1, "获取用户信息失败");
        }
    }

    @RequestMapping("/findAllByStedentId")
    @ResponseBody
    public Object findAllByStedentId(String studentId) {
        JSONObject json = new JSONObject();
        JSONArray ajsonArray = new JSONArray();
        JSONArray yjsonArray = new JSONArray();
        JSONArray njsonArray = new JSONArray();

        if (StringUtils.isEmpty(studentId)) {
            Student student = wxService.findByOpenId(openId);
            if (student != null) {
                List<Book> bookList = bookService.findAllByStudentId(student.getStudentId());
                List<Book> bookListY = bookService.findByStudentIdAndFlag(student.getStudentId(), "1");
                List<Book> bookListN = bookService.findByStudentIdAndFlag(student.getStudentId(), "0");
                if ((bookList != null && bookList.size() > 0) || (bookListY != null && bookListY.size() > 0) || (bookListN != null && bookListN.size() > 0)) {
                    ajsonArray = JSONArray.fromObject(bookList);
                    yjsonArray = JSONArray.fromObject(bookListY);
                    njsonArray = JSONArray.fromObject(bookListN);
                    json.put("allBookList", ajsonArray);
                    json.put("nBookList", njsonArray);
                    json.put("yBookList", yjsonArray);
                    return ResponseUtils.ok("成功", json);
                } else {
                    return ResponseUtils.fail(1, "无图书馆信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        } else {
            List<Book> bookList = bookService.findAllByStudentId(studentId);
            List<Book> bookListY = bookService.findByStudentIdAndFlag(studentId, "1");
            List<Book> bookListN = bookService.findByStudentIdAndFlag(studentId, "0");
            if ((bookList != null && bookList.size() > 0) || (bookListY != null && bookListY.size() > 0) || (bookListN != null && bookListN.size() > 0)) {
                ajsonArray = JSONArray.fromObject(bookList);
                yjsonArray = JSONArray.fromObject(bookListY);
                njsonArray = JSONArray.fromObject(bookListN);
                json.put("allBookList", ajsonArray);
                json.put("nBookList", njsonArray);
                json.put("yBookList", yjsonArray);
                return ResponseUtils.ok("成功", json);
            } else {
                return ResponseUtils.fail(1, "无图书馆信息！");
            }
        }
    }

    @RequestMapping("/getCard")
    @ResponseBody
    public Object getCard(String studentId) {

        CardModel cardModel = new CardModel();
        if (StringUtils.isEmpty(studentId)) {
            Student student = wxService.findByOpenId(openId);
            if (student != null) {
                cardModel.setStudentName(student.getName());
                cardModel.setPhone(student.getPhone());
                Card card = cardService.findByStudentId(student.getStudentId());
                if (card != null) {
                    cardModel.setCardNumber(card.getCardNumber());
                    cardModel.setBalance(card.getBalance());
                    cardModel.setIntegral(card.getIntegral());
                    return ResponseUtils.ok("成功", cardModel);
                } else {
                    return ResponseUtils.fail(1, "无一卡通信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        } else {
            Student student = wxService.findByStudentId(studentId);
            if (student != null) {
                cardModel.setStudentName(student.getName());
                cardModel.setPhone(student.getPhone());
                Card card = cardService.findByStudentId(studentId);
                if (card != null) {
                    cardModel.setCardNumber(card.getCardNumber());
                    cardModel.setBalance(card.getBalance());
                    cardModel.setIntegral(card.getIntegral());
                    return ResponseUtils.ok("成功", cardModel);
                } else {
                    return ResponseUtils.fail(1, "无一卡通信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        }
    }

    @RequestMapping("/getRoom")
    @ResponseBody
    public Object getRoom(String studentId) {

        JSONObject json = new JSONObject();
        JSONArray jsonArrayN = new JSONArray();
        JSONArray jsonArrayW = new JSONArray();
        JSONArray jsonArray = new JSONArray();
        RoomModel roomModel = new RoomModel();
        List<RoomModel> roomModelN = new ArrayList<>();
        List<RoomModel> roomModelW = new ArrayList<>();
        List<RoomModel> roomModels = new ArrayList<>();
        if (StringUtils.isEmpty(studentId)) {
            Student student = wxService.findByOpenId(openId);
            if (student != null) {

                List<Room> roomN = roomService.findByStudentId(student.getStudentId(), "0");
                List<Room> roomW = roomService.findByStudentId(student.getStudentId(), "1");
                List<Room> room = roomService.findByStudentId(student.getStudentId(), "2");
                if ((roomN != null && roomN.size() > 0) || (roomW != null && roomW.size() > 0) || (room != null && room.size() > 0)) {
                    roomN.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModelN.add(roomModel);
                    });
                    jsonArrayN = JSONArray.fromObject(roomModelN);
                    roomW.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModelW.add(roomModel);
                    });
                    jsonArrayW = JSONArray.fromObject(roomModelW);
                    room.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModels.add(roomModel);
                    });
                    jsonArray = JSONArray.fromObject(roomModels);
                    json.put("N", jsonArrayN);
                    json.put("W", jsonArrayW);
                    json.put("Y", jsonArray);
                    return ResponseUtils.ok("成功", json);
                } else {
                    return ResponseUtils.fail(1, "无宿舍信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        } else {
            Student student = wxService.findByStudentId(studentId);
            if (student != null) {
                List<Room> roomN = roomService.findByStudentId(student.getStudentId(), "0");
                List<Room> roomW = roomService.findByStudentId(student.getStudentId(), "1");
                List<Room> room = roomService.findByStudentId(student.getStudentId(), "2");
                if ((roomN != null && roomN.size() > 0) || (roomW != null && roomW.size() > 0) || (room != null && room.size() > 0)) {
                    roomN.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModelN.add(roomModel);
                    });
                    jsonArrayN = JSONArray.fromObject(roomModelN);
                    roomW.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModelW.add(roomModel);
                    });
                    jsonArrayW = JSONArray.fromObject(roomModelW);
                    room.forEach(e -> {
                        roomModel.setReturnTime(e.getReturnTime());
                        roomModel.setRoom(e.getRoom());
                        roomModel.setFlag(e.getFlag());
                        roomModel.setName(student.getName());
                        roomModel.setPhone(student.getPhone());
                        roomModels.add(roomModel);
                    });
                    jsonArray = JSONArray.fromObject(roomModels);
                    json.put("N", jsonArrayN);
                    json.put("W", jsonArrayW);
                    json.put("Y", jsonArray);
                    return ResponseUtils.ok("成功", json);
                } else {
                    return ResponseUtils.fail(1, "无宿舍信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        }
    }

    @RequestMapping("/getDeal")
    @ResponseBody
    public Object getDeal(String studentId) {

        if (StringUtils.isEmpty(studentId)) {
            Student student = wxService.findByOpenId(openId);
            if (student != null) {
                List<Deal> deal = dealService.findByStudentId(student.getStudentId());
                if (deal != null && deal.size() > 0) {
                    return ResponseUtils.ok("成功", deal);
                } else {
                    return ResponseUtils.fail(1, "无一卡通信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        } else {
            Student student = wxService.findByStudentId(studentId);
            if (student != null) {
                List<Deal> deal = dealService.findByStudentId(student.getStudentId());
                if (deal != null && deal.size() > 0) {
                    return ResponseUtils.ok("成功", deal);
                } else {
                    return ResponseUtils.fail(1, "无一卡通信息！");
                }
            } else {
                return ResponseUtils.fail(1, "查询用户信息失败！");
            }
        }
    }

    @RequestMapping("/save")
    @ResponseBody
    public Object save(SaveModel saveModel) {

        String cardNumber = wxService.save(saveModel);
        String msg = "一卡通账号为：" + cardNumber + "--------密码为：" + saveModel.getPassWord();
        return ResponseUtils.ok(msg);
    }

    @RequestMapping("/query")
    @ResponseBody
    public Object query(String cardNumber) {

        Student student = wxService.query(cardNumber);
        if (student != null) {
            return ResponseUtils.ok("查询成功！", student);
        } else {
            return ResponseUtils.fail(1, "查无该用户！！！");
        }
    }

    @RequestMapping("/getAllStudent")
    @ResponseBody
    public Object getAllStudent() {

        List<Student> students = wxService.getAllStudent();
        if (students != null && students.size() > 0) {
            return ResponseUtils.ok("查询成功！！！", students);
        } else {
            return ResponseUtils.fail(1, "查询失败！！！");
        }
    }

    @RequestMapping("/unBind")
    @ResponseBody
    public Object unBind(String studentId) {

        if (StringUtils.isNotEmpty(openId)) {
            Student student = wxService.findByOpenId(openId);
            if (student != null && student.getStudentId().equals(studentId)) {
                if (StringUtils.isNotEmpty(studentId)) {
                    wxService.unBind(null, studentId);
                    return ResponseUtils.ok("微信解绑成功！！！");
                } else {
                    return ResponseUtils.fail(1, "微信解绑失败！！！");
                }
            } else {
                return ResponseUtils.fail(1, "微信解绑只允许本人操作！！！");
            }
        } else {
            return ResponseUtils.fail(1, "微信解绑只允许本人操作！！！");
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    public Object delete(String cardNumber) {

        if (StringUtils.isNotEmpty(cardNumber)) {
            Student student = wxService.findByCardNumber(cardNumber);
            if (student != null) {
                wxService.delete(cardNumber);
                return ResponseUtils.ok("注销成功！！！");
            } else {
                return ResponseUtils.fail(1, "无此用户！！！");
            }
        } else {
            return ResponseUtils.fail(1, "注销失败！！！");
        }
    }

    @RequestMapping("/recharge")
    @ResponseBody
    public Object recharge(String cardNumber,String balance) {

        Student student = wxService.findByCardNumber(cardNumber);
        if(student!=null){
            Card card = wxService.findCard(cardNumber);
            if(card!=null){
                BigDecimal money = new BigDecimal(balance).add(new BigDecimal(card.getBalance()));
                wxService.updateCard(money.toString(),cardNumber);
                return ResponseUtils.ok("已为卡号："+cardNumber+"充值"+balance+"成功，余额为"+money);
            }else{
                return ResponseUtils.fail(1, "查无此号！！！");
            }
        }else{
            return ResponseUtils.fail(1, "查无此号！！！");
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



