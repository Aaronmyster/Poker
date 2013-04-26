/* Initial Pass at Calculating Hand Equity 
 Equity(Hand) = Sum of All Possible Hand Rankings Givin that Hand

Depending on speed, and memory, I may make this a lookup table.

 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class HandEquity{
	
	public static String[] cards = {
		"As","Ks","Qs","Js","Ts","9s","8s","7s","6s","5s","4s","3s","2s",
		"Ah","Kh","Qh","Jh","Th","9h","8h","7h","6h","5h","4h","3h","2h",
		"Ac","Kc","Qc","Jc","Tc","9c","8c","7c","6c","5c","4c","3c","2c",
		"Ad","Kd","Qd","Jd","Td","9d","8d","7d","6d","5d","4d","3d","2d",};

	public static void main(String[] args){

		String[] currentHand = new String[7];

		ArrayList<String> cardArrayList = (ArrayList<String>)arrayToArrayList(cards);

		currentHand[0] = args[0];
		currentHand[1] = args[1];
		if(args.length > 2) currentHand[2] = args[2];
		if(args.length > 3) currentHand[3] = args[3];
		if(args.length > 4) currentHand[4] = args[4];
		if(args.length > 5) currentHand[5] = args[5];

		for(String c : currentHand) cardArrayList.remove(c);
		
		int k = 7- (52 - cardArrayList.size());
			
		System.out.println("Building Sets of "+k+"...");

		List<Set<String>> list = getSubsets(cardArrayList,k);

		String h;
		
		double equity = 0;
		for(Set<String> set : list){
			String[] stringArray = new String[1];
			h = "";
			for(String c : currentHand) if(c!=null) h = h + c + " ";
			h = h+cardArrayToString(set.toArray(stringArray));
			try{
				equity += HandEvaluator.rankHand(new Hand(h));
				//System.out.println(h+" : "+rank);

			}catch(Exception e){
				System.out.println(h);
				e.printStackTrace();
				System.exit(1);
			}

		}

		System.out.println("BOOM!: "+equity/list.size());


	}

	//Convert an array of cards into a hand string
	public static String cardArrayToString(String[] cards){
		String hand = "";
		for(String c: cards) hand = hand+c+" ";
		hand = hand.trim();
		return hand;
	}

	//Get hand identifier (a unique integer given to each possible hand. Order does not matter);
	public static String getOrderedHandString(String hand){
		String out = "";
		String[] cardsInHand = hand.split(" ");

		java.util.Arrays.sort(cardsInHand);
		for(int i=0;i<cardsInHand.length;i++){
			out += cardsInHand[i] + " ";
		}
		return out.trim();
	}

	private static void getSubsets(List<String> superSet, int k, int idx, Set<String> current,List<Set<String>> solution) {
	    //successful stop clause
	    if (current.size() == k) {
	        solution.add(new HashSet<>(current));
	        return;
	    }
	    //unseccessful stop clause
	    if (idx == superSet.size()) return;
	    String x = superSet.get(idx);
	    current.add(x);
	    //"guess" x is in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	    current.remove(x);
	    //"guess" x is not in the subset
	    getSubsets(superSet, k, idx+1, current, solution);
	}

	public static List<Set<String>> getSubsets(List<String> superSet, int k) {
	    List<Set<String>> res = new ArrayList<>();
	    getSubsets(superSet, k, 0, new HashSet<String>(), res);
	    return res;
	}

	public static List<String> arrayToArrayList(String[] a){
		ArrayList<String> out = new ArrayList<String>();
		for(String s : a) out.add(s);
		return out;
	}


}