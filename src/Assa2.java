import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class Assa2/* implements CurrencyConverter*/
{
    private static final String FILENAME = "staff.xml";
    public JPanel panel1;

    public Assa2() {
        panel1.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                super.componentShown(e);
            }
        });
    }

    public String getConversionRate(String from, String to) throws IOException
    {

        HttpClientBuilder builder = HttpClientBuilder.create();
        try (CloseableHttpClient httpclient = builder.build())
        {
            HttpGet httpGet = new HttpGet("http://www.cbr.ru/scripts/XML_dynamic.asp?date_req1=01/03/2017&date_req2=11/03/2019&VAL_NM_RQ=R01375");
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
                // Проверка гита а то хрень

            }

            return responseBody;
        }
    }

    public static void main(String[] arguments) throws IOException
    {
        Assa2 Converter = new Assa2();
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
            List<Record> recList = new ArrayList<Record>();
            for (int i = 0; i < nodeList.getLength(); i++) {
                recList.add(getRecord(nodeList.item(i)));

            }
            int h=1;
            for (Record rec : recList) {
                System.out.println(h++ + " " + rec.toString());

            }
            XYSeries series1 = new XYSeries("sin(a)");
            XYSeries series2 = new XYSeries("sin(b)");
            for(int i = 0; i < nodeList.getLength(); i++){
                if (recList.get(i).getNominal()==1){
                    series1.add(i+1, recList.get(i).getValue()*10);
                    if (i!=0 && i%10==0){
                        System.out.println(i%10);
                        if (recList.get(i).getNominal()==1){
                            if (recList.get(i-1).getNominal()==1){
                                series2.add(i+1, (recList.get(i).getValue()*10+recList.get(i-1).getValue()*10)/2);
                            } else {
                                series2.add(i+1, (recList.get(i).getValue()*10+recList.get(i-1).getValue())/2);
                            }
                        } else {
                            if (recList.get(i-1).getNominal()==1){
                                series2.add(i+1, (recList.get(i).getValue()+recList.get(i-1).getValue()*10)/2);
                            } else {
                                series2.add(i+1, (recList.get(i).getValue()+recList.get(i-1).getValue())/2);
                            }
                        }

                    } else {
                        if (recList.get(i).getNominal()==1){
                            series2.add(i+1, (recList.get(i).getValue()*10));
                        } else {
                            series2.add(i+1, (recList.get(i).getValue()));
                        }

                    }

                } else {
                    series1.add(i+1, recList.get(i).getValue());
                    if (i%5==0){
                        series2.add(i+1, (recList.get(i).getValue()+recList.get(i).getValue())/2);
                    }

                }
            }

            XYDataset xyDataset = new XYSeriesCollection();
            ((XYSeriesCollection) xyDataset).addSeries(series1);
            ((XYSeriesCollection) xyDataset).addSeries(series2);
            JFreeChart chart = ChartFactory
                    .createXYLineChart("y = sin(x)", "x", "y",
                            xyDataset,
                            PlotOrientation.VERTICAL,
                            true, true, true);
            /*JFrame frame =
                    new JFrame("MinimalStaticChart");
            // Помещаем график на фрейм
            frame.getContentPane()
                    .add(new ChartPanel(chart));
            frame.setSize(400,300);
            frame.show();*/
            JPanel jPanel1 = new JPanel();
            jPanel1.setLayout(new java.awt.BorderLayout());
            ChartPanel CP = new ChartPanel(chart);
            jPanel1.add(CP, BorderLayout.CENTER);
            jPanel1.validate();
            jPanel1.setVisible(true);

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