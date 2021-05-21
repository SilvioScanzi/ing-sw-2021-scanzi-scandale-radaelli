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
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentCardParser {
    private final Document document;

    public DevelopmentCardParser(String path){
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/developmentCards.xml");
        Document tmp = null;
        File f = new File(path);
        if(!f.exists() || !f.isDirectory()) {
            //f = new File(resource.toExternalForm());
        }
        try {
            tmp = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resource);
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
                        String asciiReset = "\u001b[0m";
                        String asciiColor;
                        switch(colour){
                            case Blue: asciiColor = "\u001b[34m";break;
                            case Green: asciiColor = "\u001b[32m";break;
                            case Purple: asciiColor = "\u001b[35m";break;
                            case Yellow: asciiColor = "\u001b[33m";break;
                            default: asciiColor = asciiReset; break;
                        }

                        String tmp = asciiColor+"╔═══════════════════╗"+asciiReset+"\n"+asciiColor+"║"+
                                asciiReset+" Livello "+level+"         "+asciiColor+"║"+asciiReset+"\n"+asciiColor+"║"+asciiReset+" Colore "+colour.toString();
                        if(colour.equals(Colours.Blue)){
                            tmp = tmp+"        ";
                        }
                        else if(colour.equals(Colours.Yellow)){
                            tmp = tmp+"     ";
                        }
                        else if(colour.equals(Colours.Purple) || colour.equals(Colours.Green)){
                            tmp = tmp+"      ";
                        }
                        tmp = tmp+asciiColor+"║"+asciiReset+"\n";
                        tmp = tmp + asciiColor+"║" +asciiReset+" Punti vittoria "+victorypoints;
                        if(victorypoints<10){
                            tmp = tmp+" ";
                        }
                        tmp = tmp+" "+asciiColor+"║"+asciiReset+"\n"+asciiColor+"║"+asciiReset+" Costo             "+asciiColor+"║"+asciiReset;
                        for(Resources r:cost.keySet()){
                            tmp = tmp.concat("\n"+asciiColor+"║"+asciiReset+" Risorsa "+r.abbreviation()+" "+cost.get(r))+"      "+asciiColor+"║"+asciiReset;
                        }
                        tmp = tmp + ("\n"+asciiColor+"║"+asciiReset+" Risorse richieste "+asciiColor+"║"+asciiReset);
                        for(Resources r:requiredresources.keySet()){
                            tmp = tmp.concat("\n"+asciiColor+"║"+asciiReset+" Risorsa "+r.abbreviation()+" "+requiredresources.get(r))+"      "+asciiColor+"║"+asciiReset;
                        }
                        tmp=tmp.concat("\n"+asciiColor+"║"+asciiReset+" Risorse prodotte  "+asciiColor+"║"+asciiReset);
                        for(Resources r:producedresources.keySet()){
                            tmp = tmp.concat("\n"+asciiColor+"║"+asciiReset+" Risorsa "+r.abbreviation()+" "+producedresources.get(r))+"      "+asciiColor+"║"+asciiReset;
                        }
                        if(producedfaith>0) {
                            tmp = tmp + ("\n"+asciiColor+"║"+asciiReset+" Fede prodotta " + producedfaith + "   "+asciiColor+"║"+asciiReset);
                        }
                        tmp = tmp + "\n"+asciiColor+"╚═══════════════════╝"+asciiReset;
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
