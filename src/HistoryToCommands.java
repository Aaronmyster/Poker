import java.util.regex.*;
import java.io.*;

//Converts PokerStars hand history to a list of commands to be read by CommandsToTrainingData

public class HistoryToCommands{

	public static String inputFileName;

	public static String NOMATCH = "--NOMATCH--";
	
	public static void main(String[] args){

		//Parameters are an input file name, and an output file name...
		checkParameters(args);
		
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
			System.exit(1);
		}		

	}

	public static void processFile(File inputFile) throws Exception{
		String str, s;
		int line = 0;

		try{

			//Open the file. Try to process it line by line...
			String fileName = inputFile.getName();

			System.err.println(fileName);

			System.out.println("--NEW_GAME--");
			BufferedReader in = new BufferedReader(new FileReader(inputFile));
			while (((str = in.readLine()) != null)){
				line++;
				s = processLine(str);
				if(!s.equals("")&&!s.equals(NOMATCH)) System.out.println(s+"||"+fileName+"||"+line);

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
		String out = NOMATCH;
		String tempOut = "";

		String[][] patternOutput = {{"PokerStars Game #(\\d*):.*Hold'em.*","NEWHAND||1"},
								{".*Seat #(\\d) is the button","SET_BUTTON||1",},
								{"Seat (\\d): (.*) \\(\\$?[\\d|\\.]* in chips\\)","ADD_PLAYER||2",},
								{"Seat (\\d): (.*) \\(\\$?[\\d|\\.]* in chips\\) out of hand \\(moved from another table into small blind\\)","ADD PLAYER||2",},
								{"Seat (\\d): (.*) \\(\\$?[\\d|\\.]* in chips\\) is sitting out","ADD_PLAYER||2",},
								{"(.*): posts the ante \\$?([\\d|\\.]*)","ANTE||1||2"},
								{"(.*): posts small blind \\$?([\\d|\\.]*)","SMALL_BLIND||1||2"},
								{"(.*): posts big blind \\$?([\\d|\\.]*)","BIG_BLIND||1||2"},
								{"(.*): posts small & big blinds \\$?([\\d|\\.]*)","SMALL_AND_BIG_BLINDS||1||2"},
								{"Dealt to (.*) \\[(.*)\\]","DEAL_CARDS||1||2"},
								{"(.*): calls \\$?([\\d|\\.]*.*)","CALL||1||2"},
								{"(.*): folds","FOLD||1"},
								{"(.*): raises \\$?([\\d|\\.]*) to \\$?([\\d|\\.]*.*)","RAISE||1||2||3"},
								{"(.*): bets \\$?([\\d|\\.]*.*)","BETS||1||2"},
								{"(.*): checks","CHECK||1"},
								{"(.*): mucks hand","MUCK||1"},
								{"(.*) leaves the table","LEAVE TABLE||1"},
								{"(.*) joins the table at seat #(\\d)","JOIN_TABLE||1||2"},
								{"(.*) collected \\$?([\\d|\\.]*) from pot","COLLECTED_FROM_POT||1||2"},
								{"(.*) collected \\$?([\\d|\\.]*) from main pot","COLLECTED_FROM_POT||1||2"},
								{"(.*) collected \\$?([\\d|\\.]*) from side pot[\\-\\d]?","COLLECTED_FROM_SIDE_POT||1||2"},
								{"(.*) collected \\$?([\\d|\\.]*) from side pot-\\d","COLLECTED_FROM_SIDE_POT||1||2"},
								{"\\*{3} FLOP \\*{3} \\[(.*)\\]","FLOP||1"},
								{"\\*{3} TURN \\*{3} \\[(.*)\\] \\[(.*)\\]","TURN||2"},
								{"\\*{3} RIVER \\*{3} \\[(.*)\\] \\[(.*)\\]","RIVER||2"},
								{"\\*{3} HOLE CARDS \\*{3}",""},
								{"Uncalled bet .*",""},
								{".* doesn't show hand",""},
								{".* shows \\[.*",""},
								{"\\*{3} SUMMARY \\*{3}",""},
								{"\\*{3} SHOW DOWN \\*{3}",""},
								{"Total pot.*",""},
								{"Board \\[.*\\]",""},
								{".* folded on the.*",""},
								{".* folded before.*",""},
								{".* mucked \\[.*",""},
								{".* said, \".*",""},
								{".* showed \\[.*\\] and.*",""},
								{".* will be allowed to play after the button",""},
								{".* is disconnected",""},
								{".* collected \\(\\$?[\\d|\\.]*\\)",""},
								{".* has timed out.*",""},
								{".* was removed from the table for failing to post",""},
								{".* has returned",""},
								{".* is connected",""},
								{".* re-buys and receives .*",""},
								{".* sits out",""},
								{"Betting is capped",""},
								{"",""}


								};

		int matchingPattern = -1;

		for(int i=0;i<patternOutput.length;i++){
			
			tempOut = outputIfPatternMatches(input,patternOutput[i][0],patternOutput[i][1]);

			
			if(!tempOut.equals(NOMATCH)){
				if(out.equals(NOMATCH)){
					out = tempOut;
					matchingPattern = i;
				}else{
					throw new Exception("ERROR: Patterns "+i+ " and "+matchingPattern+" match: '"+input+"'");
				}
			}

		}
		
		if(out.equals(NOMATCH)) System.err.println("ERROR: No pattern match: "+input);

		return out;

	}

	//Returns the output string if the input matches the pattern.
	public static String outputIfPatternMatches(String input, String pattern, String output){
		
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(input.trim());
		
		if(m.matches()){

			//Replace the groups in the output with groups from the input.
			for(int i=0;i<=m.groupCount();i++){
				output = output.replace("||"+i,"|| "+m.group(i));
			}
			
			return output;
		}else{
			return NOMATCH;
		}
	}

	public static void checkParameters(String[] args){
		try{
			inputFileName = args[0];
		}catch(Exception e){
			System.err.println("There's a problem with your parameters...");
			System.err.println("HistoryToCommands inputfile or inputdir");
			System.exit(1);
		}
	}

}