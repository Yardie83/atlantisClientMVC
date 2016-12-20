package ch.atlantis.util;

import ch.atlantis.AtlantisClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.logging.Logger;

/**
 * Created by Loris Grether on 22.08.2016.
 *
 * This class handles the several language files.
 * As soon as the program gets started and the model is instantiated, all the language files in
 * a specific folder will be read, processed and loaded into the program.
 *
 */

public class LanguageHandler {

    private ArrayList<Language> languageList;

    private Logger logger;

    public LanguageHandler() {

        logger = Logger.getLogger(AtlantisClient.AtlantisLogger);

        languageList = new ArrayList<>();
        getFiles();
    }

    private void getFiles() {

        File folder = new File("src/ch/atlantis/res/languages/");

        if (!folder.isDirectory()){
            //TODO: Log error Message
        }

        //en-US --> locales
        File[] myFiles = folder.listFiles();

        for (File file : myFiles){
            if (file.exists()){
                if (file.getName().endsWith(".xml"));

                readLanguageFile(file.getPath());
                //TODO: Log file.getPath(); was read
            }
        }
    }

    private void readLanguageFile(String path) {

        File xmlLanguageFile = new File(path);
        String culture = "";

        try {

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setIgnoringComments(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlLanguageFile);

            culture = doc.getDocumentElement().getAttribute("Culture");

            this.languageList.add(new Language(culture, getFileValues(doc)));


        } catch (ParserConfigurationException parseException) {

        } catch (SAXException asf) {

        } catch (FileNotFoundException notFoundException) {
            logger.info("Language File could not be found.");

        } catch (IOException ioException) {

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private Hashtable<String, String> getFileValues(Document doc) {

        NodeList nodes = doc.getElementsByTagName("String");
        Element element = doc.getDocumentElement();

        Hashtable<String, String> values = new Hashtable<String, String>();

        for (int i = 0; i <= nodes.getLength() - 1; i++) {

            String id = nodes.item(i).getAttributes().getNamedItem("Id").toString();
            String value = element.getElementsByTagName("String").item(i).getChildNodes().item(0).getNodeValue();

            values.put(modifyValue(id), value);
        }

        return values;
    }

    private String modifyValue(String id) {

        String[] split = id.split("\"");
        return split[1];
    }

    public ArrayList<Language> getLanguageList() {
        return this.languageList;
    }
}