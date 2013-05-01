/* Initial Pass at Calculating Hand Equity 
 Equity(Hand) = Sum of All Possible Hand Rankings Givin that Hand

//Preflop equity is going to be based off of preflophands.com



 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class HandEquity{

	
	public static String[] cards = {
		"as","ks","qs","js","ts","9s","8s","7s","6s","5s","4s","3s","2s",
		"ah","kh","qh","jh","th","9h","8h","7h","6h","5h","4h","3h","2h",
		"ac","kc","qc","jc","tc","9c","8c","7c","6c","5c","4c","3c","2c",
		"ad","kd","qd","jd","td","9d","8d","7d","6d","5d","4d","3d","2d",};

	public static String[] currentHand = new String[7];
	public static String currentHandString = "";

	public static double totalEquity = 0;
	public static int handsEvaluated = 0;

	public static ArrayList<String> cardArrayList;

	public static void main(String[] args){

		

		currentHand[0] = args[0];
		currentHand[1] = args[1];
		if(args.length > 2) currentHand[2] = args[2];
		if(args.length > 3) currentHand[3] = args[3];
		if(args.length > 4) currentHand[4] = args[4];
		if(args.length > 5) currentHand[5] = args[5];
		if(args.length > 6) currentHand[6] = args[6];

		String c1 = "";  
		String c2 = "";  
		String f1 = "";  
		String f2 = "";  
		String f3 = "";  
		String t = "";  
		String r = "";  

		if(currentHand[0]!=null) c1 = currentHand[0];
		if(currentHand[1]!=null) c2 = currentHand[1];
		if(currentHand[2]!=null) f1 = currentHand[2];
		if(currentHand[3]!=null) f2 = currentHand[3];
		if(currentHand[4]!=null) f3 = currentHand[4];
		if(currentHand[5]!=null) t = currentHand[5];
		if(currentHand[6]!=null) r = currentHand[6];

		currentHandString = cardArrayToString(currentHand);

		cardArrayList = (ArrayList<String>)arrayToArrayList(cards);
		for(String c : currentHand) cardArrayList.remove(c);

		int k = 7- (52 - cardArrayList.size());
			
		System.out.println("Building Sets of "+k+"...");

		

		//int r = HandEvaluator.rankHand(new Hand(cardArrayToString(currentHand)));
		//int r2 = HandEvaluator.rankHand(new Hand(currentHand[0]+" "+currentHand[1]));	
		

		//System.out.println("Rank of hand: "+cardArrayToString(currentHand)+" : "+r);
		//System.out.println("Rank of whole: "+cardArrayToString(currentHand)+" : "+r2);
		//System.out.println("Rank of hand+whole: "+cardArrayToString(currentHand)+" : "+(r+r2));
		
		
		long startTime = System.nanoTime();
		//if(k==5){
		//	evaluateTwoCardHand(cardArrayToString(currentHand));
		//}else{
		//	List<Set<String>> list = getSubsets(cardArrayList,k);
		//}
		
		double community = HandEquity.rankHand(f1+" "+f2+" "+f3+" "+t+" "+r);
		double ourHand = HandEquity.rankHand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r);

		System.out.println("Community: "+community);
		System.out.println("OurHand: "+ourHand);
		System.out.println("Output: "+(ourHand/community));

		long endTime = System.nanoTime();
		System.out.println("Total Time: "+((endTime-startTime)/1000000));
//
		//totalEquity /= handsEvaluated;
//
		//System.out.println("EQ:"+totalEquity);
		//System.out.println("Total Hands Evaluated: "+handsEvaluated);


	}

	public static double rankHand(String hand){
		currentHandString = hand;
		currentHand = hand.split(" ");

		totalEquity = 0;
		handsEvaluated = 0;

		cardArrayList = (ArrayList<String>)arrayToArrayList(cards);
		for(String c : currentHand) cardArrayList.remove(c);

		int k = 7- (52 - cardArrayList.size());

		if(k==5){
			//System.out.println("TWO CARDS: "+k+" "+currentHandString);
			evaluateTwoCardHand(cardArrayToString(currentHand));
		}else{
			//System.out.println("MORE CARDS: "+k+" "+currentHandString);
			List<Set<String>> list = getSubsets(cardArrayList,k);
		}

		//System.out.println("Total Equity: "+totalEquity);
		//System.out.println("Total Hands : "+handsEvaluated);

		totalEquity /= handsEvaluated;

		return totalEquity;
	}

	//Convert an array of cards into a hand string
	public static String cardArrayToString(String[] cards){
		String hand = "";
		for(String c: cards) if(c!=null) hand = hand+c+" ";
		hand = hand.trim();
		return hand;
	}

	//Get hand identifier (a unique String given to each possible hand. Order does not matter);
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
	        //solution.add(new HashSet<>(current));
	        String h = "";
	        h = h+currentHandString+" ";
	        for(String c : current) h = h + c + " ";
	        totalEquity += HandEvaluator.rankHand(new Hand(h));
	    	//System.out.println(h + " : " +totalEquity);
	        handsEvaluated++;
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

	public static void evaluateTwoCardHand(String hand){
		//remove spaces
		hand = hand.replaceAll(" ","");

		//Sort the characters in the hand
		String unsorted = hand;
		char[] content = unsorted.toCharArray();
		Arrays.sort(content);
		String sorted = new String(content);
		hand = sorted;

		hand = removeSuite(hand,"h");
		hand = removeSuite(hand,"s");
		hand = removeSuite(hand,"d");
		hand = removeSuite(hand,"c");

		totalEquity = 169 - preflopList().indexOf(hand);
		handsEvaluated = 1;		

	}

	public static String removeSuite(String hand, String s){
		int length = hand.length();
		hand = hand.replaceAll(s,"");
		if(hand.length()==(length-2)) hand = hand+"S"; //if I remove two characters, then it's suited
		return hand;
	}

	public static ArrayList<String> preflopList(){
		ArrayList<String> out = new ArrayList<String>();
		out.add("aa");
		out.add("kk");
		out.add("qq");
		out.add("akS");
		out.add("jj");
		out.add("aqS");
		out.add("qkS");
		out.add("ajS");
		out.add("ak");
		out.add("atS");
		out.add("jkS");
		out.add("tt");
		out.add("jqS");
		out.add("tkS");
		out.add("qtS");
		out.add("jtS");
		out.add("aq");
		out.add("9aS");
		out.add("qk");
		out.add("aj");
		out.add("8aS");
		out.add("99");
		out.add("5aS");
		out.add("9kS");
		out.add("7aS");
		out.add("9tS");
		out.add("4aS");
		out.add("9qS");
		out.add("jk");
		out.add("9jS");
		out.add("at");
		out.add("3aS");
		out.add("jq");
		out.add("2aS");
		out.add("6aS");
		out.add("tk");
		out.add("8kS");
		out.add("88");
		out.add("jt");
		out.add("qt");
		out.add("8tS");
		out.add("7kS");
		out.add("8qS");
		out.add("8jS");
		out.add("89S");
		out.add("5kS");
		out.add("6kS");
		out.add("77");
		out.add("78S");
		out.add("4kS");
		out.add("9a");
		out.add("7tS");
		out.add("79S");
		out.add("7qS");
		out.add("7jS");
		out.add("3kS");
		out.add("2kS");
		out.add("9t");
		out.add("8a");
		out.add("5qS");
		out.add("9k");
		out.add("6qS");
		out.add("5a");
		out.add("9j");
		out.add("66");
		out.add("9q");
		out.add("4qS");
		out.add("7a");
		out.add("68S");
		out.add("67S");
		out.add("4a");
		out.add("45S");
		out.add("3qS");
		out.add("57S");
		out.add("6tS");
		out.add("55");
		out.add("5jS");
		out.add("69S");
		out.add("56S");
		out.add("6jS");
		out.add("2qS");
		out.add("3a");
		out.add("4jS");
		out.add("58S");
		out.add("2a");
		out.add("44");
		out.add("8t");
		out.add("5tS");
		out.add("6a");
		out.add("3jS");
		out.add("8k");
		out.add("59S");
		out.add("35S");
		out.add("89");
		out.add("4tS");
		out.add("2jS");
		out.add("8j");
		out.add("33");
		out.add("8q");
		out.add("47S");
		out.add("46S");
		out.add("7k");
		out.add("22");
		out.add("3tS");
		out.add("34S");
		out.add("2tS");
		out.add("48S");
		out.add("78");
		out.add("49S");
		out.add("25S");
		out.add("5k");
		out.add("6k");
		out.add("79");
		out.add("7t");
		out.add("39S");
		out.add("37S");
		out.add("36S");
		out.add("24S");
		out.add("7q");
		out.add("4k");
		out.add("7j");
		out.add("29S");
		out.add("38S");
		out.add("3k");
		out.add("23S");
		out.add("28S");
		out.add("5q");
		out.add("2k");
		out.add("6q");
		out.add("67");
		out.add("27S");
		out.add("68");
		out.add("45");
		out.add("26S");
		out.add("57");
		out.add("4q");
		out.add("56");
		out.add("69");
		out.add("6t");
		out.add("5j");
		out.add("3q");
		out.add("6j");
		out.add("58");
		out.add("2q");
		out.add("4j");
		out.add("35");
		out.add("5t");
		out.add("59");
		out.add("3j");
		out.add("47");
		out.add("46");
		out.add("4t");
		out.add("2j");
		out.add("34");
		out.add("3t");
		out.add("48");
		out.add("2t");
		out.add("25");
		out.add("49");
		out.add("36");
		out.add("37");
		out.add("24");
		out.add("39");
		out.add("29");
		out.add("38");
		out.add("23");
		out.add("28");
		out.add("27");
		out.add("26");
		return out;
	}


}