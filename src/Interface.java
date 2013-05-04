import java.util.ArrayList;

import javax.sound.midi.Synthesizer;


import java.util.Locale;
import java.util.StringTokenizer;
import java.io.*;
import java.util.Date;

import weka.core.*;
import weka.classifiers.*;

/*--Interface--
 * 
 * Michael Roach
 * 
 * Interface uses a model, generated by Weka, while playing against real people. 
 * Interface uses Polo to convert the actions at the table to instances,
 * and then passes the instances to the model, which then classifies the instance into an action that 
 * an experimenter can then carry out.
 *
 * Type "?" for a list of commands that interface will perform.
 * 
 */

public class Interface{

	
	//--Interface variables--
	public static String[][] commands;
	public static int command = -1;
	public static String input;
	public static BufferedReader in;
	public static StringTokenizer st;
	public static String in_str;
	
	//Texas Holdem Game
	public static String player = "i";
	public static Polo game;
	
	//Weka
	public static Attribute PotOdds;
	public static Attribute Turn;
	public static Attribute TablePosition;
	public static Attribute HandRank;
	public static Attribute Decision;
	public static FastVector features;
	public static Instances instances;
	public static Instance  instance;
	public static Classifier cls;
	
	//Logging
	public static ArrayList<String> inputHistory = new ArrayList<String>();
	public static String masterLogFileName;
	public static String inputLogFileName;
	public static String outputLogFileName;
	public static BufferedWriter masterLogWriter;
	public static BufferedWriter outputLogWriter;
	public static BufferedWriter inputLogWriter;
	public static File masterLogFile;
	public static File outputLogFile;
	public static File inputLogFile;
	public static boolean loggingEnabled;
	
	public static boolean SPEECH = false;
	

	public static void main(String[]args) {

		//startLogging();
		
		//--ATTRIBUTES--
		//*Pot Odds - (amountToCall/PotSize)
		//*Turn
		//*Table Position
		//*Big Blinds Left (if he calls)
		//*Aggression
		//*Number Of Players Remaining in Hand
		//*Hand Rank
		
		//--Class--
		//*DECISION (0=check/fold) (1=call) (2=bet/raise)
		
		//begin commands 
		commands = new String[24][2];
		commands[0][0] = "exit";
		commands[0][1] = "(saves and exits the program)\tex: exit";
		commands[1][0] = "?";
		commands[1][1] = "(list commands)\tex: ?";
		commands[2][0] = "newhand";
		commands[2][1] = "(starts a new hand)\tex: newhand";
		commands[3][0] = "setsb";
		commands[3][1] = "(sets the small blind)\tex: setsb 25";
		commands[4][0] = "setbb";
		commands[4][1] = "setbb 50";
		commands[5][0] = "players";
		commands[5][1] = "(lists the players) ex: players";
		commands[6][0] = "betamounts";
		commands[6][1] = "(how much each player is in for this betting round) ex: betamounts";
		commands[7][0] = "handamounts";
		commands[7][1] = "(how much each player is in for this hand) ex: handamounts";
		commands[8][0] = "addplayer";
		commands[8][1] = "addplayer bob";
		commands[9][0] = "removeplayer";
		commands[9][1] = "removeplayer bob";
		commands[10][0] = "postsb";
		commands[10][1] = "bob postsb - bob posts small blind";
		commands[11][0] = "postbb";
		commands[11][1] = "bob bb - bob posts big blind";
		commands[12][0] = "check";
		commands[12][1] = "bob check";
		commands[13][0] = "folds";
		commands[13][1] = "bob folds";
		commands[14][0] = "calls";
		commands[14][1] = "bob calls";
		commands[15][0] = "bets";
		commands[15][1] = "bob bets 100";
		commands[16][0] = "raises";
		commands[16][1] = "bob raises 100";
		commands[17][0] = "dealt";
		commands[17][1] = "dealt Ah 10c";
		commands[18][0] = "flop";
		commands[18][1] = "flop Ah Kh Qh";
		commands[19][0] = "turn";
		commands[19][1] = "turn Ah";
		commands[20][0] = "river";
		commands[20][1] = "river Ah";
		commands[21][0] = "getstate";
		commands[21][1] = "getstate";
		commands[22][0] = "win";
		commands[22][1] = "win";
		commands[23][0] = "classify";
		commands[23][1] = "classify";
		//end commands
		
		//Initial set up
		output("POKERBOT HUMAN INTERFACE");
		
		game = new Polo(player);
		initialSetupDebug();
		loadClassifier("../Models/Reptree2.model");
		initializeWekaAttributes();
		
		in = new BufferedReader(new InputStreamReader(System.in));
		
		game.addPlayer("bob");
		game.smallBlind=1;
		game.bigBlind=2;
		
		//THE MAIN LOOP!
		while(command != 0){
			command = -1;
			System.out.print(":");
			try {
				in_str = in.readLine().toLowerCase();
			} catch (IOException ioe) {
				output("IO error trying to read input!");
			}
			runCommand(in_str);	
			
		}
		output("Goodbye...");
		System.exit(0);		

	}
	
