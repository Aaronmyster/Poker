import java.util.HashMap;

/* --Poker Logic Class A.k.a Polo--
 * 
 * Michael Roach
 * 
 * This contains all of the logic for the poker game
 * This class is used to interpret hand history files.
 * This class is also used by the PokerInterface, which
 * uses this class during a game.
 * 
 */

public class Polo {
	
	private boolean DEBUG = false;
	
	//--ATTRIBUTES--
	//*Pot Odds - (amountToCall/PotSize)
	//*Turn
	//*Table Position
	//*Big Blinds Left (if he calls)
	//*Aggression
	//*Number Of Players Remaining in Hand
	//*Hand Rank
	
	//--Poker table state variables-- 
	//(used to translate the actual state of the table into an instance)
	public  String defaultPlayer;
	public int numberOfPlayers;
	public int[] amountsPlayersAreIn_Bet;
	public int[] amountsPlayersAreIn_Hand;
	public int[] startingAmounts;
	public String[] playersInSeats;
	public HashMap<String, Integer> playerToSeat = new HashMap<String, Integer>();
	public int turn;
	public int smallBlind = 0;
	public int bigBlind = 0;
	public int chipStack;
	
	//Aggression 
	public HashMap<String, int[]> aggressionMap = new HashMap<String, int[]>();
	public boolean[] stillInHand;
	
	
	//Hand Evaluation
	public HandEvaluator handEvaluator = new HandEvaluator();
	public String c1;
	public String c2;
	public String f1;
	public String f2;
	public String f3;
	public String t;
	public String r;
	
	//Counters for decisions
	public int numberOfHands = 0;
	public int numberOfDecisions = 0;
	public int numberOfFoldsOrChecks = 0;
	public int numberOfCalls = 0;
	public int numberOfBetsOrRaises = 0;

	public String handID;
	
	public Polo(String playerName){
		defaultPlayer = playerName;
		numberOfPlayers = 1;
		playersInSeats = new String[1];
		stillInHand = new boolean[1];
		amountsPlayersAreIn_Bet = new int[1];
		amountsPlayersAreIn_Hand = new int[1];		
		playersInSeats[0] = defaultPlayer;
		stillInHand[0] = false;
		resetSeatHash();
		newHand();
		
	}
	
	public Polo(String playerName,int i){
		defaultPlayer = playerName;
		numberOfPlayers = 0;
		playersInSeats = new String[0];
		stillInHand = new boolean[0];
		amountsPlayersAreIn_Bet = new int[0];
		amountsPlayersAreIn_Hand = new int[0];
		resetSeatHash();
	}
	
	/**
	 * Starts a new hand and rotates positions of the seats
	 */
	public void newHand(){
		numberOfHands++;
		amountsPlayersAreIn_Bet = new int[numberOfPlayers];
		amountsPlayersAreIn_Hand = new int[numberOfPlayers];
		startingAmounts = new int[numberOfPlayers];
		rotatePositions();
		turn = 0;
		c1 = c2 = f1 = f2 = f3 = t = r = "";
	}
	
	/**
	 * Starts a new hand and clears all of the player/seat information
	 */
	public void newHand_clearPlayers(){
		if(DEBUG)
			System.out.println("--NEW HAND CLEAR--");
		numberOfHands++;
		numberOfPlayers = 0;
		amountsPlayersAreIn_Bet = new int[0];
		amountsPlayersAreIn_Hand = new int[0];
		startingAmounts = new int[0];
		playersInSeats = new String[0];
		stillInHand = new boolean[0];
		turn = 0;
		c1 = c2 = f1 = f2 = f3 = t = r = "";
		
	}
	
	public void resetSeatHash(){
		playerToSeat = new HashMap<String, Integer>();
		for(int n=0;n<playersInSeats.length;n++){
			playerToSeat.put(playersInSeats[n],n);
		}
	}
	
