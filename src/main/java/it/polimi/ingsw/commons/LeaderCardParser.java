package it.polimi.ingsw.commons;

import it.polimi.ingsw.model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class LeaderCardParser {
    private final Document document;

    public LeaderCardParser(){
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/leaderCards.xml");
        Document tmp = null;

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

    /**
     * Method used to parse leader cards from the relative xml file. To do this, it is used
     * a document to extract the information from.
     * @return ArrayList of leader cards from the xml file
     */
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

    /**
     * Method used for the CLI to print a leader card. If the player is currently
     * using the colored version of the cli, color codes are also added.
     * @param r resource which is part of the leader card ability, used to identify it
     * @param vp victory points given by the card, used to identify it
     * @param sr current resources on the card (if any are present)
     * @return a String which represent the leader card
     */
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

                        String tmp = "╔═══════════════════╗\n";
                        tmp = tmp + "║ Punti vittoria: "+vp+" ║\n";
                        tmp = tmp + "║ Requisiti         ║";
                        for(Colours c: requiredcolours.keySet()){
                            tmp=tmp.concat("\n║ Colore "+c.toString());
                            if(c.equals(Colours.Blue)){
                                tmp = tmp+"        ║";
                            }
                            else if(c.equals(Colours.Yellow)){
                                tmp = tmp+"     ║";
                            }
                            else if(c.equals(Colours.Purple) || c.equals(Colours.Green)){
                                tmp = tmp+"      ║";
                            }
                            tmp = tmp + "\n║ Carte richieste "+requiredcolours.get(c).getKey() + " ║";
                            if(requiredcolours.get(c).getValue()>1)
                                tmp = tmp + "\n║ Livello "+requiredcolours.get(c).getValue()+"         ║";
                        }
                        tmp = tmp+"\n";
                        for(Resources res:requiredresources.keySet()){
                            tmp=tmp + "║ Risorsa "+res.abbreviation()+ "        ║\n";
                            tmp=tmp + "║ Numero richiesto "+requiredresources.get(res)+"║\n";
                        }

                        tmp = tmp+"║ Abilità           ║\n";

                        switch (type) {
                            case "DiscountAbility":
                                tmp = tmp + "║ Sconto di 1 "+r.abbreviation()+"    ║\n";
                                break;
                            case "ExtraSlotAbility":
                                tmp = tmp + "║ Deposito di "+capacity+" "+r.abbreviation()+"  ║\n";
                                tmp = tmp + "║ Sono presenti "+sr+" "+r.abbreviation()+"║\n";
                                break;
                            case "ProductionPowerAbility":
                                tmp = tmp + "║ Produzione        ║\n";
                                tmp = tmp + "║ 1 punto fede e "+r.abbreviation()+" ║\n";
                                break;
                            case "WhiteMarbleAbility":
                                tmp = tmp + "║ Conversione bilia ║\n";
                                tmp = tmp + "║ " + r.abbreviation() + "                ║\n";
                                break;
                        }
                        tmp = tmp + "╚═══════════════════╝";
                        return tmp;
                    }
                }
            }
        }
        return "";
    }

    /**
     * Method used to check which ability type a leader card has.
     * @param r resource which is part of the leader card ability, used to identify it
     * @param vp victory points given by the card, used to identify it
     * @return a String which represent the ability type of the leader card
     */
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
