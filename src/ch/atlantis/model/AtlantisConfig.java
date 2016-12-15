package ch.atlantis.model;

import ch.atlantis.AtlantisClient;
import com.sun.javafx.scene.layout.region.Margins;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.sql.rowset.spi.XmlWriter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by LorisGrether on 10.10.2016.
 */
public class AtlantisConfig {

    //The path where the configuration file is stored
    private final String ConfigPath = "src/ch/atlantis/res/AtlantisConfig.xml";

    private String configLanguage;

    private Boolean isMusic;

    private Logger logger;

    public AtlantisConfig() {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        File myFile = new File(ConfigPath);

        if (!myFile.exists()) {

            configLanguage = "en-en";
            isMusic = true;

            createAtlantisConfig();
        }
    }

    public boolean readAtlantisConfig() {

        File xmlConfigurationFile = new File(ConfigPath);

        if (!xmlConfigurationFile.exists()) {
            logger.warning("The Configuration file could not be imported.");
            return false;
        }

        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringComments(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlConfigurationFile);

            Node n = doc.getFirstChild();

            for (int i = 0; i < n.getChildNodes().getLength(); i++) {

                if (n.getChildNodes().item(i).getNodeName().equals("Language")) {

                    this.configLanguage = n.getChildNodes().item(i).getTextContent();
                }

                if (n.getChildNodes().item(i).getNodeName().equals("Music")) {

                    this.isMusic = Boolean.valueOf(n.getChildNodes().item(i).getTextContent());
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return false;
        } catch (SAXException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void createAtlantisConfig() {

        try {

            DocumentBuilderFactory myFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder myBuilder = myFactory.newDocumentBuilder();

            Document doc = myBuilder.newDocument();

            Element rootElement = doc.createElement("Configurations");
            doc.appendChild(rootElement);

            Element languageElement = doc.createElement("Language");
            languageElement.appendChild(doc.createTextNode(configLanguage));

            Element musicElement = doc.createElement("Music");
            musicElement.appendChild(doc.createTextNode(isMusic.toString()));

            rootElement.appendChild(languageElement);
            rootElement.appendChild(musicElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(ConfigPath));
            transformer.transform(source, result);

        } catch (ParserConfigurationException parseException) {

        } catch (TransformerException transformerException) {

        } catch (Exception ex) {

        }
    }

    public String getConfigLanguage() {
        return configLanguage;
    }

    public void setConfigLanguage(String configLanguage) {
        this.configLanguage = configLanguage;
    }

    public Boolean getIsMusic() {
        return isMusic;
    }

    public void setIsMusic(Boolean music) {
        isMusic = music;
    }
}
