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
import java.util.ArrayList;
import java.util.HashMap;

public class DevelopmentCardParser {
    private final Document document;

    public DevelopmentCardParser(){
        InputStream resource = getClass().getClassLoader().getResourceAsStream("xml/developmentCards.xml");
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
     * Method used to parse development cards from the relative xml file. To do this, it is used
     * a document to extract the information from.
     * @return ArrayList of development cards from the xml file
     */
    public ArrayList<DevelopmentCard> parseFromXML(){
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

    /**
     * Method used for the CLI to print the current development card market. If the player is currently
     * using the colored version of the cli, color codes are also added.
     * @param CM current Market represented by a HashMap of Colour-Level, Victory points and number of cards in the stack
     * @param color boolean that indicates if the player is using the colored version of cli
     * @return a String which represent the current state of the market
     */
    public String findMarketByID(HashMap<Pair<Colours, Integer>, Pair<Integer,Integer>> CM, boolean color){
        NodeList developmentcards = document.getElementsByTagName("developmentcard");
        //Iterating on the nodelist previously got, single node for every DCCard
        ArrayList<Integer> lvl = new ArrayList<>();
        ArrayList<Integer> fth = new ArrayList<>();
        ArrayList<HashMap<Resources, Integer>> cst = new ArrayList<>();
        ArrayList<HashMap<Resources, Integer>> rr = new ArrayList<>();
        ArrayList<HashMap<Resources, Integer>> pr = new ArrayList<>();

        String tmp = "";
        for(Colours c : Colours.values()) {
            for (int l = 1; l <= 3; l++) {
                Pair<Colours, Integer> P = new Pair<>(c, l);
                if (!CM.containsKey(P)) {
                    lvl.add(l);
                    fth.add(-1);
                    cst.add(new HashMap<>());
                    rr.add(new HashMap<>());
                    pr.add(new HashMap<>());
                } else {
                    for (int i = 0; i < developmentcards.getLength(); i++) {
                        Node developmentcardnode = developmentcards.item(i);
                        if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                            Element developmentcardElement = (Element) developmentcardnode;
                            int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());
                            if (victorypoints == CM.get(P).getKey()) {
                                Colours colour = Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                                if (colour.equals(c)) {

                                    int level = Integer.parseInt(developmentcardElement.getElementsByTagName("level").item(0).getTextContent());
                                    lvl.add(level);

                                    //Getting the cost String, splitting it into multiple strings
                                    String[] costString = (developmentcardElement.getElementsByTagName("cost").item(0).getTextContent()).split("-");

                                    //Getting the requiredresources String, splitting it into multiple strings
                                    String[] requiredresourcesString = (developmentcardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");

                                    //Getting the producedresources String, splitting it into multiple strings
                                    String[] producedresourcesString = (developmentcardElement.getElementsByTagName("producedresources").item(0).getTextContent()).split("-");

                                    int producedfaith = Integer.parseInt(developmentcardElement.getElementsByTagName("producedfaith").item(0).getTextContent());
                                    fth.add(producedfaith);

                                    //Generating the HashMap from costString
                                    HashMap<Resources, Integer> cost = new HashMap<>();
                                    for (int j = 0; j < costString.length; j = j + 2) {
                                        cost.put(Resources.valueOf(costString[j]), Integer.parseInt(costString[j + 1]));
                                    }
                                    cst.add(cost);

                                    //Generating the HashMap from requiredresourcesString
                                    HashMap<Resources, Integer> requiredresources = new HashMap<>();
                                    for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                                        requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                                    }
                                    rr.add(requiredresources);

                                    //Generating the HashMap from producedresourcesString
                                    HashMap<Resources, Integer> producedresources = new HashMap<>();
                                    if (!(producedresourcesString[0].equals("NULL"))) {
                                        for (int j = 0; j < producedresourcesString.length; j = j + 2) {
                                            producedresources.put(Resources.valueOf(producedresourcesString[j]), Integer.parseInt(producedresourcesString[j + 1]));
                                        }
                                    }
                                    pr.add(producedresources);
                                }
                            }
                        }
                    }
                }
            }
            String asciiReset = "";
            String asciiColor = "";
            //Generating the string from the cards found previously
            if(color) {
                asciiReset = "\u001b[0m";
                asciiColor = switch (c) {
                    case Blue -> "\u001b[34m";
                    case Green -> "\u001b[32m";
                    case Purple -> "\u001b[35m";
                    case Yellow -> "\u001b[33m";
                };
            }

            String space;
            if (c.equals(Colours.Blue)) {
                space = "        ";
            } else if (c.equals(Colours.Yellow)) {
                space = "     ";
            } else {
                space = "      ";
            }

            tmp = tmp + asciiColor + "╔═══════════════════╗       ╔═══════════════════╗       ╔═══════════════════╗" + "\n";
            tmp = tmp + asciiColor + "║" + asciiReset + " LIVELLO " + lvl.get(0) + "         " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " LIVELLO " + lvl.get(1) + "         " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " LIVELLO " + lvl.get(2) + "         " + asciiColor + "║" + asciiReset + "\n";
            tmp = tmp + asciiColor + "║" + asciiReset + " COLORE " + c + space + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " COLORE " + c + space + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " COLORE " + c + space + asciiColor + "║" + asciiReset + "       " + "\n";
            for(int i=1;i<=3;i++){
                if(!CM.containsKey(new Pair<>(c,i))){
                    tmp = tmp + asciiColor + "║" + asciiReset + "   <PILA VUOTA> " + asciiColor + "   ║" + asciiReset;
                }
                else{
                    tmp = tmp + asciiColor + "║" + asciiReset + " P. VITTORIA " + CM.get(new Pair<>(c,i)).getKey();
                    if(CM.get(new Pair<>(c,i)).getKey()<10) tmp = tmp + " ";
                    tmp = tmp + asciiColor + "    ║" + asciiReset;
                }
                tmp = tmp + "       ";
            }
            tmp = tmp + "\n";
            for(int i=1;i<=3;i++){
                if(CM.containsKey(new Pair<>(c,i))){
                    tmp = tmp + asciiColor + "║" + asciiReset + " CARTE PILA " + CM.get(new Pair<>(c,i)).getValue();
                    tmp = tmp + asciiColor + "      ║" + asciiReset;
                    tmp = tmp + "       ";
                }
            }

            tmp = tmp + "\n" + asciiColor + "║" + asciiReset + " COSTO             " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " COSTO             " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " COSTO             " + asciiColor + "║" + asciiReset;

            for(Resources r : Resources.values()) {
                tmp = tmp + "\n";
                for(int i=0;i<3;i++) {
                    if(cst.get(i).containsKey(r)){
                        tmp = tmp + asciiColor + "║" + asciiReset + " Risorsa " + r.abbreviation() + " " + cst.get(i).get(r) + "      " + asciiColor + "║" + asciiReset + "       ";
                    }
                    else tmp = tmp + asciiColor + "║                   ║" + asciiReset + "       ";
                }
            }

            tmp = tmp + "\n" + asciiColor + "║" + asciiReset + " RISORSE RICHIESTE " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " RISORSE RICHIESTE " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " RISORSE RICHIESTE " + asciiColor + "║" + asciiReset;

            for(Resources r : Resources.values()) {
                tmp = tmp + "\n";
                for(int i=0;i<3;i++) {
                    if(rr.get(i).containsKey(r)){
                        tmp = tmp + asciiColor + "║" + asciiReset + " Risorsa " + r.abbreviation() + " " + rr.get(i).get(r) + "      " + asciiColor + "║" + asciiReset + "       ";
                    }
                    else tmp = tmp + asciiColor + "║                   ║" + asciiReset + "       ";
                }
            }

            tmp = tmp + "\n" + asciiColor + "║" + asciiReset + " RISORSE PRODOTTE  " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " RISORSE PRODOTTE  " + asciiColor + "║" + asciiReset + "       " +
                    asciiColor + "║" + asciiReset + " RISORSE PRODOTTE  " + asciiColor + "║" + asciiReset;

            for(Resources r : Resources.values()) {
                tmp = tmp + "\n";
                for(int i=0;i<3;i++) {
                    if(pr.get(i).containsKey(r)){
                        tmp = tmp + asciiColor + "║" + asciiReset + " Risorsa " + r.abbreviation() + " " + pr.get(i).get(r) + "      " + asciiColor + "║" + asciiReset + "       ";
                    }
                    else tmp = tmp + asciiColor + "║                   ║" + asciiReset + "       ";
                }
            }
            tmp = tmp + "\n";
            for(int i=0; i<3;i++){
                if (fth.get(i) > 0){
                    tmp = tmp + asciiColor + "║" + asciiReset + " FEDE PRODOTTA " + fth.get(i) + "   " + asciiColor + "║" + asciiReset + "       ";
                }
                else tmp = tmp + asciiColor + "║                   ║" + asciiReset + "       ";
            }
            tmp = tmp + "\n" + asciiColor + "╚═══════════════════╝" + asciiReset + "       " + asciiColor + "╚═══════════════════╝" + asciiReset + "       " + asciiColor + "╚═══════════════════╝" + asciiReset;
            tmp = tmp +"\n\n\n";

            lvl.clear();
            cst.clear();
            fth.clear();
            rr.clear();
            pr.clear();
        }
        return tmp;
    }

    /**
     * Method used for the CLI to print a single development card. If the player is currently
     * using the colored version of the cli, color codes are also added.
     * @param c colour of the card to print
     * @param vp victory points of the card to print
     * @param color boolean that indicates if the player is using the colored version of cli
     * @return a String which represent the card requested
     */
    public String findCardByID(Colours c, int vp, boolean color){
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
                        String asciiReset = "";
                        String asciiColor = "";
                        if(color) {
                            asciiReset = "\u001b[0m";
                            switch (colour) {
                                case Blue:
                                    asciiColor = "\u001b[34m";
                                    break;
                                case Green:
                                    asciiColor = "\u001b[32m";
                                    break;
                                case Purple:
                                    asciiColor = "\u001b[35m";
                                    break;
                                case Yellow:
                                    asciiColor = "\u001b[33m";
                                    break;
                                default:
                                    asciiColor = asciiReset;
                                    break;
                            }
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

}
