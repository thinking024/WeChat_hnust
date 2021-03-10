package utils;

import pojo.TextMessage;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息处理工具类（处理微信发来的请求）
 */
public class MessageUtil {
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";  //返回消息类型：文本（公众号向用户）
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";  //请求消息类型：文本（用户向公众号）
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";  //请求消息类型：事件推送
    public static final String EVENT_TYPE_CLICK = "CLICK";  //事件类型：CLICK(自定义菜单点击事件)
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe"; // 事件类型：关注
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe"; // 事件类型：关注

    //解析微信发来的请求（XML）
    public static Map<String,String> parseXML(HttpServletRequest hsRequest) throws Exception{
        //将解析结果存储在HashMap中
        Map<String,String> map = new HashMap();
        InputStream inputStream = hsRequest.getInputStream();
        //System.out.println("获取输入流");

        //读取输入流，得到xml根元素的所有子节点
        SAXReader reader = new SAXReader();
        Document document = reader.read(inputStream);
        Element root = document.getRootElement();
        List<Element> elementList = root.elements();

        //遍历所有子节点
        for (Element e : elementList) {
//            System.out.println(e.getName() + "|" + e.getText());
            map.put(e.getName(), e.getText());
        }

        //释放资源
        inputStream.close();
        return map;
    }

    //文本消息对象转换成xml
    public static String textMessageToXml(TextMessage textMessage){
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    //扩展xstream，使其支持CDATA块
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;

                @SuppressWarnings("unchecked")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }

                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write("<![CDATA[");
                        writer.write(text);
                        writer.write("]]>");
                    } else {
                        writer.write(text);
                    }
                }
            };
        }
    });


}
