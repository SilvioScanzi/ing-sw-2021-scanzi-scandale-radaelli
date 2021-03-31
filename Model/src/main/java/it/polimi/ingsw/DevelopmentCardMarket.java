package it.polimi.ingsw;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.util.*;


public class DevelopmentCardMarket {
    private Map<Pair<Game.Colours,Integer>,Stack<DevelopmentCard>> cardMarket;

    public DevelopmentCardMarket(){
        cardMarket = new HashMap<Pair<Game.Colours,Integer>,Stack<DevelopmentCard>>();
        cardMarket.put(new Pair<>(Game.Colours.Green,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Green,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Green,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Yellow,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Blue,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Blue,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Blue,3),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,1),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,2),new Stack<>());
        cardMarket.put(new Pair<>(Game.Colours.Purple,3),new Stack<>());
        try{
            ArrayList<DevelopmentCard> tmp = new ArrayList<>();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("Model\\src\\xml_src\\developmentCards.xml"));
            document.getDocumentElement().normalize();
            Element root = document.getDocumentElement();
            NodeList developmentcardList = document.getElementsByTagName("developmentcard");

            for (int i=0; i<developmentcardList.getLength(); i++) {
                Node DCnode = developmentcardList.item(i);
                if (DCnode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) DCnode;
                    int level = Integer.parseInt(eElement.getElementsByTagName("level").item(0).getTextContent());
                    Game.Colours colour = Game.Colours.valueOf(eElement.getElementsByTagName("colour").item(0).getTextContent());
                    int victorypoints = Integer.parseInt(eElement.getElementsByTagName("victorypoints").item(0).getTextContent());

                    //Getting the cost String, splitting it into multiple strings
                    String[] costString = (eElement.getElementsByTagName("cost").item(0).getTextContent()).split("-");
                    //Getting the requiredresources String, splitting it into multiple strings
                    String[] requiredresourcesString = (eElement.getElementsByTagName("requiredresources").item(0).getTextContent()).split("-");
                    //Getting the producedresources String, splitting it into multiple strings
                    String[] producedresourcesString = (eElement.getElementsByTagName("producedresources").item(0).getTextContent()).split("-");

                    int producedfaith = Integer.parseInt(eElement.getElementsByTagName("producedfaith").item(0).getTextContent());

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

                    DevelopmentCard DC = new DevelopmentCard(level,colour,victorypoints,cost,requiredresources,producedresources,producedfaith);
                    tmp.add(DC);
                }
            }

            // For testing: until every card is implemented, substitute 48 with the actual number of cards implemented
            for(int i=0;i<48;i++){
                int index;
                index = (int) (Math.random() * (48-i));
                cardMarket.get(new Pair<>(tmp.get(index).getColour(),tmp.get(index).getLevel())).push(tmp.remove(index));
            }
        }
        catch(Exception e) {e.printStackTrace();}




        /*ArrayList<DevelopmentCard> tmp = new ArrayList<>();
        Pair<Game.Colours,Integer> tmp1 = new Pair<>(Game.Colours.Green,1);

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }

        tmp1 = new Pair<> (Game.Colours.Green,2);
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));
        tmp.add(new DevelopmentCard(1, Game.Colours.Green,1,
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Shields,2);}},
                new HashMap<Game.Resources,Integer>() {{put(Game.Resources.Coins,1);}},
                new HashMap<Game.Resources,Integer>(),
                1));

        cardMarket.put(tmp1,new Stack<DevelopmentCard>());
        for(int i=0; i<4; i++){
            int index;
            index = (int) (Math.random() * (3-i));
            cardMarket.get(tmp1).push(tmp.remove(index));
        }*/
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
