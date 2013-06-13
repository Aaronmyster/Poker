public class Player {
	
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

