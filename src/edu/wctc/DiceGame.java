package edu.wctc;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiceGame {
    //Fields
    private	final List<Player> players;
    private	final List<Die> dice;
    private	final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) {
        //Constructor that initializes all final instance fields.
        players = new ArrayList<>();
        dice = new ArrayList<>();
        this.maxRolls = maxRolls;

        //Creates the required number of Player objects and Die objects and adds them to the appropriate lists.
        if(countPlayers < 2){
            //If the number of players is less than 2, throws an IllegalArgumentException.
            throw new IllegalArgumentException();
        }else { //if num of players is not less then 2 then...
            for(int i = 0; i < countPlayers; i++){
                players.add(new Player()); //add new player
            }
            for (int i = 0; i < countDice; i++){
                dice.add(new Die(6)); //add new die
            }
        }
    }
    private boolean allDiceHeld(){
        //Returns true if all dice are held, false otherwise.
        return dice.stream().allMatch(Die::isBeingHeld);
    }

    public boolean autoHold(int faceValue){

        //Optional object that may or may not hold a null value
        //Holds held die
        Optional<Die> held = dice.stream()
                .filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld())
                .findFirst();
        //Holds un-held die
        Optional<Die> unheld = dice.stream()
                .filter(die -> die.getFaceValue() == faceValue && !(die.isBeingHeld()))
                .findFirst();

        //if statement to loop through die and if they are held or not
        if(held.isPresent()){
            //If there already is a die with the given face value that is held, just return true.
            return true;
        }else if(unheld.isPresent()){
            //If there is a die with the given face value that is un-held,
            //hold it and return true. (If there are multiple matches, only hold one of them.)
            unheld.get().holdDie();
            return true;
        }else{
            //If there is no die with the given face value, return false.
            return false;
        }
    }

    public boolean currentPlayerCanRoll(){
        //Returns true if the current player has any rolls remaining and if not all dice are held.
        if(currentPlayer.getRollsUsed() == maxRolls || allDiceHeld()){
            //if the current player is at max rolls OR all dice are held return false
            return false;
        }else {
            return true;
        }
    }

    public int getCurrentPlayerNumber(){
        //Returns the player number of the current player.
        return currentPlayer.getPlayerNumber();
    }

    //Why is the one above getPlayerNumber but the one below is getScore?
    public int getCurrentPlayerScore(){
        return currentPlayer.getScore();
    }

    public String getDiceResults(){
        //Resets a string composed by concatenating each Die's toString.
        return dice.stream().map(Die::toString).collect(Collectors.joining());
    }

    public String getFinalWinner(){
        //Finds the player with the most wins and returns its toString.
        return players.stream().max(Comparator.comparingInt(Player::getScore)).toString();
    }

    public String getGameResults(){
        //Sorts the player list field by score, highest to lowest.
        players.sort(Comparator.comparingInt(Player::getScore).reversed());
        //sorting players by comparing the player and their score, then reversing it
        int playerScore =  players.stream().mapToInt(Player::getScore).max().orElse(0);

        if (playerScore != 0){
            //Awards each player that earned the highest score a win and all others a loss.
            for(Player player : players){//initializes player for each of the players in the collection
                if (player.getScore() == playerScore){
                    player.addWin(); //highest score = win
                }else{
                    player.addLoss();
                }
            }
        }

        //Returns a string composed by concatenating each Player's toString.
        return players.stream().map(Player::toString).collect(Collectors.joining());
    }

    private boolean isHoldingDie(int faceValue){
        //Returns true if there is any held die with a matching face value, false otherwise.
        return dice.stream().anyMatch(die -> die.isBeingHeld() && faceValue == die.getFaceValue());
    }

    public boolean nextPlayer(){
        //If there are more players in the list after the current player,
        //updates currentPlayer to be the next player and returns true.
        //Otherwise, returns false.

        //Taking play size and subtracting 1 to get next player
        Player playerSize = players.get(players.size() - 1);
        //Creating ID to grab and label next player(s)
        int id = 0; //initializing the id to start at 0

        //Use playerSize to make it shorter, I could have put the stuff playerSize is equal to but it's long
        if(currentPlayer == playerSize)
        {
            return false;
        }else{ //if there are more players in the list do this
            //iterating over players
            for(Player player : players){
                //if statement to add to the player id for s new player
                if(currentPlayer == player){
                    currentPlayer = players.get(id + 1); //adding id to player and making id = 1
                    return true;
                }
                id++; //incrementing id
            }
        }
        return false;
    }

    public void playerHold(char dieNum){
        //Finds the die with the given die number (NOT the face value) and holds it.
        dice.stream().filter(die -> die.getDieNum() == dieNum).findFirst().ifPresent(Die::holdDie);
    }

    public void resetDice(){
        //reset die
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers(){
        //reset player
        players.forEach(Player::resetPlayer);
    }

    public void rollDice(){
        //Logs the roll for the current player, then rolls each die.
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        int score = 0; //initializing the score so it starts at 0

        //If there is currently a ship (6), captain (5), and crew (4) die held,
        if (isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4)){
            for (Die die : dice) {
                //adds the points of the die
                score += die.getFaceValue();
            }
            score -= 15; //subtracting the 6, 5, & 4 from the score since they don't count as points
            currentPlayer.setScore(currentPlayer.getScore() + score);
            //getting the current players score which is their currentScore + newly added score
        }else {
            //If there is not a 6, 5, and 4 held, assigns no points.
            System.out.println("No points assigned");
        }
    }

    public void startNewGame(){
        //Assigns the first player in the list as the current player.
        //(The list will still be sorted by score from the previous round,
        //so winner will end up going first.)
        //Resets all players.
        currentPlayer = players.get(0);
        //returns player 0
        resetPlayers();
        //resets player

    }
}

/*
Documentation
.allMatch()
https://www.geeksforgeeks.org/stream-allmatch-java-examples/

Collectors.joining
https://www.geeksforgeeks.org/java-8-streams-collectors-joining-method-with-examples/

.comparingInt
https://www.geeksforgeeks.org/comparator-comparingint-in-java-with-examples/

.reverse
https://www.geeksforgeeks.org/reverse-a-string-in-java/

forEach and :
https://blog.gitnux.com/code/java-colon/
https://www.youtube.com/watch?v=vrq6oR5Og8Y

.roll()
https://www.tutorialspoint.com/java/util/calendar_roll.htm#:~:text=The%20java.util.Calendar.roll%20%28%29%20method%20adds%20%28up%29%20or%20subtracts,the%20given%20time%20field%20without%20changing%20larger%20fields.

Optional Objects
https://www.baeldung.com/java-optional
https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html

*/


