package poker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class RoundOfPoker {

    private int currentBet;
    private ArrayList<PokerPlayer> players;

    public RoundOfPoker(ArrayList<PokerPlayer> players, DeckOfCards deck)
    {
        this.players = players;
        this.currentBet = 0;
    }

    public void play(GameOfPoker game)
    {

        // START ROUND
        game.updateGameMessage("New Deal:");

        for (PokerPlayer player : this.players)
            game.updateGameMessage("> " + player.getName() + " has " + player.getCoinsBalance() + " coins in the bank");

        // CHECK IF ANY PLAYER CAN OPEN
        boolean canOpen = false;
        for (PokerPlayer player : this.players)
            if (player.canOpenBet()) {
                game.updateGameMessage("> " + player.getName() + " says: I can open");
                canOpen = true;
            }
            else
                game.updateGameMessage("> " + player.getName() + " says: I cannot open");

        if (!canOpen) {
            game.updateGameMessage("Sorry, we cannot open the game.");
            return;
        }

        game.updateGameMessage("You have been dealt the following hand:");
        // PRINT THE TYPE OF HAND THAT HUMAN PLAYER OWNS
        for (PokerPlayer player : this.players)
            if (player.isHuman())
                game.updateGameMessage(player.getHand());

        // DISCARD
        for (PokerPlayer player : this.players) {
            if (player.isHuman()) {
                game.updateGameMessage(">> Which card(s) would you like to discard (e.g., 1,3): ");
                player.askDiscard();
            }
        }

        // PRINT THE TYPE OF HAND THAT HUMAN PLAYER OWNS
        for (PokerPlayer player : this.players)
            if (player.isHuman()) {
                game.updateGameMessage("Your hand now looks like:");
                game.updateGameMessage(player.getHand());
            }

        // ASK TO FOLD
        boolean[] fold = new boolean[this.players.size()];
        boolean allFold = false;
        for (int i = 0; i < players.size(); i++) {

            if (players.get(i).isHuman()) {
                game.updateGameMessage(">> Would you like to fold (y/n)? ");
            }
            fold[i] = players.get(i).askFold(this.currentBet);
            if(!fold[i])
                allFold = true;
        }
        // SHOW DISCARDING STATS
        for (PokerPlayer player : this.players)
            if (!player.isHuman())
                game.updateGameMessage(player.getName() + " discards " + player.discard() + " card(s)");

        System.out.println("");

        // IF EVERY PLAYER FOLD THEN EXIT ROUND OF POKER
        if (!allFold) {
            game.updateGameMessage("sorry, all players fold in the round.");
            return;
        }


        // BETTING
        int checkOpen = 0, roundCounter = 0, currentPot = 0;
        boolean round = true, openingBetting = true, human = true, firstOpen = false;

        while (round) {
            for (int i = 0; i < this.players.size(); i++) {
                // CHECK FOR PLAYERS THAT DIDN'T FOLD
                if (!fold[i]) {
                    // IF THIS IS THE FIRST TIME OPENING THE BET
                    if(openingBetting) {
                        // CHECK IF THE PLAYER IS A HUMAN AND OPEN THE BET
                        if(players.get(i).isHuman()){
                            // CHECK IF THE HUMAN PLAYER IS THE FIRST PLAYER TO OPEN
                            if (players.get(i).canOpenBet() && !fold[i] && human && checkOpen == 0) {
                                game.updateGameMessage("Would you like to open bet (y/n)? ");
                               firstOpen = players.get(i).askOpenBet(this.currentBet);
                               if(firstOpen){
                                   this.currentBet = 1;
                                   players.get(i).updateCoinsBalance(-this.currentBet);
                                   players.get(i).updateTableCoins(this.currentBet);
                                   currentPot += this.currentBet;
                                   human = false;
                               }
                            }
                        }
                        // CHECK IF THE COMPUTER PLAYER IS THE FIRST PLAYER TO OPEN
                        else if(players.get(i).canOpenBet() && !players.get(i).isHuman()){
                            firstOpen = players.get(i).askOpenBet(this.currentBet);
                            if(firstOpen){
                                this.currentBet = 1;
                                players.get(i).updateCoinsBalance(-this.currentBet);
                                players.get(i).updateTableCoins(this.currentBet);
                            }
                        }
                    }

                    // CHECK IF THIS IS THE FIRST TIME OF OPENING AND PRINT THE OPENING STATEMENT
                    if (checkOpen == 0 && firstOpen) {
                        game.updateGameMessage(players.get(i).getName() + " says: I open with " + this.currentBet + " chip!");
                        currentPot += this.currentBet;
                        checkOpen = 1;
                    }
                    // CHECK IF THE PLAYERS HAS ALREADY OPEN
                    else if (checkOpen > 0 ) {

                        // CHECK IF THE PLAYER IS A HUMAN AND ASK THE PLAYER TO RAISE THE BET
                        if (players.get(i).isHuman() && !fold[i]) {
                            if(checkActive(fold) == 1)
                                break;

                            printSeenStatement(currentPot, i, game);

                            game.updateGameMessage("Would you like to raise (y/n)? ");
                            boolean checkHuman = players.get(i).askRaiseBet(this.currentBet);

                            // IF THE PLAYER SAID YES THEN RAISE BET
                            if (checkHuman) {
                                players.get(i).updateCoinsBalance(-this.currentBet);
                                players.get(i).updateTableCoins(this.currentBet);
                                printRaiseStatement(i, this.currentBet, game);
                                currentPot += this.currentBet;
                            }
                            // CHECK IF THE PLAYER DIDN'T RAISE THE BET AND THE BETTING ISN'T THE OPENING BET THEN FOLD
                            else if (!checkHuman ) {
                               //System.out.println("Would you like to fold (y/n)? ");
                                fold[i] = true;
                                game.updateGameMessage(players.get(i).getName() + " says: I fold ");
                            }
                        }
                        // CHECK IF THE PLAYER IS A COMPUTER PLAYER AND ASK THE PLAYER TO RAISE THE BET
                        else {
                            boolean checkComputer = players.get(i).askRaiseBet(this.currentBet);
                            // IF THE PLAYER COIN BALANCE IS ZERO REMOVE THE PLAYER FROM THE GAME
                            if(checkActive(fold) == 1)
                                break;;

                            // IF THE PLAYER SAID YES THEN RAISE BET
                            if (checkComputer ) {
                                players.get(i).updateCoinsBalance(-this.currentBet);
                                players.get(i).updateTableCoins(this.currentBet);
                                if(checkActive(fold) == 1)
                                    break;

                                printSeenStatement(currentPot, i, game);
                                printRaiseStatement(i, this.currentBet, game);
                                currentPot += this.currentBet;
                            }
                            // CHECK IF THE PLAYER DIDN'T RAISE THE BET AND THE BETTING ISN'T THE OPENING BET THEN FOLD
                            else if (!checkComputer) {
                                fold[i] = true;
                                game.updateGameMessage(players.get(i).getName() + " says: I  fold ");
                            }
                        }
                    }
                }
            }
            System.out.println("");
            // UPDATE VARIABLES VALUES
            openingBetting = false;
            roundCounter++;


            // CHECK IF THE GAME ROUND OF POKER IS FINNISH AND ANNOUNCE WINNER AND RESET THE POT
            if(roundCounter == 2){
                winner(fold, currentPot, game);
                currentPot = 0;
                resetPlayerPot();
                round = false;
            }
            //CHECK IF THERe'S ONLY ONE PLAYER LEFT IN THE GAME AND ANNOUNCE WINNER AND RESET THE POT
            else if(checkActive(fold) == 1){
                winner(fold, currentPot, game);
                currentPot = 0;
                resetPlayerPot();
                round = false;
            }

        }

    }

    // A METHOD THAT CHECKS WHICH PLAYER IS THE WINNER AND DISPLAY PLAYERS HAND
    public void winner(boolean fold[], int currentPot, GameOfPoker game) {
        // CHECK FOR WINNER
        int winnings = currentPot;
        int winnerPos = 0, cardGameValue = 0;
        for (int i = 0; i < players.size(); i++) {
            if (i ==  0 ) {
               // players.get(i).updateTableCoins(-this.currentBet);
                game.updateGameMessage(players.get(i).getName() + " goes first");
                game.updateGameMessage(players.get(i).getHand());
                if(players.get(i).getHandValue() > cardGameValue && !fold[i]) {
                    cardGameValue = players.get(i).getHandValue();
                    winnerPos = i;
                }
            }
            else {
                if(players.get(i).getHandValue() > cardGameValue && !fold[i]){
                   // players.get(i).updateTableCoins(-this.currentBet);
                    game.updateGameMessage(players.get(i).getName() + " says 'read them and weep'");
                    game.updateGameMessage(players.get(i).getHand());
                    cardGameValue = players.get(i).getHandValue();
                    winnerPos = i;
                }
                else{
                    //players.get(i).updateTableCoins(-this.currentBet);
                    game.updateGameMessage(players.get(i).getName() + " says 'read them and weep'");
                    game.updateGameMessage(players.get(i).getHand());
                }

            }
        }

        // PRINT WINNER
        if(winnings > 0) {
            players.get(winnerPos).updateCoinsBalance(winnings);
            game.updateGameMessage(players.get(winnerPos).getName() + " say: I WIN  " + winnings + " chip");
            game.updateGameMessage(players.get(winnerPos).getHand());
            game.updateGameMessage(players.get(winnerPos).getName() + " has " +
                    players.get(winnerPos).getCoinsBalance()  + " chip(s) in the bank");
        }
        else
            game.updateGameMessage("No winner because none of the players can open the bet");

    }

    // RESET THE CURRENT POT
    public void resetPlayerPot(){
        for(PokerPlayer player : players){
            player.updateTableCoins(-(player.updatePlayerPot()));
        }
    }

    // A METHOD THAT PRINT SEE STATEMENT IN THE GAME
    public void printSeenStatement(int currentPot, int i, GameOfPoker game){
        game.updateGameMessage(players.get(i).getName() + " says: I see that " + currentPot + " chip!");
    }

    // A METHOD THAT PRINT THE RAISE STATEMENT IN THE GAME
    public void printRaiseStatement(int i, int current, GameOfPoker game){
        game.updateGameMessage(players.get(i).getName() + " says: I raise " + current + " chip!");
    }

    // CHECK THE NUMBER OF PLAYER STILL IN THE GAME
    public int checkActive(boolean fold[]){
        int checkActivePlayer = 0;
        for(int k = 0; k < players.size(); k++){
            if(!fold[k]){
                checkActivePlayer++;
            }
        }
        return  checkActivePlayer;
    }


    public static void main(String[] args) {
       /* DeckOfCards deck = new DeckOfCards();

        Scanner input = new Scanner(System.in);



        System.out.println("Welcome to the Automated Poker Machine ...");
        System.out.print("What is your name? ");
        String name = input.nextLine();
        System.out.println("Let's play POKER ...");

        // MAKE HUMAN PLAYER, PASS A NAME
        HumanPlayer humanPlayer = new HumanPlayer(deck);
        ComputerPlayer p1 = new ComputerPlayer(deck);
        ComputerPlayer p2 = new ComputerPlayer(deck);
        ComputerPlayer p3 = new ComputerPlayer(deck);
        ComputerPlayer p4 = new ComputerPlayer(deck);
        ArrayList<PokerPlayer> players = new ArrayList<PokerPlayer>();
        players.add(humanPlayer);
        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
       // Collections.shuffle(players);
        RoundOfPoker round = new RoundOfPoker(players, deck);

        boolean poker = true;
        while(poker && players.contains(humanPlayer)){
            round.play();
            System.out.println("Would like to play another round of poker (y/n)");
            Scanner in = new Scanner(System.in);
            String response = in.nextLine();

            for(int i = 0; i < players.size(); i++){
                if(players.get(i).getCoinsBalance() == 0)
                    players.remove(i);
            }
            deck.reset();
            for(PokerPlayer player : players)
              player.resetHand();

            if(response.equalsIgnoreCase("n"))
                poker = false;
        }*/

        //System.out.println(round.players.get(0).name);

        String message = "Hello bit2_poker Let's play POKER ...\n" +
                "New Deal:\n" +
                "> Aurelia has 10 coins in the bank\n" +
                "> Cassie has 10 coins in the bank\n" +
                "> Horace has 10 coins in the bank\n" +
                "> Elouise has 10 coins in the bank\n" +
                "> Normand has 10 coins in the bank\n" +
                "> bit2_poker has 10 coins in the bank\n" +
                "> Aurelia says: I cannot open\n" +
                "> Cassie says: I cannot open\n" +
                "> Horace says: I can open\n" +
                "> Elouise says: I cannot open\n" +
                "> Normand says: I cannot open\n" +
                "> bit2_poker says: I can open\n" +
                "You have been dealt the following hand:\n" +
                "0: 7H\n" +
                "1: 7S\n" +
                "2: 6S\n" +
                "3: 3S\n" +
                "4: 2C\n" +
                "\n" +
                ">> Which card(s) would you like to discard (e.g., 1,3): ";
        /*if (message.length() > 140) {
            System.out.println(message.substring(0, 130));
            System.out.println(message = message.substring(130));
        }*/
        System.out.println(message.substring(140));
    }
}

