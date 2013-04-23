import java.io.*;
import java.util.StringTokenizer;

/* -- Interpreter --
 * 
 * Michael Roach
 * 
 * 
 * Interpreter is used to parse through the natural language of the history file,
 * extrapolate all of the most important information (i.e. the features or attributes for classification),
 * and essentially replay those hands while keeping track of what a particular player did.
 * 
 * The history file simply re-tells the actions of every player in every hand.
 * The instance set file produced from that history, contains all of the important attributes,
 * and the actions (i.e. the classifications) of 1 player (WarnerK: the friend that provided the history)
 * 
 * 
 */

public class Interpreter {
	
	public static Polo game;
	
	public static int numberOfPlayers = 9;
	public static String player = "Alameda22";
	public static String InputFileName = "HandHistoryPS.txt";
	public static String OutputFileName = "InstanceSet.csv";

	public static boolean summaryHasBeenReached;
	
	//Counters for decisions
	public static int numberOfHands = 0;
	public static int numberOfDecisions = 0;
	public static int numberOfFoldsOrChecks = 0;
	public static int numberOfCalls = 0;
	public static int numberOfBetsOrRaises = 0;
	
	public static String[] previousState = new String[7];
	public static String output = new String();
	public static int lineNumber = 0;
	
	public static void main(String[]args){
		String str;
		game = new Polo(player,0);
		//*Pot Odds - (amountToCall/PotSize)
		//*Turn
		//*Table Position
		//*Big Blinds Left (if he calls)
		//*Aggression
		//*Number Of Players Remaining in Hand
		//*Hand Rank
		//*DECISIONS (0=check/fold) (1=call) (2=bet/raise)
		
		
		System.out.println("ID,PotOdds,Turn,TablePosition,BigBlindsLeft,Aggression,NumberOfPlayersLeft,HandRank,Decision");
		output = output.concat(("ID,PotOdds,Turn,TablePosition,BigBlindsLeft,Aggression,NumberOfPlayersLeft,HandRank,Decision"+'\n'));
		try{
			BufferedReader in = new BufferedReader(new FileReader(InputFileName));
			while (((str = in.readLine()) != null)){
				lineNumber++;
				//Determine if this line shows a decision by player				
				if((str.startsWith(player))&&(!str.contains("posts"))&&(!str.contains("mucks"))&&(!str.contains("wins"))&&(!str.contains("has"))&&(!str.contains("shows"))&&(!str.contains("adds"))&&(!str.contains("******"))&&(!str.contains(":"))&&(!str.contains("ties"))&&(!str.contains("******"))&&(!str.contains(":"))&&(!str.contains("antes"))&&(!str.contains(" is "))&&(!str.contains(" brings "))&&(!str.contains(" completes "))){
					//System.out.print(lineNumber+" : "+(numberOfDecisions+","+game.getTurn()));
					output = output.concat((numberOfDecisions+","));
					output = output.concat((game.getPotOdds()+","));
					output = output.concat((game.getTurn()+","));
					output = output.concat((game.getTablePositionRatio()+","));
					output = output.concat((game.getBigBlindsLeft()+","));
					output = output.concat((game.getAggressionOfRemainingPlayers()+","));
					output = output.concat((game.getNumberOfRemainingPlayers()+","));
					output = output.concat((game.getHandRank()+","));
					
					if((str.contains("raises ")||str.contains("bets "))){
						//System.out.println("raisebet");
						output = output.concat(("raisebet"+'\n'));
						numberOfBetsOrRaises++;
					}else if(str.contains(" calls ")){
						//System.out.println("call");
						output = output.concat(("call"+'\n'));
						numberOfCalls++;
					}else if((str.contains(" checks"))||str.contains(" folds")){
						//System.out.println("checkfold");
						output = output.concat(("checkfold"+"\n"));
						numberOfFoldsOrChecks++;
					}
					//System.out.println(str);
					numberOfDecisions++;
				}
				
				//System.out.print(str);
				if(str.contains("Full Tilt")||(str.contains("FullTiltPoker"))){
					game.newHand_clearPlayers();
					summaryHasBeenReached = false;
				}else if(str.contains("Dealt to "+player)){
					dealCards(str);
				}else if(str.contains("*** FLOP ***")){
					dealFlop(str);
				}else if(str.contains("*** TURN ***")){
					dealTurn(str);
				}else if(str.contains("*** RIVER ***")){
					dealRiver(str);
				}else if(str.contains("SUMMARY")){
					summaryHasBeenReached = true;
				}else if(str.contains("Seat")&&!summaryHasBeenReached){
					//if(!str.contains("sitting out"))
						addPlayerInSeat(str);
				}else if(str.contains("The button is in seat #")){
					int button = Integer.parseInt(str.replaceAll("The button is in seat #",""));
					game.setButtonToSeat(button);
				}else if(str.contains("posts the small blind of")){					
					game.addBetAmountToPlayer(getPlayerName(str),getNumbersOffEnd(str));					
					//printAmountsPlayersAreIn();
				}else if(str.contains("posts the big blind of")){
					game.addBetAmountToPlayer(getPlayerName(str),getNumbersOffEnd(str));
					game.bigBlind = (int)(getNumbersOffEnd(str));
					//printAmountsPlayersAreIn();
				}else if((str.contains("bets "))&&(!str.contains(":"))){
					game.addBetAmountToPlayer(getPlayerName(str),getNumbersOffEnd(str));
					game.addAggression(getPlayerName(str));
					//printAmountsPlayersAreIn();
				}else if(str.contains("raises to")){
					game.raise(getPlayerName(str),getNumbersOffEnd(str));
					game.addAggression(getPlayerName(str));
					//printAmountsPlayersAreIn();
				}else if(str.contains("calls")){
					game.addBetAmountToPlayer(getPlayerName(str),getNumbersOffEnd(str));
					game.addPassive(getPlayerName(str));
					//printAmountsPlayersAreIn();					
				}else if(str.contains("checks")){										//ADDED 4/5/2011
					game.addPassive(getPlayerName(str));                                         
					//printAmountsPlayersAreIn();					
				}else if(str.contains("folds")){
					game.removePlayer(getPlayerName(str));
					game.addPassive(getPlayerName(str));					
				}else if(str.contains("Uncalled bet of ")){
					//game.
					game.subtractBetAmountFromPlayer(getPlayerName(str),getNumbersOffEnd(str));
					//printAmountsPlayersAreIn();
				}
				//System.out.println("*"+getPotOdds()+","+turn+","+tablePosition+","+getBigBlindsLeft()+","+getAggressionOfRemainingPlayers()+","+getNumberOfRemainingPlayers()+","+handRank);
				//if(aggressionMap.containsKey(player))
					//System.out.println("--"+aggressionMap.get(player)[0]+"/"+aggressionMap.get(player)[1]+"--");
				
				
				
				
				
			}
			in.close();
			System.out.println("THE FILE");
			System.out.print(output);
			System.out.println("Writing File...");
			//WRITE OUTPUT TO FILE
			try{
				FileWriter fstream = new FileWriter(OutputFileName);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(output);
				out.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			
			System.out.println();
			System.out.println("The player "+player+" made "+numberOfDecisions+" decisions in "+numberOfHands+" hands.");
			System.out.println();
			System.out.println("He made "+numberOfFoldsOrChecks+" folds and checks,");
			System.out.println("he made "+numberOfCalls+" calls, and");
			System.out.println("he made "+numberOfBetsOrRaises+" bets and raises.");
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static String getPlayerName(String x){
		//System.out.println("GET PLAYER NAME "+x);
		if(x.contains(" posts the small blind of"))
			x = x.substring(0,x.indexOf(" posts the small blind of"));
		else if(x.contains(" posts the big blind of"))
			x = x.substring(0,x.indexOf(" posts the big blind of"));
		else if(x.contains(" bets "))
			x = x.substring(0,x.indexOf(" bets "));
		else if(x.contains(" raises to "))
			x = x.substring(0,x.indexOf(" raises to "));
		else if(x.contains(" calls"))
			x = x.substring(0,x.indexOf(" calls"));
		else if(x.contains(" folds")){
			x = x.substring(0,x.indexOf(" folds"));
		}else if(x.contains("returned to"))
			x = x.substring(x.indexOf("returned to ")+12,x.length());
		
		return x;
	}
	
	public static int getNumbersOffEnd(String str){
		//System.out.println(str);
		str = str.replace(",","");
		StringTokenizer st = new StringTokenizer(str);
		double d = 0;
		boolean found = false;
		String temp;
		while(st.hasMoreTokens()&&!found){
			temp = st.nextToken();
			try{
				d = Double.parseDouble(temp)*100;
				found = true;
			}catch(Exception e){
				
			}
		}
		int i = (int)d;
		//System.out.println("I:"+i+" B:"+begin+" E:"+end+" "+str);
		return i;
	}
	
	public static void addPlayerInSeat(String str){
		//System.out.println("Player in seat "+str);
		//int i = Integer.parseInt(str.substring(5,6));
		//Get the name
		String y = str.substring(7,str.length());		
		String x = y.substring(1,y.indexOf(" ("));
		//Set the starting amount
		y = y.replace(")","");
		y = y.replace("(","");
		int yInt = getNumbersOffEnd(y);
		if(x.equals(player))
			game.chipStack = yInt;
		
		game.addPlayer(x);
		
	}
	
	public static void dealCards(String str){
		String cards = str.substring((str.indexOf("[")+1),str.lastIndexOf("]"));
		StringTokenizer st = new StringTokenizer(cards);
		String c1 = st.nextToken();
		String c2 = st.nextToken();
		game.DealHand(c1,c2);
	}
	
	public static void dealFlop(String str){
		//System.out.println("DEAL FLOPTURNORRIVER "+str);
		String cards = str.substring((str.indexOf("[")+1),str.lastIndexOf("]"));
		cards = cards.replace("[","");
		cards = cards.replace("]","");
		String f1, f2, f3;
		f1 = f2 = f3 = "";
		StringTokenizer st = new StringTokenizer(cards);
		if(st.hasMoreTokens())
			f1 = st.nextToken();
		if(st.hasMoreTokens())
			f2 = st.nextToken();
		if(st.hasMoreTokens())
			f3 = st.nextToken();
		if(!f1.equals("")&&!f1.equals("")&&!f1.equals(""))
			game.DealFlop(f1, f2, f3);

	}
	
	public static void dealTurn(String str){
		//System.out.println("DEAL FLOPTURNORRIVER "+str);
		String cards = str.substring((str.indexOf("[")+1),str.lastIndexOf("]"));
		cards = cards.replace("[","");
		cards = cards.replace("]","");
		String f1, f2, f3, t, r;
		f1 = f2 = f3 = t = r = "";
		StringTokenizer st = new StringTokenizer(cards);
		if(st.hasMoreTokens())
			f1 = st.nextToken();
		if(st.hasMoreTokens())
			f2 = st.nextToken();
		if(st.hasMoreTokens())
			f3 = st.nextToken();
		if(st.hasMoreTokens())
			t = st.nextToken();
		if(st.hasMoreTokens())
			r = st.nextToken();
		game.DealTurn(t);

	}
	
	public static void dealRiver(String str){
		//System.out.println("DEAL FLOPTURNORRIVER "+str);
		String cards = str.substring((str.indexOf("[")+1),str.lastIndexOf("]"));
		cards = cards.replace("[","");
		cards = cards.replace("]","");
		String f1, f2, f3, t, r;
		f1 = f2 = f3 = t = r = "";
		StringTokenizer st = new StringTokenizer(cards);
		if(st.hasMoreTokens())
			f1 = st.nextToken();
		if(st.hasMoreTokens())
			f2 = st.nextToken();
		if(st.hasMoreTokens())
			f3 = st.nextToken();
		if(st.hasMoreTokens())
			t = st.nextToken();
		if(st.hasMoreTokens())
			r = st.nextToken();
		game.DealRiver(r);
	}
	
	
}
