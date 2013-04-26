/* Initial Pass at Calculating Hand Equity 
 Equity(Hand) = Sum of All Possible Hand Rankings Givin that Hand

Depending on speed, and memory, I may make this a lookup table.

 */

import java.util.ArrayList;

public class HandEquity{
	
	public static String[] cards = {
		"As","Ks","Qs","Ts","9s","8s","7s","6s","5s","4s","3s","2s",
		"Ah","Kh","Qh","Th","9h","8h","7h","6h","5h","4h","3h","2h",
		"Ac","Kc","Qc","Tc","9c","8c","7c","6c","5c","4c","3c","2c",
		"Ad","Kd","Qd","Td","9d","8d","7d","6d","5d","4d","3d","2d",};

	public static void main(String[] args){
		String[] allHands = allPossibleHands("As Ah");

		for(String hand : allHands) System.out.println(hand);

		System.out.println(allHands.length);
	}

	//Given a hand, return the sum of all possible future hands
	public static int getEquity(String hand){
		int[] allRanks = allPossibleRanks(hand);
		int out = 0;
		for(int i : allRanks) out += i;
		return out;
	}

	//Given a hand, return a list of all possible future ranks
	public static int[] allPossibleRanks(String hand){
		String[] allHands = allPossibleHands(hand);
		int[] out = new int[allHands.length];

		for(int i=0;i<out.length;i++){
			out[i] = HandEvaluator.rankHand(new Hand(allHands[i]));
		}

		return out;
	}

	//Given a hand, return all possible 7CARD future hands.
	//i.e. If I pass in 6 cards, I should get back a list of 46 hands.
	//Called recursively
	public static String[] allPossibleHands(String hand){
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> list2 = new ArrayList<String>();

		//If the hand is already 7 cards, just return it.
		if(hand.length() >= 20) return new String[] {hand};
		//Go through each card in the array. 
		//If it isn't already in the hand, add an new row in the list with that card
		for(String c : cards){
			if(!hand.contains(c)) list.add(hand+" "+c);
		}

		System.out.println(list.size()+" "+hand);

		//If there are no future hands, return the current hand
		if(list.size()==0) return new String[] {hand};



		//Go through each new hand in the list,
		//Call all possible hands recursively.
		String[] hands;
		for(String newHand : list){
			hands = allPossibleHands(newHand);
			//Add the new hands to list2
			for(String h : hands) list2.add(h);
		}

		//Add the current hand to the output list...
		list.add(hand);

		//Add list2 to list
		for(String h : list2) list.add(h);

		String[] out = new String[list.size()];
		list.toArray(out);

		return out;


	}


}