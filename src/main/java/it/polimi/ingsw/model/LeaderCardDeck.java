package it.polimi.ingsw.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class LeaderCardDeck {
    private Stack<LeaderCard> deck;

    public LeaderCardDeck(){
        deck = new Stack<>();
        try{
            ArrayList<LeaderCard> tmp = new ArrayList<>();

            //Instantiating objects for the XML file parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            /*Getting the XML file via path. For future updates: pathname is a String, it could be used another String
              given to the Constructor, eventually resorting to the default one
              Especially useful for parameter editor*/
            Document document = builder.parse(new File("src\\xml_src\\leaderCards.xml"));
            document.getDocumentElement().normalize();

            /* Only used for testing to identify if the DOM parser worked as intended
            Element root = document.getDocumentElement(); */

            NodeList leadercards = document.getElementsByTagName("leadercard");
            //Iterating on the nodelist previously got, single node for every LCCard
            for (int i=0; i<leadercards.getLength(); i++) {
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
                    Ability.AbilityType abilitytype = Ability.AbilityType.valueOf(leadercardElement.getElementsByTagName("abilitytype").item(0).getTextContent());
                    //Getting the resource type
                    Resources restype = Resources.valueOf(leadercardElement.getElementsByTagName("restype").item(0).getTextContent());
                    //Getting the initial capacity
                    int capacity = Integer.parseInt(leadercardElement.getElementsByTagName("capacity").item(0).getTextContent());

                    //Generating the HashMap from costString
                    HashMap<Colours,Pair<Integer,Integer>> requiredcolours = new HashMap<>();
                    if(!requiredcoloursString[0].equals("NULL")){
                        for(int j=0; j< requiredcoloursString.length; j=j+3) {
                            requiredcolours.put(Colours.valueOf(requiredcoloursString[j]),new Pair<>(Integer.parseInt(requiredcoloursString[j+1]),Integer.parseInt(requiredcoloursString[j+2])));
                        }
                    }


                    //Generating the HashMap from requiredresourcesString
                    HashMap<Resources,Integer> requiredresources = new HashMap<>();
                    if(!requiredresourcesString[0].equals("NULL")) {
                        for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                            requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                        }
                    }

                    //Generating a new LCCard with the previously calculated attributes
                    LeaderCard LC = new LeaderCard(victorypoints,requiredcolours,requiredresources,new Ability(abilitytype,restype,capacity));
                    tmp.add(LC);
                }
            }

            for(int i=0;i<16;i++){
                int index;
                index = (int) (Math.random() * (16-i));
                deck.push(tmp.remove(index));
            }
        }
        //For future updates: if a path should be specified, exceptions must be handled differently
        catch(Exception e) {e.printStackTrace();}
    }

    //costruttore per mettere le carte in ordine (testing)
    public LeaderCardDeck(int Arandom){
        deck = new Stack<>();
        try{
            ArrayList<LeaderCard> tmp = new ArrayList<>();

            //Instantiating objects for the XML file parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            /*Getting the XML file via path. For future updates: pathname is a String, it could be used another String
              given to the Constructor, eventually resorting to the default one
              Especially useful for parameter editor*/
            Document document = builder.parse(new File("src\\xml_src\\leaderCards.xml"));
            document.getDocumentElement().normalize();

            /* Only used for testing to identify if the DOM parser worked as intended
            Element root = document.getDocumentElement(); */

            NodeList leadercards = document.getElementsByTagName("leadercard");
            //Iterating on the nodelist previously got, single node for every LCCard
            for (int i=0; i<leadercards.getLength(); i++) {
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
                    Ability.AbilityType abilitytype = Ability.AbilityType.valueOf(leadercardElement.getElementsByTagName("abilitytype").item(0).getTextContent());
                    //Getting the resource type
                    Resources restype = Resources.valueOf(leadercardElement.getElementsByTagName("restype").item(0).getTextContent());
                    //Getting the initial capacity
                    int capacity = Integer.parseInt(leadercardElement.getElementsByTagName("capacity").item(0).getTextContent());

                    //Generating the HashMap from costString
                    HashMap<Colours,Pair<Integer,Integer>> requiredcolours = new HashMap<>();
                    if(!requiredcoloursString[0].equals("NULL")){
                        for(int j=0; j< requiredcoloursString.length; j=j+3) {
                            requiredcolours.put(Colours.valueOf(requiredcoloursString[j]),new Pair<>(Integer.parseInt(requiredcoloursString[j+1]),Integer.parseInt(requiredcoloursString[j+2])));
                        }
                    }


                    //Generating the HashMap from requiredresourcesString
                    HashMap<Resources,Integer> requiredresources = new HashMap<>();
                    if(!requiredresourcesString[0].equals("NULL")) {
                        for (int j = 0; j < requiredresourcesString.length; j = j + 2) {
                            requiredresources.put(Resources.valueOf(requiredresourcesString[j]), Integer.parseInt(requiredresourcesString[j + 1]));
                        }
                    }

                    //Generating a new LCCard with the previously calculated attributes
                    LeaderCard LC = new LeaderCard(victorypoints,requiredcolours,requiredresources,new Ability(abilitytype,restype,capacity));
                    tmp.add(LC);
                }
            }

            for(int i=0;i<16;i++){
                deck.push(tmp.remove(0));
            }
        }
        //For future updates: if a path should be specified, exceptions must be handled differently
        catch(Exception e) {e.printStackTrace();}
    }

    private LeaderCard getFirstCard(){
        return deck.pop();
    }

    public ArrayList<LeaderCard> getLeaderCards(){
        ArrayList<LeaderCard> tmp = new ArrayList<>();
        for(int i=0;i<4;i++){
            tmp.add(getFirstCard());
        }
        return tmp;
    }
}