	/**
	 * Rotates the positions
	 * 
	 * All the players move down one on the list (-1), and index 0 goes to the end.
	 * i.e. Dealer is now in late position.
	 */
	public void rotatePositions(){
		String[] oldPlayersInSeats = playersInSeats;
		playersInSeats = new String[numberOfPlayers];
		stillInHand = new boolean[numberOfPlayers];
		String firstPosition = oldPlayersInSeats[0];
		for(int i=0;i<playersInSeats.length;i++){
			stillInHand[i] = true;
			if(i==playersInSeats.length-1){
				playersInSeats[i] = firstPosition;
			}else{
				playersInSeats[i] = oldPlayersInSeats[i+1];
			}
		}
		resetSeatHash();
	}
	
	/**
	 * Rotates positions, until the given seat number is the dealer.
	 * This assumes that the lowest seat number is 1.
	 * @param button
	 */
	public void setButtonToSeat(int button){
		String dealer = playerInSeat(Math.min((numberOfPlayers-1),(button-1)));
		while(!playerInSeat(0).equals(dealer)){
			rotatePositions();
		}
			
	}
	
	/**
	 * Starts a new betting round
	 * 
	 * Starts a new betting round for flop, turn and river.
	 * Empties the amountsPlayersAreIn_Bet array and adds it to amountsPlayersAreIn_Bet
	 */
	public void newBettingRound(){
		turn++;
		for(int i=0;i<amountsPlayersAreIn_Bet.length;i++){
			amountsPlayersAreIn_Hand[i]+=amountsPlayersAreIn_Bet[i];
		}
		amountsPlayersAreIn_Bet = new int[numberOfPlayers];
	}	
	
	public void raise(int amount) throws Exception{
		raise(defaultPlayer,amount);
	}
	
	public void raise(String name, int amount) throws Exception{
		if(!stillInHand(name))
			throw new Exception("Player "+name+" is no longer in hand");
		if(name.equals(defaultPlayer))
			chipStack-=amount;
		addBetAmountToPlayer(name, amount);
		addAggression(name);
	}
	
	public void bets(int amount) throws Exception{
		bets(defaultPlayer,amount);
	}
	
	public void bets(String name, int amount) throws Exception{
		if(!stillInHand(name))
			throw new Exception("Player "+name+" is no longer in hand");
		if(name.equals(defaultPlayer))
			chipStack-=(amount-amountPlayerIsIn_Bet(defaultPlayer));
		setBetAmountOfPlayer(name, amount);
		addAggression(name);
	}
	
	public int call(){
		return call(defaultPlayer);
	}
	
	public int call(String name){
		int amount = getAmountToCall(name);
		if(name.equals(defaultPlayer))
			chipStack-=amount;
		addBetAmountToPlayer(name, amount);
		return amount;
		
	}
	
	/**
	 * Default player wins
	 * 
	 * adds pot size to default player chipstack
	 */
	public void win(){
		chipStack += getPotSize();
	}
	
	public void DealHand(String c1_in, String c2_in){
		c1 = c1_in;
		c2 = c2_in;
		//handRank = HandEvaluator.rankHand(new Hand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r));
		//System.out.println(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r);
	}

	public void DealFlop(String f1_in, String f2_in, String f3_in){
		f1 = f1_in;
		f2 = f2_in;
		f3 = f3_in;
		//handRank = HandEvaluator.rankHand(new Hand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r));
		//System.out.println(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r);
		newBettingRound();
	}

	public void DealTurn(String t_in){
		t = t_in;
		//handRank = HandEvaluator.rankHand(new Hand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r));
		//System.out.println(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r);
		newBettingRound();
	}

	public void DealRiver(String r_in){
		r = r_in;
		//handRank = HandEvaluator.rankHand(new Hand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r));
		//System.out.println(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r);
		newBettingRound();
	}
	
	public int amountPlayerIsIn_Bet(String name){
		return this.amountsPlayersAreIn_Bet[seatOfPlayer(name)];
	}
	
	public int amountPlayerIsIn_Hand(String name){
		return this.amountsPlayersAreIn_Hand[seatOfPlayer(name)];
	}

