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
import java.util.*;

public class LeaderCardParser {
    private final Document document;

    public LeaderCardParser(String path){
        Document tmp = null;
        File f = new File(path);
        if(!f.exists() || f.isDirectory()) f = new File("src/xml_src/leaderCards.xml");
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

    public ArrayList<LeaderCard> parseFromXML() throws IOException, SAXException {
        ArrayList<LeaderCard> tmp = new ArrayList<>();

        NodeList leadercards = document.getElementsByTagName("leadercard");
        //Iterating on the nodelist previously got, single node for every LCCard
        for (int i = 0; i < leadercards.getLength(); i++) {
            Node leadercardnode = leadercards.item(i);
            if (leadercardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element leadercardElement = (Element) leadercardnode;

                //Getting the number of victorypoints
                int victorypoints = Integer.parseInt(leadercardElement.getElementsByTagName("victorypoints").item(0).getTextContent());

                //Getting the requiredColours String, splitting it into multiple strings
                String[] requiredcoloursString = (leadercardElement.getElementsByTagName("requiredcolours").item(0).getTextContent()).split("-");

                //Getting the requiredresources String, splitting it into multiple strings
                String[] requiredresourcesString = (leadercardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");

                //Getting the ability type
                String type = leadercardElement.getElementsByTagName("abilitytype").item(0).getTextContent();

                //Getting the resource type
                Resources restype = Resources.valueOf(leadercardElement.getElementsByTagName("restype").item(0).getTextContent());

                //Getting the initial capacity
                int capacity = Integer.parseInt(leadercardElement.getElementsByTagName("capacity").item(0).getTextContent());

                //Generating the HashMap from costString
                HashMap<Colours, Pair<Integer, Integer>> requiredcolours = new HashMap<>();
                if (!requiredcoloursString[0].equals("NULL")) {
                    for (int j = 0; j < requiredcoloursString.length; j = j + 3) {
                        requiredcolours.put(Colours.valueOf(requiredcoloursString[j]), new Pair<>(Integer.parseInt(requiredcoloursString[j + 1]), Integer.parseInt(requiredcoloursString[j + 2])));
                    }
                }

                //Generating the HashMap from requiredresourcesString
                HashMap<Resources, Integer> requiredresources = new HashMap<>();
                if (!requiredresourcesString[0].equals("NULL")) {
                    for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                        requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                    }
                }

                //Generating a new LCCard with the previously calculated attributes
                LeaderCard LC = new LeaderCard(victorypoints, requiredcolours, requiredresources, type, restype, capacity);
                tmp.add(LC);
            }
        }
        return tmp;
    }

    public String findCardByID(Resources r, int vp, int sr){
        NodeList leadercards = document.getElementsByTagName("leadercard");
        //Iterating on the nodelist previously got, single node for every LCCard
        for (int i = 0; i < leadercards.getLength(); i++) {
            Node leadercardnode = leadercards.item(i);
            if (leadercardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element leadercardElement = (Element) leadercardnode;

                int victorypoints = Integer.parseInt(leadercardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                if(victorypoints == vp) {
                    Resources restype = Resources.valueOf(leadercardElement.getElementsByTagName("restype").item(0).getTextContent());
                    if (restype.equals(r)) {

                        String[] requiredcoloursString = (leadercardElement.getElementsByTagName("requiredcolours").item(0).getTextContent()).split("-");
                        String[] requiredresourcesString = (leadercardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");
                        String type = leadercardElement.getElementsByTagName("abilitytype").item(0).getTextContent();
                        int capacity = Integer.parseInt(leadercardElement.getElementsByTagName("capacity").item(0).getTextContent());

                        //Generating the HashMap from costString
                        HashMap<Colours, Pair<Integer, Integer>> requiredcolours = new HashMap<>();
                        if (!requiredcoloursString[0].equals("NULL")) {
                            for (int j = 0; j < requiredcoloursString.length; j = j + 3) {
                                requiredcolours.put(Colours.valueOf(requiredcoloursString[j]), new Pair<>(Integer.parseInt(requiredcoloursString[j + 1]), Integer.parseInt(requiredcoloursString[j + 2])));
                            }
                        }

                        //Generating the HashMap from requiredresourcesString
                        HashMap<Resources, Integer> requiredresources = new HashMap<>();
                        if (!requiredresourcesString[0].equals("NULL")) {
                            for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                                requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                            }
                        }
                        Ability ability = null;

                        if(type.equals("DiscountAbility")){
                            ability = new DiscountAbility(restype,capacity);
                        }
                        else if(type.equals("ExtraSlotAbility")){
                            ability = new ExtraSlotAbility(restype,capacity);
                            try {
                                ability.doUpdateSlot(r,sr);
                            }catch(Exception e){e.printStackTrace();}
                        }
                        else if(type.equals("ProductionPowerAbility")){
                            ability = new ProductionPowerAbility(restype);
                        }
                        else if(type.equals("WhiteMarbleAbility")){
                            ability = new WhiteMarbleAbility(restype);
                        }

                        String tmp = "Punti vittoria: "+vp+"\n";
                        for(Colours c: requiredcolours.keySet()){
                            tmp=tmp.concat("Colore richiesto: "+c.toString()+"; Numero di carte richiesto: "+requiredcolours.get(c).getKey()+"; Livello richiesto: "+requiredcolours.get(c).getValue()+"\n");
                        }
                        for(Resources res:requiredresources.keySet()){
                            tmp=tmp.concat("Risorsa richiesta: "+res.toString()+"; Numero: "+requiredresources.get(res)+"\n");
                        }

                        tmp=tmp.concat(ability.toString()+"\n");
                        return tmp;
                    }
                }
            }
        }
        return "";
    }

    public String findTypeByID(Resources r, int vp){
        NodeList leadercards = document.getElementsByTagName("leadercard");
        //Iterating on the nodelist previously got, single node for every LCCard
        for (int i = 0; i < leadercards.getLength(); i++) {
            Node leadercardnode = leadercards.item(i);
            if (leadercardnode.getNodeType() == Node.ELEMENT_NODE) {
                Element leadercardElement = (Element) leadercardnode;

                int victorypoints = Integer.parseInt(leadercardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                if(victorypoints == vp) {
                    Resources restype = Resources.valueOf(leadercardElement.getElementsByTagName("restype").item(0).getTextContent());
                    if (restype.equals(r)) {
                       return leadercardElement.getElementsByTagName("abilitytype").item(0).getTextContent();
                    }
                }
            }
        }
        return "";
    }
}
