package it.polimi.ingsw.model;
import it.polimi.ingsw.Game;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;


public class DevelopmentCardMarket {
    private final Map<Pair<Game.Colours,Integer>,Stack<DevelopmentCard>> cardMarket;

    public DevelopmentCardMarket(){
        //Instantiating one stack for every card deck (One for every combination of Color-Level)
        cardMarket = new HashMap<>();
        cardMarket.put(new Pair<>(Game.Colours.Blue,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Blue,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Blue,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Green,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Green,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Green,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,3),new Stack<>());

        //XML file Parsing
        try{
            ArrayList<DevelopmentCard> tmp = new ArrayList<>();

            //Instantiating objects for the XML file parsing
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            /*Getting the XML file via path. For future updates: pathname is a String, it could be used another String
              given to the Constructor, eventually resorting to the default one
              Especially useful for parameter editor*/
            Document document = builder.parse(new File("Model\\src\\xml_src\\developmentCards.xml"));
            document.getDocumentElement().normalize();

            /* Only used for testing to identify if the DOM parser worked as intended
            Element root = document.getDocumentElement(); */

            NodeList developmentcards = document.getElementsByTagName("developmentcard");
            //Iterating on the nodelist previously got, single node for every DCCard
            for (int i=0; i<developmentcards.getLength(); i++) {
                Node developmentcardnode = developmentcards.item(i);
                if (developmentcardnode.getNodeType() == Node.ELEMENT_NODE) {
                    Element developmentcardElement = (Element) developmentcardnode;
                    int level = Integer.parseInt(developmentcardElement.getElementsByTagName("level").item(0).getTextContent());
                    Game.Colours colour = Game.Colours.valueOf(developmentcardElement.getElementsByTagName("colour").item(0).getTextContent());
                    int victorypoints = Integer.parseInt(developmentcardElement.getElementsByTagName("victorypoints").item(0).getTextContent());

                    //Getting the cost String, splitting it into multiple strings
                    String[] costString = (developmentcardElement.getElementsByTagName("cost").item(0).getTextContent()).split("-");
                    //Getting the requiredresources String, splitting it into multiple strings
                    String[] requiredresourcesString = (developmentcardElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");
                    //Getting the producedresources String, splitting it into multiple strings
                    String[] producedresourcesString = (developmentcardElement.getElementsByTagName("producedresources").item(0).getTextContent()).split("-");

                    int producedfaith = Integer.parseInt(developmentcardElement.getElementsByTagName("producedfaith").item(0).getTextContent());

                    //Generating the HashMap from costString
                    HashMap<Game.Resources,Integer> cost = new HashMap<>();
                    for(int j=0; j< costString.length; j=j+2) {
                        cost.put(Game.Resources.valueOf(costString[j]),Integer.parseInt(costString[j+1]));
                    }

                    //Generating the HashMap from requiredresourcesString
                    HashMap<Game.Resources,Integer> requiredresources = new HashMap<>();
                    for(int j=0; j< requiredresourcesString.length; j=j+2) {
                        requiredresources.put(Game.Resources.valueOf(requiredresourcesString[j]),Integer.parseInt(requiredresourcesString[j+1]));
                    }

                    //Generating the HashMap from producedresourcesString
                    HashMap<Game.Resources,Integer> producedresources = new HashMap<>();
                    if(!(producedresourcesString[0].equals("NULL"))){
                        for(int j=0; j<producedresourcesString.length; j=j+2) {
                            producedresources.put(Game.Resources.valueOf(producedresourcesString[j]),Integer.parseInt(producedresourcesString[j+1]));
                        }
                    }

                    //Generating a new DCCard with the previously calculated attributes
                    DevelopmentCard DC = new DevelopmentCard(level,colour,victorypoints,cost,requiredresources,producedresources,producedfaith);
                    tmp.add(DC);
                }
            }

            //Getting the 48 cards from the arrayList tmp, inserting them into the cardMarket in a random order for every stack
            for(int i=0;i<48;i++){
                int index;
                index = (int) (Math.random() * (48-i));
                cardMarket.get(new Pair<>(tmp.get(index).getColour(),tmp.get(index).getLevel())).push(tmp.remove(index));
            }
        }
        //For future updates: if a path should be specified, exceptions must be handled differently
        catch(Exception e) {e.printStackTrace();}
    }

    public DevelopmentCard peekFirstCard(Game.Colours colour, int level) throws IllegalArgumentException{
        Pair<Game.Colours,Integer> tmp1 = new Pair<>(colour,level);
        if(cardMarket.get(tmp1).empty()) throw new IllegalArgumentException("There's no card on the stack");
        return cardMarket.get(tmp1).peek();
    }
    public DevelopmentCard getFirstCard(Game.Colours colour, int level) throws IllegalArgumentException{
        Pair<Game.Colours,Integer> tmp1 = new Pair<>(colour,level);
        if(cardMarket.get(tmp1).empty()) throw new IllegalArgumentException("There's no card on the stack");
        return cardMarket.get(tmp1).pop();
    }
}
