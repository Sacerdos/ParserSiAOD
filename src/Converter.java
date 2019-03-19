import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;

public class Converter/* implements CurrencyConverter*/
{
    private static final String FILENAME = "staff.xml";
    public String getConversionRate(String from, String to) throws IOException
    {
        HttpClientBuilder builder = HttpClientBuilder.create();
        try (CloseableHttpClient httpclient = builder.build())
        {
            HttpGet httpGet = new HttpGet("http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=01/03/2019&date_req2=11/03/2019&VAL_NM_RQ=R01375");
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpGet, responseHandler);
            HttpResponse response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                BufferedInputStream bis = new BufferedInputStream(entity.getContent());
                String filePath = "sample.xml";
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(filePath)));
                int inByte;
                while((inByte = bis.read()) != -1) bos.write(inByte);
                bis.close();
                bos.close();
                // Проверка гита
            }

            return responseBody;
        }
    }

    public static void main(String[] arguments) throws IOException
    {
        Converter Converter = new Converter();
        String current = Converter.getConversionRate("USD", "ILS");
        System.out.println(current);
        String filepath = "sample.xml";
        File xmlFile = new File(filepath);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document doc = builder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Корневой элемент: " + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("Record");

            // создадим из него список объектов Language
            List<Record> langList = new ArrayList<Record>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                langList.add(getRecord(nodeList.item(i)));

            }

            // печатаем в консоль информацию по каждому объекту Language

            for (Record lang : langList) {
                System.out.println(lang.toString());

            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }

    }
    private static Record getRecord(Node node) {
    Record lang = new Record();
    if (node.getNodeType() == Node.ELEMENT_NODE) {
        Element element = (Element) node;
        lang.setNominal(Integer.parseInt(getTagValue("Nominal", element)));
        String str = getTagValue("Value", element);
        str=str.replace(',', '.');
        lang.setValue(Double.parseDouble(str));
        lang.setDate(element.getAttribute("Date"));
    }

    return lang;
}
    // получаем значение элемента по указанному тегуement element) {
    //        NodeList nodeList = element.getElementsByTag
    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}