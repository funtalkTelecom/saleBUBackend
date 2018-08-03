package com.hrtx.web.websocket;

import com.hrtx.dto.Result;
import com.hrtx.global.ContextUtils;
import com.hrtx.web.mapper.AuctionDepositMapper;
import com.hrtx.web.mapper.AuctionMapper;
import com.hrtx.web.pojo.AuctionDeposit;
import net.sf.json.JSONArray;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 */
@ServerEndpoint(value = "/websocket/{numId}/{gId}")
@Component
//@WebListener
public class WebSocketServer {
    public static  final org.slf4j.Logger log = LoggerFactory.getLogger(WebSocketServer.class);
/*    @Autowired private AuctionMapper auctionMapper;
    @Autowired private AuctionDepositMapper auctionDepositMapper;
    @Autowired private AuctionDepositService auctionDepositService;*/
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static Set<WebSocketServer> wsSet =new HashSet<WebSocketServer>();
    private static ConcurrentHashMap<String,Set<WebSocketServer>> webSocketSet = new ConcurrentHashMap<String,Set<WebSocketServer>>();

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
    public void onOpen(Session session,EndpointConfig config,@PathParam("numId") String numId,@PathParam("gId") String gId) {
        this.session =session;
        // 得到httpSession
      //  HttpSession httpSession= (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
       // log.info("config:{}", config.getUserProperties().get("name"));
       // log.info("session:{}", config.getUserProperties().get("sessionid"));
        String keyId=numId+"_"+gId;
        if(webSocketSet.size()>0)
        {
            wsSet=webSocketSet.get(keyId);
            if(wsSet!=null&&wsSet.size()>0)
            {
                webSocketSet.get(keyId).add(this);
            }else
            {
                if(wsSet==null)
                {
                    wsSet =new HashSet<WebSocketServer>();
                }
                wsSet.add(this);
                webSocketSet.put(keyId,wsSet);
            }
        }else
        {
            if(wsSet==null)
            {
                wsSet =new HashSet<WebSocketServer>();
            }
            wsSet.add(this);
            webSocketSet.put(keyId,wsSet);
        }
        //webSocketSet.put(keyId,this);     //加入set中
        //webSocketSet.putIfAbsent(keyId,this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线总人数为" + getOnlineCount()+";*********** numId_gId:"+keyId+";当前在线人数"+wsSet.size());
        log.info("有新连接加入！当前在线总人数为" + getOnlineCount()+"*********** numId_gId:"+keyId+";当前在线人数"+wsSet.size());
        log.info("【websocket消息】有新的连接, 商品总数:", webSocketSet.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("numId") String numId,@PathParam("gId") String gId) {
        String keyId="";
        String sessionId="";
        sessionId=this.session.getId();
        if(webSocketSet.size()>0)
        {
            Iterator<String> keys=webSocketSet.keySet().iterator();
            while (keys.hasNext())
            {
                keyId=keys.next();
                if(webSocketSet.get(keyId).size()>0)
                {
                    if(webSocketSet.get(keyId).contains(this))
                    {
                        webSocketSet.get(keyId).remove(this); //从set中删除
                    }
                  //  sessionId=webSocketSet.get(keyId).session.getId();

                }
            }
        }
        this.session=null;
       /* try {
            session.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }*/
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前总在线人数为" + getOnlineCount()+"*********** numId_gId"+keyId);
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(String message,@PathParam("numId") String numId,@PathParam("gId") String gId, Session session) {
        String msg="";
        String keyId=numId+"_"+gId;
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

        // 群发消息
        if(webSocketSet.size()>0)
        {
            wsSet=webSocketSet.get(keyId);//socket_Set
            if(wsSet!=null&&wsSet.size()>0)
            {
                log.info("************************************************");
                log.info("信息广播开始:numId_gId:"+keyId+"*****");
                log.info("************************************************");
                for (WebSocketServer item :wsSet)
                {
                   try {
                       item.sendMessage(msg);
                       log.info("信息广播:numId_gId_sessionId:"+keyId+"_"+item.session.getId());
                       log.info("信息广播:信息msg:"+msg);
                   }catch (IOException e) {
                       e.printStackTrace();
                       continue;
                   }
                }
                log.info("***********************************************");
                log.info("信息广播结束:numId_gId:"+keyId+"*****");
                log.info("***********************************************");
            }
        }

    }

    /**
     * 群发自定义消息
     * */
    public static void sendInfo(@PathParam("numId") String numId,@PathParam("gId") String gId) throws IOException {
        String keyId=numId+"_"+gId;
        String msg=auctionAfterInfo(Long.valueOf(numId),Long.valueOf(gId));
        // 群发消息
        if(webSocketSet.size()>0&&msg.trim().length()>0)
        {
            wsSet=webSocketSet.get(keyId);//socket_Set
            if(wsSet!=null&&wsSet.size()>0)
            {
                log.info("************************************************");
                log.info("信息广播开始:numId_gId:"+keyId+"*****");
                log.info("************************************************");
                for (WebSocketServer item :wsSet)//socket_Set
                {
                    try {
                        item.sendMessage(msg);
                        log.info("出价成功、保证金支付成功，广播信息【最近10次出价的记录，状态=2支付成功的保证金列表】");
                        log.info(msg);
                        log.info("信息广播:numId_gId_sessionId:"+keyId+"_"+item.session.getId());
                        log.info("信息广播:信息msg:"+msg);
                    }catch (IOException e) {
                        e.printStackTrace();
                        log.info(String.format("出价成功、保证金支付成功，广播信息异常【[%s]",e.getMessage())+"】");
                        continue;
                    }
                }
                log.info("***********************************************");
                log.info("信息广播结束:numId_gId:"+keyId+"*****");
                log.info("***********************************************");
            }
        }
    }

    /*
	  出价成功后或保证金支付成功后
	  返回广播信息
	 */
    public static String auctionAfterInfo(Long numId,Long gId) {
        AuctionMapper auctionMapper2=(AuctionMapper) ContextUtils.getContext().getBean("auctionMapper");
        AuctionDepositMapper auctionDepositMapper2=(AuctionDepositMapper) ContextUtils.getContext().getBean("auctionDepositMapper");
        Map goodsAuctionMap=new HashMap();
        int priceCount=0;//出价次数
        //出价后的最近10次出价记录
        List<Map> goodsAuctionListAfter=auctionMapper2.findAuctionListByNumIdAndGId(numId,gId);
        //出价次数
        List<Map> epSaleGoodsAuctionPriceInfo=auctionMapper2.findAuctionSumEPSaleGoodsByNumIdAndGId(numId,gId);
        //对应的状态：2支付成功保证金列表
        AuctionDeposit auctionDeposit=new AuctionDeposit();
        auctionDeposit.setStatus(2);
        auctionDeposit.setNumId(numId);
        auctionDeposit.setgId(gId);
        List<Map> auctionDepositLisAftet=auctionDepositMapper2.findAuctionDepositListByNumIdAndStatusAndGId(auctionDeposit);
        if(epSaleGoodsAuctionPriceInfo!=null&&epSaleGoodsAuctionPriceInfo.size()>0) {
            priceCount = NumberUtils.toInt(String.valueOf(epSaleGoodsAuctionPriceInfo.get(0).get("priceCount")));
        }
        if(goodsAuctionListAfter!=null&&goodsAuctionListAfter.size()>0)
        {
            goodsAuctionMap.put("goodsAuctionList",goodsAuctionListAfter);
            goodsAuctionMap.put("priceCount",priceCount);
            goodsAuctionMap.put("serviceTime",java.lang.System.currentTimeMillis());;
            //goodsAuctionListStr="goodsAuctionList:"+goodsAuctionListAfter;
        }else
        {
            goodsAuctionMap.put("goodsAuctionList","");
            goodsAuctionMap.put("priceCount","");
            goodsAuctionMap.put("serviceTime",java.lang.System.currentTimeMillis());;
            //goodsAuctionListStr="goodsAuctionList:"+"";
        }
        if(auctionDepositLisAftet!=null&&auctionDepositLisAftet.size()>0)
        {
            goodsAuctionMap.put("goodsAuctionDepositList",auctionDepositLisAftet);
        }else
        {
            goodsAuctionMap.put("goodsAuctionDepositList","");
        }
        //******************************出价后的向所有WebSocket客户端广播信息
        String msg = "{\"code\":\"" +  Result.OK + "\", \"data\":" + JSONArray.fromObject(goodsAuctionMap) + "}";
        return  msg;
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