import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jeff-Wang on 2016/6/21.
 */
public class Bookmark {
    public static void addBookMark(String address){
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            File f = new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
            if(f.exists() && !f.isDirectory()) {
                Document document = documentBuilder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");

                Element root = document.getDocumentElement();

                // add elements
                Element newData = document.createElement("Data");

                Element name = document.createElement("Address");
                name.appendChild(document.createTextNode(address));
                newData.appendChild(name);

                root.appendChild(newData);

                DOMSource source = new DOMSource(document);

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                StreamResult result = new StreamResult("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
                transformer.transform(source, result);
            }else{
                createXMLForBookmark(address);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createXMLForBookmark(String address){
        try {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Bookmark");
            doc.appendChild(rootElement);

            // bookmark element
            Element data = doc.createElement("Data");
            rootElement.appendChild(data);

            // Address elements into data Element
            Element link = doc.createElement("Address");
            link.appendChild(doc.createTextNode(address));
            data.appendChild(link);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml"));
            transformer.transform(source, result);
            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }

    public static ArrayList<String> getXMLValue(){
        ArrayList<String> getXMLElement = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            File f = new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
            if(f.exists() && !f.isDirectory()) {
                Document document = builder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
                NodeList nList = document.getElementsByTagName("Data");
                for (int i = 0; i < nList.getLength(); i++)
                {
                    Node nNode = nList.item(i);
                    Element eElement = (Element) nNode;
                    String address = eElement.getElementsByTagName("Address").item(0).getTextContent().toString();
                    getXMLElement.add(address);
                }
            }else{
                createXMLForBookmark("");
                deleteXMLElement("");
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //need to find someway to reload the JPanel
        return getXMLElement;
    }

    public static void deleteXMLElement(String deleteLink){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml");
            NodeList nList = doc.getElementsByTagName("Data");
            for (int i = 0; i < nList.getLength(); i++)
            {
                Node nNode = nList.item(i);
                Element eElement = (Element) nNode;
                String address = eElement.getElementsByTagName("Address").item(0).getTextContent().toString();
                if(address.equals(deleteLink)){
                    nNode.getParentNode().removeChild(eElement);
                    break;
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\Users\\Jeff-Wang\\Documents\\bookmark.xml"));
            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }
}
