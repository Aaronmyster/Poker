import java.util.ArrayList;
/*
	This is all based off of the robopoker api...
	http://robopoker.org/about/api/
*/

public class Game {
	
	public Table table;
	public ArrayList<Post> posts;
	public Betting betting;
	ArrayList<Card> community;

}

//Class to describe the table structure
class Table {
	
	public int button;			//seat of the button player
	public ArrayList<Player> players;	//list of players

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
	
	public int amount;
	public Player player;
	public PostType type;

}

enum PostType{
	ANTE, SMALLBLIND, BIGBLIND
}

//Holds the betting history round by round.
//Players actions appear in chronological order.
class Betting {

	public Round preflop;
	public Round flop;
	public Round turn;
	public Round river;

}

//Collection of Player's actions in chronological order
class Round {

	public RoundType name;
	public ArrayList<Action> actions;

}

enum RoundType{
	PREFLOP, FLOP, TURN, RIVER
}

class Action{
	public int amount;
	public Player player;
	public ActionType type;
}

enum ActionType{
	CHECK, CALL, FOLD, RAISE, BET
}