	public static void runCommand(String in_str){
		st = new StringTokenizer(in_str);
		addToInputLog(in_str);
		//look for the command...
		for(int i=0;i<commands.length;i++){
			if(in_str.contains(commands[i][0])){
				command = i;
				//System.out.print(command+" ");
			}
		}
		try{
			switch(command){
			case -1: output("Command "+in_str+" not found...");
			break;
			case 1:	listCommands();
			break;
			case 2:	newHand();
			break;
			case 3: setSmallBlind(st);				
			break;
			case 4: setBigBlind(st);
			break;
			case 5: listPlayers();
			break;
			case 6: getBetAmounts();
			break;
			case 7: getHandAmounts();
			break;
			case 8: addPlayer(st);
			break;
			case 9: removePlayer(st);
			break;
			case 10: postSmallBlind(st);
			break;
			case 11: postBigBlind(st);
			break;
			case 12: check(st);
			break;
			case 13: fold(st);
			break;
			case 14: call(st);
			break;
			case 15: bets(st);
			break;
			case 16: raise(st);
			break;
			case 17: dealt(st);
			break;
			case 18: flop(st);
			break;
			case 19: turn(st);
			break;
			case 20: river(st);
			break;
			case 21: getState();
			break;
			case 22: win();
			break;
			case 23: classify();
			break;
			}
		}catch(Exception e){
			output("Error: "+commands[command][0]+" "+e.getCause());
			output(e.toString());
			e.printStackTrace();
		}
		
	}

	public static void classify(){
		instance = new Instance(5);

		instance.setValue((Attribute)features.elementAt(0),game.getPotOdds());
		instance.setValue((Attribute)features.elementAt(1),game.getTurn());
		instance.setValue((Attribute)features.elementAt(2),game.getTablePosition());
		instance.setValue((Attribute)features.elementAt(3),game.getHandEquity());
		instance.setDataset(instances);
		double[] fDistribution = new double[0];
		try {
			fDistribution = cls.distributionForInstance(instance);
		} catch (Exception e) {
			e.printStackTrace();
		}
		output("FOLD: "+fDistribution[0]);
		output("RAISE:       "+fDistribution[1]);
		output("BET:  "+fDistribution[2]);
		output("CHECK:       "+fDistribution[3]);
		output("CALL:  "+fDistribution[4]);
	//	output(""+fDistribution);
		
		double max = 0;
		for (int i = 0; i < fDistribution.length; i++) 
        {
            if(max < fDistribution[i])
            {
               max = fDistribution[i]; 
            }
        }
		
		output(""+max);
		if(fDistribution[0]==max){
			output("i think i should fold...");
		}else if(fDistribution[3]==max){
			output("i think i will check");
		}else if(fDistribution[4]==max){
			output("i think i should call "+game.getAmountToCall()+" cents.");
		}else{
			int random = (int)(Math.random()*10);
			System.out.println("BigBlind: "+game.bigBlind+", Random: "+random);
			System.out.println((int)((fDistribution[2])*game.bigBlind*random));
			int raiseAmount = Math.max(game.getAmountToCall()*3,game.getPotSize());
			if((game.getAmountToCall()+raiseAmount)> 0.7 * game.chipStack)
				output("i am all in!");
			else if(game.getAmountToCall()==0)
				output("i will bet "+raiseAmount+" cents.");
			else 
				output("i will see your "+game.getAmountToCall()+", and I will raise you "+raiseAmount+" cents.");
				
		}
	}
	
