package it.polimi.ingsw.commons;

import it.polimi.ingsw.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentCardParser {
    private final Document document;

    public DevelopmentCardParser(String path){
        Document tmp = null;
        File f = new File(path);
        if(!f.exists() || !f.isDirectory()) f = new File("src/xml_src/developmentCards.xml");
        try {
            tmp = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
            tmp.getDocumentElement().normalize();
        }
        catch (ParserConfigurationException |IOException | SAXException e){
            e.printStackTrace();
        }
        finally{
            document = tmp;
        }
    }

    public ArrayList<DevelopmentCard> parseFromXML() throws IOException, SAXException {
        ArrayList<DevelopmentCard> tmp = new ArrayList<>();

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

    public String findCardByID(Colours c, int vp){
        NodeList developmentcards = document.getElementsByTagName("developmentcard");
        //Iterating on the nodelist previously got, single node for every DCCard
        for (int i = 0; i < developmentcards.getLength(); i++) {
            Node developmentcardnode = developmentcards.item(i);
            if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element developmentcardElement = (Element) developmentcardnode;
                int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                if (victorypoints == vp) {
                    Colours colour = Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                    if (colour.equals(c)) {

                        int level = Integer.parseInt(developmentcardElement.getElementsByTagName("level").item(0).getTextContent());

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
                        String tmp = "Livello: "+level+"\nColore: "+colour.toString()+"\nPunti vittoria: "+victorypoints+"\nCosto:";
                        for(Resources r:cost.keySet()){
                            tmp = tmp.concat("\nRisorsa: "+r.toString()+" Quantità: "+cost.get(r));
                        }
                        tmp=tmp.concat("\nRisorse richieste nella produzione:");
                        for(Resources r:requiredresources.keySet()){
                            tmp = tmp.concat("\nRisorsa: "+r.toString()+" Quantità: "+requiredresources.get(r));
                        }
                        tmp=tmp.concat("\nRisorse prodotte nella produzione:");
                        for(Resources r:producedresources.keySet()){
                            tmp = tmp.concat("\nRisorsa: "+r.toString()+" Quantità: "+producedresources.get(r));
                        }
                        tmp=tmp.concat("\nFede prodotta: "+producedfaith);
                        return tmp;
                    }
                }
            }
        }
        return "";
    }

    public HashMap<Resources, Integer> findRequiredResourcesByID(Colours c, int vp) {
        NodeList developmentcards = document.getElementsByTagName("developmentcard");
        //Iterating on the nodelist previously got, single node for every DCCard
        for (int i = 0; i < developmentcards.getLength(); i++) {
            Node developmentcardnode = developmentcards.item(i);
            if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element developmentcardElement = (Element) developmentcardnode;
                int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                if (victorypoints == vp) {
                    Colours colour = Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                    if (colour.equals(c)) {

                        //Getting the requiredresources String, splitting it into multiple strings
                        String[] requiredresourcesString = (developmentcardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");

                        //Generating the HashMap from requiredresourcesString
                        HashMap<Resources, Integer> requiredresources = new HashMap<>();
                        for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                            requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                        }
                        return requiredresources;
                    }
                }
            }
        }
        return null;
    }

    public HashMap<Resources, Integer> findCostByID(Colours c, int vp){
        NodeList developmentcards = document.getElementsByTagName("developmentcard");
        //Iterating on the nodelist previously got, single node for every DCCard
        for (int i = 0; i < developmentcards.getLength(); i++) {
            Node developmentcardnode = developmentcards.item(i);
            if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element developmentcardElement = (Element) developmentcardnode;
                int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                if (victorypoints == vp) {
                    Colours colour = Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                    if (colour.equals(c)) {

                        //Getting the cost String, splitting it into multiple strings
                        String[] costString = (developmentcardElement.getElementsByTagName("cost").item(0).getTextContent()).split("-");

                        //Generating the HashMap from costString
                        HashMap<Resources, Integer> cost = new HashMap<>();
                        for(Resources R : Resources.values()){
                            cost.put(R,0);
                        }
                        for (int j = 0; j < costString.length; j = j + 2) {
                            cost.replace(Resources.valueOf(costString[j]), Integer.parseInt(costString[j + 1]));
                        }
                        return cost;
                    }
                }
            }
        }
        return null;
    }

}
