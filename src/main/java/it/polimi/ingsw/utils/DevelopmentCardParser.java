package it.polimi.ingsw.utils;

import it.polimi.ingsw.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentCardParser {
    private final String path;

    public DevelopmentCardParser(String path){
        this.path = path;
    }

    public ArrayList<DevelopmentCard> parseFromXML() throws IOException, SAXException {
        ArrayList<DevelopmentCard> tmp = new ArrayList<>();

        //Instantiating objects for the XML file parsing
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        /*Getting the XML file via path. For future updates: pathname is a String, it could be used another String
        given to the Constructor, eventually resorting to the default one, especially useful for parameter editor*/
        assert builder != null;
        Document document = builder.parse(new File(path));
        document.getDocumentElement().normalize();

        /* Only used for testing to identify if the DOM parser worked as intended
        Element root = document.getDocumentElement(); */

        NodeList developmentcards = document.getElementsByTagName("developmentcard");
        //Iterating on the nodelist previously got, single node for every DCCard
        for (int i = 0; i < developmentcards.getLength(); i++) {
            Node developmentcardnode = developmentcards.item(i);
            if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element developmentcardElement = (Element) developmentcardnode;
                int level = Integer.parseInt(developmentcardElement.getElementsByTagName("level").item(0).getTextContent());
                Colours colour = Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());

                //Getting the cost String, splitting it into multiple strings
                String[] costString = (developmentcardElement.getElementsByTagName("cost").item(0).getTextContent()).split("-");

                //Getting the requiredresources String, splitting it into multiple strings
                String[] requiredresourcesString = (developmentcardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");

                //Getting the producedresources String, splitting it into multiple strings
                String[] producedresourcesString = (developmentcardElement.getElementsByTagName("producedresources").item(0).getTextContent()).split("-");

                int producedfaith = Integer.parseInt(developmentcardElement.getElementsByTagName("producedfaith").item(0).getTextContent());

                //Generating the HashMap from costString
                HashMap<Resources, Integer> cost = new HashMap<>();
                for (int j = 0; j < costString.length; j = j + 2) {
                    cost.put(Resources.valueOf(costString[j]), Integer.parseInt(costString[j + 1]));
                }

                //Generating the HashMap from requiredresourcesString
                HashMap<Resources, Integer> requiredresources = new HashMap<>();
                for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                    requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                }

                //Generating the HashMap from producedresourcesString
                HashMap<Resources, Integer> producedresources = new HashMap<>();
                if (!(producedresourcesString[0].equals("NULL"))) {
                    for (int j = 0; j < producedresourcesString.length; j = j + 2) {
                        producedresources.put(Resources.valueOf(producedresourcesString[j]), Integer.parseInt(producedresourcesString[j + 1]));
                    }
                }

                //Generating a new DCCard with the previously calculated attributes
                DevelopmentCard DC = new DevelopmentCard(level, colour, victorypoints, cost, requiredresources, producedresources, producedfaith);
                tmp.add(DC);
            }
        }
        return tmp;
    }
}