	public static void loadClassifier(String fileName){
		try {
			cls = (Classifier) weka.core.SerializationHelper.read(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void initializeWekaAttributes(){
		PotOdds = new Attribute("PotOdds");
		FastVector fvNom = new FastVector(4);
		fvNom.addElement("preflop");
		fvNom.addElement("flop");
		fvNom.addElement("turn");
		fvNom.addElement("river");
		Turn = new Attribute("Turn",fvNom);
		TablePosition = new Attribute("TablePosition");
		HandRank = new Attribute("HandRank");
		FastVector fvClass = new FastVector(3);
		fvClass.addElement("checkfold");
		fvClass.addElement("call");
		fvClass.addElement("raisebet");
		Decision = new Attribute("Decision",fvClass);
		features = new FastVector(8);
		features.addElement(PotOdds);
		features.addElement(Turn);
		features.addElement(TablePosition);
		features.addElement(HandRank);
		features.addElement(Decision);
		instances = new Instances("Rel",features,10);
		instances.setClassIndex(4);
	}
	
	public static void newHand(){
		output("--New Hand--");
		game.newHand();
		output("Did "+game.smallBlindPlayer()+" and "+game.bigBlindPlayer()+" post the blinds?");
		try{
			in_str = in.readLine().toLowerCase();
			if(in_str.equals("yes")){
				runCommand(game.smallBlindPlayer()+" postsb");
				runCommand(game.bigBlindPlayer()+" postbb");
			}else{
				output("Big blinds not set");
			}
		}catch(Exception e){
			output("Error in reading input.");
			output("Big blinds not set.");
		}
	}
	
	public static void win(){
		output("Sweet! I won a hand!");
		game.chipStack += game.getPotSize();
		newHand();
	}
	
	public static void getState(){
		output("--Current Instance:--");
		output("Chipstack: "+game.chipStack);
		output("Pot Odds: "+game.getPotOdds());
		output("Turn: "+game.getTurn());
		output("Table Position: "+game.getTablePosition());
		output("Hand Rank: "+game.getHandEquity());
		output("PotSize: "+game.getPotSize());
		output("Hole Cards: "+game.c1+" "+game.c2);
		output("Community Cards: "+" "+game.f1+" "+game.f2+" "+game.f3+" "+game.t+" "+game.r);
	}
	
	public static void river(StringTokenizer st) throws Exception{
		st.nextToken();
		String r = st.nextToken();
		game.DealRiver(r);
		output("Dealt some cards. HR = "+game.getHandEquity());
	}
	
	public static void turn(StringTokenizer st) throws Exception{
		st.nextToken();
		String t = st.nextToken();
		game.DealTurn(t);
		output("Dealt some cards. HR = "+game.getHandEquity());
	}
	
	public static void flop(StringTokenizer st) throws Exception{
		st.nextToken();
		String f1 = st.nextToken();
		String f2 = st.nextToken();
		String f3 = st.nextToken();
		game.DealFlop(f1,f2,f3);
		output("Dealt some cards. HR = "+game.getHandEquity());
	}
	
	public static void dealt(StringTokenizer st) throws Exception{
		st.nextToken();
		String c1 = st.nextToken();
		String c2 = st.nextToken();
		game.DealHand(c1, c2);
		output("Dealt some cards. HR = "+game.getHandEquity());		
	}
	
	public static void raise(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		st.nextToken(); //skip command
		int amount = Integer.parseInt(st.nextToken());
		game.raise(name,amount);
		output(name + " raises "+amount+".");
	}
	
	public static void bets(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		st.nextToken(); //skip command
		int amount = Integer.parseInt(st.nextToken());
		game.bets(name,amount);
		output(name + " bets "+amount+".");
	}
	
	public static void call(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		if(!game.stillInHand[game.seatOfPlayer(name)])
			throw new Exception("Player "+name+" is no longer in hand");
		int amount = game.call(name);
		output(name + " calls "+amount+".");
	}
	
	public static void fold(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		if(game.seatOfPlayer(name) == -1)
			throw new Exception("Player "+name+" does not exist.");
		game.stillInHand[game.seatOfPlayer(name)] = false;
		game.addPassive(name);
		output(name + " folds.");		
	}
	
	public static void check(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		if(game.seatOfPlayer(name) == -1)
			throw new Exception("Player "+name+" does not exist.");
		//output(""+game.getAmountToCall(name));
		if(game.getAmountToCall(name)!=0)
			throw new Exception("Amount to call is not 0 for player "+name);
		game.addPassive(name);
		output(name + " checks.");		
	}
	
	public static void postBigBlind(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		if(game.seatOfPlayer(name) == -1)
			throw new Exception("Player "+name+" does not exist.");
		
		output(name + " posts the big blind of "+game.bigBlind+" from seat "+game.seatOfPlayer(name));
		if(name.equals(player))
			game.chipStack-=game.bigBlind;
		game.addBetAmountToPlayer(name,game.bigBlind);
	}
	
	public static void postSmallBlind(StringTokenizer st) throws Exception{
		String name = st.nextToken();
		if(game.seatOfPlayer(name) == -1)
			throw new Exception("Player "+name+" does not exist.");
		
		output(name + " posts the small blind of "+game.smallBlind+" from seat "+game.seatOfPlayer(name));
		if(name.equals(player))
			game.chipStack-=game.smallBlind;
		game.addBetAmountToPlayer(name,game.smallBlind);
	}
	
	public static void removePlayer(StringTokenizer st) throws Exception{
		st.nextToken();
		String name = st.nextToken();
		int seat = game.removePlayer(name);
		output("Removed player "+name+" from seat "+seat+".");
	}
	
	public static void addPlayer(StringTokenizer st){
		st.nextToken();
		String playername = st.nextToken();
		game.addPlayer(playername);
		output("Added player "+playername+" to seat "+(game.numberOfPlayers-1)+".");
	}
	
	public static void getHandAmounts(){
		for(int i=0;i<game.numberOfPlayers;i++){
			output(game.playersInSeats[i]+"\t"+game.amountsPlayersAreIn_Hand[i]+"\t"+game.stillInHand[i]);
		}
	}
	
	public static void getBetAmounts(){
		for(int i=0;i<game.numberOfPlayers;i++){
			output(game.playersInSeats[i]+"\t"+game.amountsPlayersAreIn_Bet[i]+"\t"+game.stillInHand[i]);
		}
	}
	
	public static void listCommands(){
		output("--COMMANDS---");
		for(int i=0;i<commands.length;i++)
			output(i+".\t"+commands[i][0]+"\t"+commands[i][1]);
	}
	
	public static void listPlayers(){
		output("--List of Players--");
		output("number of players: "+game.numberOfPlayers);
		for(int i=0;i<game.numberOfPlayers;i++){
			output(i+". "+game.playersInSeats[i]);
		}
	}	
	
	public static void setSmallBlind(StringTokenizer st){
		st.nextToken();
		game.smallBlind = Integer.parseInt(st.nextToken());
		output("Small blind set to "+game.smallBlind);
	}
	
	public static void setBigBlind(StringTokenizer st){
		st.nextToken();
		game.bigBlind = Integer.parseInt(st.nextToken());
		output("Big blind set to " + game.bigBlind);
	}
	
//	public static void evaluateHand(String str){
//		handRank = HandEvaluator.rankHand(new Hand(str.substring(str.indexOf("["),str.lastIndexOf("]"))));
//	}
	
	public static void initialSetup(){
		output("---Initial Setup---");
		output("List players in order, starting from the person on your left. Leave a line blank when you are done.");
		//Insert the names of the players, add the players as you go.
		output("Which player is the starting dealer?");
		//rotate the players until the dealer is 0...
		output("Small blind?");
		output("Big Blind?");	
	}
	
	public static void initialSetupDebug(){
		game.chipStack = 2000;
	}
	
	public static void startLogging(){
		loggingEnabled = true;
		Date d = new Date();
		outputLogFileName = new String((d.toString()).replace(':',' ')+"_OUTPUT.txt");
		inputLogFileName = new String((d.toString()).replace(':',' ')+"_INPUT.txt");
		masterLogFileName = new String((d.toString()).replace(':',' ')+"_MASTER.txt");
		outputLogFile = new File(outputLogFileName);
		inputLogFile = new File(inputLogFileName);
		masterLogFile = new File(masterLogFileName);
		try{
			outputLogWriter = new BufferedWriter(new FileWriter(outputLogFile));
			inputLogWriter = new BufferedWriter(new FileWriter(inputLogFile));
			masterLogWriter = new BufferedWriter(new FileWriter(masterLogFile));
			outputLogWriter.write("--Output Logging Started: "+d.toString()+"--");
			inputLogWriter.write("--Input Logging Started: "+d.toString()+"--");
			masterLogWriter.write("--Master Logging Started: "+d.toString()+"--");
			inputLogWriter.newLine();
			outputLogWriter.newLine();
			masterLogWriter.newLine();
			outputLogWriter.close();
			inputLogWriter.close();
			masterLogWriter.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void output(String s){
		System.out.println(s);
		
		if(loggingEnabled){
			try{
				outputLogWriter = new BufferedWriter(new FileWriter(outputLogFile, true));
				outputLogWriter.write(s);
				outputLogWriter.newLine();
				outputLogWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}	
	
	public static void addToInputLog(String s){
		if(loggingEnabled){
			try{
				inputLogWriter = new BufferedWriter(new FileWriter(inputLogFile, true));
				inputLogWriter.write(s);
				inputLogWriter.newLine();
				inputLogWriter.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	

}