	public void addBetAmountToPlayer(int amount){
		amountsPlayersAreIn_Bet[seatOfPlayer(defaultPlayer)]+=amount;
	}
	
	public void addBetAmountToPlayer(String name, int amount){
		if(DEBUG){
			for(int i=0;i<numberOfPlayers;i++){
				System.out.println(i+". "+playersInSeats[i]);
			}
			System.out.println(name);
		}
		amountsPlayersAreIn_Bet[seatOfPlayer(name)]+=amount;
	}
	
	public void addBetAmountToPlayerInSeat(int seat, int amount){
		amountsPlayersAreIn_Bet[seat]+=amount;
	}
	
	public void subtractBetAmountFromPlayer(int amount){
		amountsPlayersAreIn_Bet[seatOfPlayer(defaultPlayer)]-=amount;
	}
	
	public void subtractBetAmountFromPlayer(String name, int amount){
		amountsPlayersAreIn_Bet[seatOfPlayer(name)]-=amount;
	}
	
	public void subtractBetAmountFromPlayerInSeat(int seat, int amount){
		amountsPlayersAreIn_Bet[seat]-=amount;
	}
	
	public void setBetAmountOfPlayer(int amount){
		amountsPlayersAreIn_Bet[seatOfPlayer(defaultPlayer)]=amount;
	}
	
	public void setBetAmountOfPlayer(String name, int amount){
		amountsPlayersAreIn_Bet[seatOfPlayer(name)]=amount;
	}
	
	public void setBetAmountOfPlayerInSeat(int seat, int amount){
		amountsPlayersAreIn_Bet[seat]=amount;
	}
	
	public void addAggression(String name){
		String p = playersInSeats[seatOfPlayer(name)];
		int[] i;
		int[] n = {1,1};
		if(aggressionMap.containsKey(p)){
			i = aggressionMap.get(p);
			i[0]++;
			i[1]++;
			aggressionMap.remove(p);
			aggressionMap.put(p,i);
		}else{
			aggressionMap.put(p,n);
		}
	}
	
	public void addPassive(String name){
		String p = name;
		int[] i;
		int[] n = {0,1};
		if(aggressionMap.containsKey(p)){
			i = aggressionMap.get(p);
			i[1]++;
			aggressionMap.remove(p);
			aggressionMap.put(p,i);
		}else{
			aggressionMap.put(p,n);
		}
	}
	
	public int removePlayer(String name) throws Exception{
		int seat = seatOfPlayer(name);
		if(seat != -1){
			//throw new Exception("Player "+name+" does not exist.");
		numberOfPlayers--;
		String[] oldPlayersInSeats = playersInSeats;
		int[] oldAmountsPlayersAreIn_Hand = amountsPlayersAreIn_Hand;
		int[] oldAmountsPlayersAreIn_Bet = amountsPlayersAreIn_Bet;
		boolean[] oldStillInHand = stillInHand;
		playersInSeats = new String[numberOfPlayers];
		stillInHand = new boolean[numberOfPlayers];
		amountsPlayersAreIn_Hand = new int[numberOfPlayers];
		amountsPlayersAreIn_Bet = new int[numberOfPlayers];
		
		for(int i=0;i<seat;i++){					
			amountsPlayersAreIn_Hand[i] = oldAmountsPlayersAreIn_Hand[i];
			amountsPlayersAreIn_Bet[i] = oldAmountsPlayersAreIn_Bet[i];
			playersInSeats[i] = oldPlayersInSeats[i];
			stillInHand[i] = oldStillInHand[i];
		}
		for(int i=seat;i<numberOfPlayers;i++){
			amountsPlayersAreIn_Hand[i] = oldAmountsPlayersAreIn_Hand[i+1];
			amountsPlayersAreIn_Bet[i] = oldAmountsPlayersAreIn_Bet[i+1];
			playersInSeats[i] = oldPlayersInSeats[i+1];
			stillInHand[i] = oldStillInHand[i+1];
		}
		resetSeatHash();
		}
		return seat;
	}
	
