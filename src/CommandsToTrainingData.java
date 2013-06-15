import java.util.regex.*;
import java.io.*;

//Converts PokerStars hand history to a list of commands to be read by CommandsToTrainingData

public class CommandsToTrainingData{

	public static String inputFileName;
	public static String playerName;
	public static String NOMATCH = "--NOMATCH--";
	public static Game game;
	public static boolean DEBUG = false;
	
	public static void main(String[] args){

		//Parameters are an input file name, and an output file name...
		checkParameters(args);

		System.out.println("HandID,PotOdds,Turn,TablePosition,BigBlindsLeft,Aggression,NumberOfPlayersLeft,HandRank,HandEquity,Decision");
		
		try{
			File f = new File(inputFileName);

			if(f.isDirectory()){
				for (File child : f.listFiles())
					processFile(child);
			}else{
				processFile(f);
			}

		}catch(Exception e){
			System.err.println(e);
			e.printStackTrace();
			System.exit(1);
		}		

	}

	public static void processFile(File inputFile) throws Exception{
		String str, s;
		int line = 0;

		try{

			//Open the file. Try to process it line by line...

			System.err.println(inputFile.getName());

			game = new Polo(playerName);
			BufferedReader in = new BufferedReader(new FileReader(inputFile));

			long startTime = System.nanoTime();
			while (((str = in.readLine()) != null)){
				line++;
				if( line % 100 == 0) System.err.println(line+" : "+((System.nanoTime()-startTime)/1000000000)+" seconds");
				s = processLine(str);
				if(!s.equals("")) System.out.println(s);

			}
			System.out.println("");

		}catch(Exception e){
			System.err.println("File: "+inputFile.getName());
			System.err.println("Line: "+line);
			throw e;
		}

			
	}

	//Returns the processed string...
	public static String processLine(String input) throws Exception{
		String out = "";
		String tempOut = "";
		String c[];

		String[] args = input.split("\\|\\|");

		for(int i=0;i<args.length;i++) args[i] = args[i].trim();

		if(DEBUG){
			for(int i=0;i<args.length;i++) System.out.print(args[i]+" ");
			System.out.println();
		}

		switch(args[0]){
			case "--NEW_GAME--" :
				game = new Game(playerName);
				break;
			case "NEWHAND" :
				game.newHand();
				game.handID = args[1];
				break;
			case "SET_BUTTON" :
				game.setButtonToSeat(integer(args[1]));
				break;
			case "ADD_PLAYER" :
				game.addPlayer(args[1]);
				break;
			case "ANTE" :
				game.addBetAmountToPlayer(args[1],money(args[2]));
				break;
			case "SMALL_BLIND" :
				game.addBetAmountToPlayer(args[1],money(args[2]));
				break;
			case "BIG_BLIND" :
				game.addBetAmountToPlayer(args[1],money(args[2]));
				break;
			case "SMALL_AND_BIG_BLINDS" :
				game.addBetAmountToPlayer(args[1],money(args[2]));
				break;
			case "DEAL_CARDS" :
				c = args[2].split(" ");
				game.DealHand(c[0],c[1]);
				break;
			case "CALL":
				if(args[1].equals(playerName)){
					System.out.println(game.getState()+"CALL");
				}
				game.addBetAmountToPlayer(args[1],money(args[2]));
				game.addPassive(args[1]);
				break;
			case "FOLD":
				if(args[1].equals(playerName)){
					System.out.println(game.getState()+"FOLD");
				}
				game.removePlayer(args[1]);
				game.addPassive(args[1]);
				break;
			case "RAISE":
				if(args[1].equals(playerName)){
					System.out.println(game.getState()+"RAISE");
				}
				game.raise(args[1],money(args[3]));
				game.addAggression(args[1]);
				break;
			case "BETS":
				if(args[1].equals(playerName)){
					System.out.println(game.getState()+"BET");
				}
				game.addBetAmountToPlayer(args[1],money(args[2]));
				game.addAggression(args[1]);
				break;
			case "CHECK":
				if(args[1].equals(playerName)){
					System.out.println(game.getState()+"CHECK");
				}
				game.addPassive(args[1]);
				break;
			case "LEAVE TABLE":
				game.removePlayer(args[1]);
				break;
			case "COLLECTED_FROM_POT":
				if(args[1].equals(playerName)){
					game.chipStack+=money(args[2]);
				}
				break;
			case "COLLECTED_FROM_SIDE_POT":
				if(args[1].equals(playerName)){
					game.chipStack+=money(args[2]);
				}
				break;
			case "FLOP":
				c = args[1].split(" ");
				game.DealFlop(c[0],c[1],c[2]);
				break;
			case "TURN":
				game.DealTurn(args[1]);
				break;
			case "RIVER":
				game.DealRiver(args[1]);
				break;


		}

		if(DEBUG) System.out.println(game.getState());

		

		return out;

	}

	public static int integer(String s){
		return Integer.parseInt(s);
	}

	public static int money(String s){
		double d = (Double.parseDouble(s)) * 100;
		return (int)d;
	}

	public static void checkParameters(String[] args){
		try{
			
			playerName = args[0];
			inputFileName = args[1];

		}catch(Exception e){
			System.err.println("There's a problem with your parameters...");
			System.err.println("CommandsToTrainingData playername inputfile or inputdir");
			System.exit(1);
		}
	}

}