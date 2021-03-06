package poker;

import java.util.Scanner;

public class Parser {

    public int convertResponse(String response){
        String checkYes = "Yes";
        String checkY = "Y";
        String checkNo =  "No";
        String checkN = "N";

        if(checkYes.equalsIgnoreCase(response) || checkY.equalsIgnoreCase(response) ){
            return  1;
        }
        else if(checkNo.equalsIgnoreCase(response) || checkN.equalsIgnoreCase(response)){
            return 0;
        }
        else
        	return -1;
    }
    
    public boolean checkAmountDiscards(String response) {
    	String [] strArray = response.split("\\s*(\\s|=>|,)\\s*");
        
    	if(strArray.length > 3)
    		return false;
    	else 
    		return true;
    }
    
    public boolean checkDiscardNumbers(String response) {
    	String [] strArray = response.split("\\s*(\\s|=>|,)\\s*");
    	
    	for(int i = 0; i < strArray.length; i++) {
    		if(strArray[i].length() > 1)
    			return false;
    		else if(strArray[i].equals(null) || strArray[i].equals("")) 
    			return false;	
    		else if(strArray[i].charAt(0) < 48 || strArray[i].charAt(0) > 52)
				return false;
		}
    	
    	return true;
    }
    
    public int[] convertDiscards(String response){
        String [] strArray = response.split("\\s*(\\s|=>|,)\\s*");
        int discard[] = new int[PokerPlayer.DISCARD_MAX];

        for (int i = 0; i < discard.length; i++){

            if(i < strArray.length)
                discard[i] = Integer.parseInt(strArray[i]);
            else
                discard[i] = -1;

        }
        return discard;
    }

    // CHECK IF PLAYER PLACE IN THE CORRECT VALUE OF THE BET
    public boolean bettingAmount(String bet){
        if (bet.matches("[0-9]+"))
            return true;
        else
            return false;
    }
    
    public boolean checkDealMeOut(String response) {
    	if (response.matches(".*#bit2_poker_DealMeOut.*"))
    		return true;
    	else 
    		return false;
    }


    public static void main(String[] args){
        Parser parser = new Parser();
        Scanner input = new Scanner(System.in);

        System.out.println("Enter discard answer Y/N");
        String response = input.nextLine();
        System.out.println(parser.convertResponse(response));

        System.out.println("Enter discard position");
        String discard = input.nextLine();
        int discards[] = parser.convertDiscards(discard);
        for(int i = 0; i < discards.length; i++)
            System.out.println(discards[i]);
        
        
        System.out.println("Test regex");
        System.out.println("big 685/-0'/#bit2_poker_DealMeOut 579**-*'#[ banana".matches(".*#bit2_poker_DealMeOut.*"));





    }
}
