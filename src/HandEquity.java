/* Initial Pass at Calculating Hand Equity 
 Equity(Hand) = Sum of All Possible Hand Rankings Givin that Hand

Depending on speed, and memory, I may make this a lookup table.

 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class HandEquity{
	
	public static String[] cards = {
		"As","Ks","Qs","Ts","9s","8s","7s","6s","5s","4s","3s","2s",
		"Ah","Kh","Qh","Th","9h","8h","7h","6h","5h","4h","3h","2h",
		"Ac","Kc","Qc","Tc","9c","8c","7c","6c","5c","4c","3c","2c",
		"Ad","Kd","Qd","Td","9d","8d","7d","6d","5d","4d","3d","2d",};

	public static void main(String[] args){

		HashMap<String,Integer> handIDs = new HashMap<String,Integer>();

		String search = getOrderedHandString("As Ks Ts 9s 6s");

		for (int k=1; k<=7; k++){
			
			System.out.println("Building Sets of "+k+"...");

			List<Set<String>> list = getSubsets(arrayToArrayList(cards),k);

			String h;
			
			int rank = 0;
			for(Set<String> set : list){
				String[] stringArray = new String[1];
				h = getOrderedHandString(cardArrayToString(set.toArray(stringArray)));
				if(h.equals(search)) System.out.println("Found it... "+h);
				try{
					rank = HandEvaluator.rankHand(new Hand(h));
				}catch(Exception e){
					System.out.println(h);
					e.printStackTrace();
					System.exit(1);
				}
				//System.out.println(h+" : "+rank);			
				handIDs.put(h,new Integer(rank));

			}
		}

		System.out.println(search);

		System.out.println(HandEvaluator.rankHand(new Hand("As Ks Ts 9s 6s")));

		System.out.println(handIDs.get(search));


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