package com.hrtx.web.websocket;

import com.hrtx.dto.Result;
import com.hrtx.global.ContextUtils;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.pojo.AuctionDeposit;
import com.hrtx.web.service.AuctionDepositService;
import net.sf.json.JSONArray;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/websocket/{numId}/{gId}")
@Component
//@WebListener
public class WebSocketServer {
    public static  final org.slf4j.Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    @Autowired private AuctionMapper auctionMapper;
    @Autowired private AuctionDepositMapper auctionDepositMapper;
    @Autowired private AuctionDepositService auctionDepositService;
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //当前会话的httpsession
    private HttpSession httpSession;
    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {
        this.session =session;
        // 得到httpSession
      //  HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        log.info("config:{}", config.getUserProperties().get("name"));
        log.info("session:{}", config.getUserProperties().get("sessionid"));
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        log.info("【websocket消息】有新的连接, 总数:{}", webSocketSet.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(@PathParam("numId") String numId,@PathParam("gId") String gId,String message, Session session) {
        String msg="";
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> mapData= new HashMap<String, Object>();
        int priceCount=0;//出价次数
        if(!message.trim().contains("与服务器建立连接成功"))
        {
            //最近10次出价记录
            AuctionMapper auctionMapper2=(AuctionMapper) ContextUtils.getContext().getBean("auctionMapper");
            AuctionDepositMapper auctionDepositMapper2=(AuctionDepositMapper) ContextUtils.getContext().getBean("auctionDepositMapper");
            List<Map> goodsAuctionList=auctionMapper2.findAuctionListByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
            AuctionDeposit auctionDeposit=new AuctionDeposit();
            auctionDeposit.setStatus(2);
            auctionDeposit.setgId(NumberUtils.toLong(gId,0L));
            auctionDeposit.setNumId(NumberUtils.toLong(numId,0L));
            //对应的状态：2支付成功保证金列表
            List<Map> auctionDepositList=auctionDepositMapper2.findAuctionDepositListByNumIdAndStatusAndGId(auctionDeposit);
            //出价次数
            List<Map> epSaleGoodsAuctionPriceInfo=auctionMapper2.findAuctionSumEPSaleGoodsByNumIdAndGId(Long.valueOf(numId),Long.valueOf(gId));
            if(epSaleGoodsAuctionPriceInfo!=null&&epSaleGoodsAuctionPriceInfo.size()>0) {
                priceCount = NumberUtils.toInt(String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("priceCount")));
            }
            if(goodsAuctionList!=null&&goodsAuctionList.size()>0)
            {
                mapData.put("goodsAuctionList",goodsAuctionList);
                mapData.put("priceCount",priceCount);
                mapData.put("serviceTime",System.currentTimeMillis());;
            }else
            {
                mapData.put("goodsAuctionList","");
                mapData.put("priceCount","");
                mapData.put("serviceTime",System.currentTimeMillis());;
            }
            if(auctionDepositList!=null&&auctionDepositList.size()>0)
            {
                mapData.put("goodsAuctionDepositList",auctionDepositList);
            }else
            {
                mapData.put("goodsAuctionDepositList","");
            }


            //当前用户是否支付保证金
          /*  AuctionDeposit auctionDeposit=new AuctionDeposit();
            auctionDeposit.setNumId(Long.valueOf(numId));
            auctionDeposit.setgId(Long.valueOf(gId));
            List<Map> goodsAuctionDepositList=auctionDepositService.findAuctionDepositListByNumIdAndGId(auctionDeposit);
            if(goodsAuctionDepositList!=null&&goodsAuctionDepositList.size()>0)
            {
                mapData.put("idDeposit","1");
            }else
            {
                mapData.put("idDeposit","0");
            }*/
            map.put("code", Result.OK);
            map.put("data",mapData);
            //msg="来自客户端的消息:" + message+"numId:"+numId+";gId:"+gId+";map:"+map;
            msg=""+map;
            msg = "{\"code\":\"" +  Result.OK + "\", \"data\":" + JSONArray.fromObject(mapData) + "}";
        }
        else
        {
            msg="来自客户端的消息:" + message+"numId:"+numId+";gId:"+gId;
        }

        System.out.println(msg);
        //群发消息
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        log.info("sdfsdfsdfsdf");
        for (WebSocketServer item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

}