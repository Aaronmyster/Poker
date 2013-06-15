import java.util.ArrayList;
/*
	This is all based off of the robopoker api...
	http://robopoker.org/about/api/
*/

public class Game {
	
	private Table table;					//Describes the table structure
	private ArrayList<Post> posts;		//a collection of Post events
	private Betting betting;				//The betting rounds
	private ArrayList<Card> community;	//Collection of Community cards
	private Card[] pocket;				//The two pocket cards
	private String myName;				//The name of the player at this hand

	public Game(String p_name){
		myName = p_name;
	}

	public void newHand(){
		table = new Table();
	}

	public void newHand(Strign pTableId){
		this.newHand();
		table.tableId = pTableId;
	}


}

//Class to describe the table structure
class Table {
	
	public int button;					//seat of the button player
	public ArrayList<Player> players;	//list of players
	public String tableId;				//Unique ID for the hand -optional

	public Table(){

	}



}

//Class to represent a Player
class Player {
	
	public String name;		//name of the player
	public int sit; 		//seat number of the player
	public int stack;		//stack amount for the player
	public int inStack;		//stack amount before delt

	public Player(String p_name, int p_sit, int p_stack, int p_inStack){
		name = p_name;
		sit = p_sit; 		
		stack = p_stack;
		inStack = p_inStack;
	}

}

//Holds information about antes and blind posts. Type of the post 
//appears in the type. Amount indicates the post amount
class Post {
	
	public int amount;		//indicates the post amount
	public Player player;	//the player that made the post
	public PostType type;	//the type of Post

}

enum PostType{
	ANTE, SMALLBLIND, BIGBLIND
}

//Holds the betting history round by round.
//Players actions appear in chronological order.
class Betting {

	public Round preflop;	//preflop betting round
	public Round flop;		//flop betting round
	public Round turn;		//turn betting round
	public Round river;		//river betting round

}

//Collection of Player's actions in chronological order
class Round {

	public RoundType name;				//the name of the round
	public ArrayList<Action> actions;	//collection of actions

}

enum RoundType{
	PREFLOP, FLOP, TURN, RIVER
}

//Action that a player performed
class Action{
	public int amount;		//indicates the amound of the action
	public Player player;	//the player that performed the action
	public ActionType type; //the type of action
}

enum ActionType{
	CHECK, CALL, FOLD, RAISE, BET
}



