/* Initial Pass at Calculating Hand Equity 
 Equity(Hand) = Sum of All Possible Hand Rankings Givin that Hand

//Preflop equity is going to be based off of preflophands.com



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

	public static String[] currentHand = new String[7];
	public static String currentHandString = "";

	public static double totalEquity = 0;
	public static int handsEvaluated = 0;

	public static ArrayList<String> cardArrayList = (ArrayList<String>)arrayToArrayList(cards);

	public static void main(String[] args){

		

		currentHand[0] = args[0];
		currentHand[1] = args[1];
		if(args.length > 2) currentHand[2] = args[2];
		if(args.length > 3) currentHand[3] = args[3];
		if(args.length > 4) currentHand[4] = args[4];
		if(args.length > 5) currentHand[5] = args[5];
		if(args.length > 6) currentHand[6] = args[6];


		currentHandString = cardArrayToString(currentHand);

		for(String c : currentHand) cardArrayList.remove(c);

		int k = 7- (52 - cardArrayList.size());
			
		System.out.println("Building Sets of "+k+"...");

		

		int r = HandEvaluator.rankHand(new Hand(cardArrayToString(currentHand)));
		int r2 = HandEvaluator.rankHand(new Hand(currentHand[0]+" "+currentHand[1]));	
		

		System.out.println("Rank of hand: "+cardArrayToString(currentHand)+" : "+r);
		System.out.println("Rank of whole: "+cardArrayToString(currentHand)+" : "+r2);
		System.out.println("Rank of hand+whole: "+cardArrayToString(currentHand)+" : "+(r+r2));
		
		
		long startTime = System.nanoTime();
		if(k==5){
			evaluateTwoCardHand(cardArrayToString(currentHand));
		}else{
			List<Set<String>> list = getSubsets(cardArrayList,k);
		}
		long endTime = System.nanoTime();


		System.out.println("Total Time: "+((endTime-startTime)/1000000));

		totalEquity /= handsEvaluated;

		System.out.println("EQ:"+totalEquity);
		System.out.println("Total Hands Evaluated: "+handsEvaluated);


	}

	public static double rankHand(String hand){
		currentHandString = hand;
		currentHand = hand.split(" ");

		for(String c : currentHand) cardArrayList.remove(c);

		int k = 7- (52 - cardArrayList.size());

		List<Set<String>> list = getSubsets(cardArrayList,k);

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

		System.out.println(hand);

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
		out.add("AA");
		out.add("KK");
		out.add("QQ");
		out.add("AKS");
		out.add("JJ");
		out.add("AQS");
		out.add("QKS");
		out.add("AJS");
		out.add("AK");
		out.add("ATS");
		out.add("JKS");
		out.add("TT");
		out.add("JQS");
		out.add("TKS");
		out.add("QTS");
		out.add("JTS");
		out.add("AQ");
		out.add("9AS");
		out.add("QK");
		out.add("AJ");
		out.add("8AS");
		out.add("99");
		out.add("5AS");
		out.add("9KS");
		out.add("7AS");
		out.add("9TS");
		out.add("4AS");
		out.add("9QS");
		out.add("JK");
		out.add("9JS");
		out.add("AT");
		out.add("3AS");
		out.add("JQ");
		out.add("2AS");
		out.add("6AS");
		out.add("TK");
		out.add("8KS");
		out.add("88");
		out.add("JT");
		out.add("QT");
		out.add("8TS");
		out.add("7KS");
		out.add("8QS");
		out.add("8JS");
		out.add("89S");
		out.add("5KS");
		out.add("6KS");
		out.add("77");
		out.add("78S");
		out.add("4KS");
		out.add("9A");
		out.add("7TS");
		out.add("79S");
		out.add("7QS");
		out.add("7JS");
		out.add("3KS");
		out.add("2KS");
		out.add("9T");
		out.add("8A");
		out.add("5QS");
		out.add("9K");
		out.add("6QS");
		out.add("5A");
		out.add("9J");
		out.add("66");
		out.add("9Q");
		out.add("4QS");
		out.add("7A");
		out.add("68S");
		out.add("67S");
		out.add("4A");
		out.add("45S");
		out.add("3QS");
		out.add("57S");
		out.add("6TS");
		out.add("55");
		out.add("5JS");
		out.add("69S");
		out.add("56S");
		out.add("6JS");
		out.add("2QS");
		out.add("3A");
		out.add("4JS");
		out.add("58S");
		out.add("2A");
		out.add("44");
		out.add("8T");
		out.add("5TS");
		out.add("6A");
		out.add("3JS");
		out.add("8K");
		out.add("59S");
		out.add("35S");
		out.add("89");
		out.add("4TS");
		out.add("2JS");
		out.add("8J");
		out.add("33");
		out.add("8Q");
		out.add("47S");
		out.add("46S");
		out.add("7K");
		out.add("22");
		out.add("3TS");
		out.add("34S");
		out.add("2TS");
		out.add("48S");
		out.add("78");
		out.add("49S");
		out.add("25S");
		out.add("5K");
		out.add("6K");
		out.add("79");
		out.add("7T");
		out.add("39S");
		out.add("37S");
		out.add("36S");
		out.add("24S");
		out.add("7Q");
		out.add("4K");
		out.add("7J");
		out.add("29S");
		out.add("38S");
		out.add("3K");
		out.add("23S");
		out.add("28S");
		out.add("5Q");
		out.add("2K");
		out.add("6Q");
		out.add("67");
		out.add("27S");
		out.add("68");
		out.add("45");
		out.add("26S");
		out.add("57");
		out.add("4Q");
		out.add("56");
		out.add("69");
		out.add("6T");
		out.add("5J");
		out.add("3Q");
		out.add("6J");
		out.add("58");
		out.add("2Q");
		out.add("4J");
		out.add("35");
		out.add("5T");
		out.add("59");
		out.add("3J");
		out.add("47");
		out.add("46");
		out.add("4T");
		out.add("2J");
		out.add("34");
		out.add("3T");
		out.add("48");
		out.add("2T");
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