	public void addPlayer(String name){
		
		String[] oldPlayersInSeats = playersInSeats;
		int[] oldAmountsPlayersAreIn_Hand = amountsPlayersAreIn_Hand;
		int[] oldAmountsPlayersAreIn_Bet = amountsPlayersAreIn_Bet;
		boolean[] oldStillInHand = stillInHand;
		
		numberOfPlayers++;
		playersInSeats = new String[numberOfPlayers];
		stillInHand = new boolean[numberOfPlayers];
		amountsPlayersAreIn_Hand = new int[numberOfPlayers];
		amountsPlayersAreIn_Bet = new int[numberOfPlayers];
		for(int i=0;i<oldPlayersInSeats.length;i++){
			amountsPlayersAreIn_Hand[i] = oldAmountsPlayersAreIn_Hand[i];
			amountsPlayersAreIn_Bet[i] = oldAmountsPlayersAreIn_Bet[i];
			playersInSeats[i] = oldPlayersInSeats[i];
			stillInHand[i] = oldStillInHand[i];
		}
		playersInSeats[numberOfPlayers-1] = name;
		stillInHand[numberOfPlayers-1] = true;
		if(DEBUG){
			for(int i=0;i<numberOfPlayers;i++){
				System.out.println(i+". "+playersInSeats[i]);
			}
			System.out.println(name);
		}
		resetSeatHash();
	}
	/**
	 * Adds a player to a specific seat, and rotates the rest counter clockwise
	 * 
	 * @param name the name of the player you want to add
	 * @param seat the seat that you want to add the player to
	 */
	public void addPlayerToSeat(String name,int seat){
		String[] oldPlayersInSeats = playersInSeats;
		int[] oldAmountsPlayersAreIn_Hand = amountsPlayersAreIn_Hand;
		int[] oldAmountsPlayersAreIn_Bet = amountsPlayersAreIn_Bet;
		boolean[] oldStillInHand = stillInHand;
		
		numberOfPlayers++;
		playersInSeats = new String[numberOfPlayers];
		stillInHand = new boolean[numberOfPlayers];
		amountsPlayersAreIn_Hand = new int[numberOfPlayers];
		amountsPlayersAreIn_Bet = new int[numberOfPlayers];
		
		for(int i=0;i<seat;i++){					
			amountsPlayersAreIn_Hand[i] = oldAmountsPlayersAreIn_Hand[i];
			amountsPlayersAreIn_Bet[i] = oldAmountsPlayersAreIn_Bet[i];
			playersInSeats[i] = oldPlayersInSeats[i];
			stillInHand[i] = oldStillInHand[i];
		}
		playersInSeats[seat] = name;
		stillInHand[seat] = true;
		for(int i=seat+1;i<numberOfPlayers;i++){
			amountsPlayersAreIn_Hand[i] = oldAmountsPlayersAreIn_Hand[i-1];
			amountsPlayersAreIn_Bet[i] = oldAmountsPlayersAreIn_Bet[i-1];
			playersInSeats[i] = oldPlayersInSeats[i-1];
			stillInHand[i] = oldStillInHand[i-1];
		}
		resetSeatHash();
		
	}
	
	/**
	 * Return what seat a player is in
	 * @param name the name of the player whos seat you want.
	 * @return returns the seat.
	 */	
	public int seatOfPlayer(String name){
		if(playerToSeat.containsKey(name))
			return (int)playerToSeat.get(name);
		else
			return -1;
	}
	
	public String playerInSeat(int seat){
		return playersInSeats[seat];
	}
	
	public String bigBlindPlayer(){
		if(numberOfPlayers<3)
			return playersInSeats[0];
		else
			return playersInSeats[2];
	}
	
	public String smallBlindPlayer(){
		return playersInSeats[1];
	}

	public boolean stillInHand(String name){
		return stillInHand[seatOfPlayer(name)];
	}

	public double getPotOdds(){
		return getPotOdds(defaultPlayer);
	}

	public double getPotOdds(String name){
		double potOdds = 0;
		int amountToCall = getAmountToCall(name);
		int potSize = getPotSize();
		if((potSize<=0)||amountToCall<=0){
			potOdds = 0;
		}else{
			potOdds = ((double)amountToCall)/((double)potSize);
		}
		return potOdds;
	}

	public int getAmountToCall(){
		return getAmountToCall(defaultPlayer);		
	}

	public int getAmountToCall(String name){
		int max=0;
		int amountToCall = 0;
		for(int i=0;i<amountsPlayersAreIn_Bet.length;i++){
			if(amountsPlayersAreIn_Bet[i]>max)
				max = amountsPlayersAreIn_Bet[i];
		}
		amountToCall = max-amountsPlayersAreIn_Bet[seatOfPlayer(name)];
		return amountToCall;
		
	}

	/**
	 * Returns the current pot size
	 * @return pot size
	 */
	public int getPotSize(){
		int i = 0;
		for(int x=0;x<amountsPlayersAreIn_Hand.length;x++){
			i+=amountsPlayersAreIn_Hand[x];
			i+=amountsPlayersAreIn_Bet[x];
		}
		return i;
	}

	public String getTurn(){
		if(turn==0){
			return "preflop";
		}else if(turn==1){
			return "flop";
		}else if(turn==2){
			return "turn";
		}else if(turn==3){
			return "river";
		}
		return "";
	}

	public int getTablePosition(){
		//This function assumes that the button is always in seat 0
		return seatOfPlayer(defaultPlayer);
	}
	
	public double getTablePositionRatio(){
		//This function assumes that the button is always in seat 0
		return ((double)seatOfPlayer(defaultPlayer))/(double)(numberOfPlayers-1);
	}

	public int getTablePosition(String name){
		//This function assumes that the button is always in seat 0
		return seatOfPlayer(name);		
	}

	public double getBigBlindsLeft(){
		return getBigBlindsLeft(defaultPlayer);
	}

	public double getBigBlindsLeft(String name){
		double d = 0;		
		if(bigBlind!=0){
			int i = seatOfPlayer(name);
			d = (chipStack-amountsPlayersAreIn_Hand[i]-amountsPlayersAreIn_Bet[i]-getAmountToCall(name))/bigBlind;
		}
		if(d>0)
			return d;
		else
			return 0;
	}

	public  double getAggressionOfRemainingPlayers(){
		double d=0;
		int a = 0;
		int t = 0;
		for(int i=0;i<stillInHand.length;i++){
			if((stillInHand[i])&&(aggressionMap.containsKey(playersInSeats[i]))){
				a+= aggressionMap.get(playersInSeats[i])[0];
				t+= aggressionMap.get(playersInSeats[i])[1];
			}
		}
		if(t!=0){
			d = (double)a/t;
			return d;
		}else{
			return 0;
		}
	}

	public int getNumberOfRemainingPlayers(){
		int x=0;
		for(int i=0;i<stillInHand.length;i++){
			if(stillInHand[i])
				x++;
		}
		return x;
	}

	public int getHandRank(){
		
		return HandEvaluator.rankHand(new Hand(c1+" "+c2+" "+f1+" "+f2+" "+f3+" "+t+" "+r)) + HandEvaluator.rankHand(new Hand(c1+" "+c2));
		
	}

	public String getState() throws Exception{

		String output = "";
		output = output.concat(handID+",");
		output = output.concat((getPotOdds()+","));
		output = output.concat((getTurn()+","));
		output = output.concat((getTablePositionRatio()+","));
		output = output.concat((getBigBlindsLeft()+","));
		output = output.concat((getAggressionOfRemainingPlayers()+","));
		output = output.concat((getNumberOfRemainingPlayers()+","));
		output = output.concat((getHandRank()+","));

		return output;
		
		
	}
}