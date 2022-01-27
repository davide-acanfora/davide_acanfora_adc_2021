package it.davideacanfora.sudoku;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import org.beryx.textio.TextIO;
import org.beryx.textio.TextIoFactory;
import org.beryx.textio.TextTerminal;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class App {
	//Classe principale eseguita all'avvio del programma
	//Gestisce il menu dell'utente
	//Istanzia la classe Sudoku e la utilizza per tutte le operazioni
	
	@Option(name = "-m", aliases = "--master", usage = "the IP address of the master peer", required = false)
	private static String master_address = "127.0.0.1";
	
	@Option(name = "-i", aliases = "--id", usage = "the unique ID the peer will use", required = true)
	private static int peerID;
	
	private static TextTerminal<?> terminal;
	
	private static ArrayList<String> messages = new ArrayList<>();
	
	private final static String infoColor = "yellow";
	
	public static void main(String[] args) {
		CmdLineParser parser = new CmdLineParser(new App());
		TextIO textIO = TextIoFactory.getTextIO();
		terminal = textIO.getTextTerminal();
		terminal.getProperties().setPaneBackgroundColor("black");
		terminal.getProperties().setPromptBackgroundColor("black");
		terminal.getProperties().setPromptColor(Color.white);
		terminal.getProperties().setInputColor(Color.blue);
		
		try {
			parser.parseArgument(args);			
			
			int choice = 0;
			
			SudokuGameImpl sudoku = new SudokuGameImpl(peerID, master_address, new MessageListenerImpl(messages));
			
			printInfo("______  _____ ______   _____           _       _          \r\n"
					+ "| ___ \\/ __  \\| ___ \\ /  ___|         | |     | |         \r\n"
					+ "| |_/ /`' / /'| |_/ / \\ `--. _   _  __| | ___ | | ___   _ \r\n"
					+ "|  __/   / /  |  __/   `--. \\ | | |/ _` |/ _ \\| |/ / | | |\r\n"
					+ "| |    ./ /___| |     /\\__/ / |_| | (_| | (_) |   <| |_| |\r\n"
					+ "\\_|    \\_____/\\_|     \\____/ \\__,_|\\__,_|\\___/|_|\\_\\\\__,_|\r\n", infoColor);
			
			String last_game = "sudoku";
			
			while (choice != 8) {
				//Stampa del menu
				printMenu();
				
				choice = textIO.newIntInputReader().withMinVal(1).withMaxVal(8).read("Choice");
				String game_name = last_game;
				
				switch (choice) {
				case 1:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					Integer[][] new_game = sudoku.generateNewSudoku(game_name);
					if (new_game != null) {
						printInfo("New game \""+game_name+"\" created successfully, its initial matrix is:", infoColor);
						printMatrix(new_game);
						printInfo("Remember to join it first!", infoColor);
					}
					else
						printError("Something went wrong: maybe game already created with the same name?\n");
					break;
					
				case 2:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					String nickname = textIO.newStringInputReader().withDefaultValue("Player").read("Nickname");
					if (sudoku.join(game_name, nickname)) {
						printInfo("Joined game \""+game_name+"\" successfully, here is your initial matrix:", infoColor);
						printMatrix(sudoku.getSudoku(game_name));
					}
					else
						printError("Something went wrong: game inexistent, already joined or nickname already in use!\n");
					break;
					
				case 3:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					Integer[][] matrix_to_print = sudoku.getSudoku(game_name);
					if (matrix_to_print != null)
						printMatrix(matrix_to_print);
					else
						printError("Something went wrong: game inexistent or not joined yet!\n");
					break;
					
				case 4:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					Integer row = textIO.newIntInputReader().withMinVal(1).withMaxVal(9).read("Row");
					Integer column = textIO.newIntInputReader().withMinVal(1).withMaxVal(9).read("Column");
					Integer number = textIO.newIntInputReader().withMinVal(1).withMaxVal(9).read("Number");
					Integer score = sudoku.placeNumber(game_name, row-1, column-1, number);
					switch (score){
					case -1:
						printInfo("Wrong number! You lost a point.\n", "red");
						break;
					case 0:
						printInfo("The number was correct, but someone has already placed it.\n", infoColor);
						break;
					case 1:
						printInfo("You earned a point, let's go!\n", "green");
						break;
					default: //999
						printError("Something went wrong: game not joined yet or inexistent!\n");
						break;
					}
					break;
					
				case 5:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					if (sudoku.leave(game_name))
						printInfo("Game \""+game_name+"\" left successfully\n", infoColor);
					else
						printError("Something went wrong: game not joined yet or inexistent!\n");
					break;
				
				case 6:
					game_name = textIO.newStringInputReader().withDefaultValue(last_game).read("Game name");
					ArrayList<Player> leaderboard = sudoku.getLeaderboard(game_name);
					if (leaderboard != null) {
						if(leaderboard.size()>0) {
							printInfo("-----LEADERBOARD-----", infoColor);
							for(Player p : leaderboard)
								printInfo(p.getNickname() + ": " + p.getScore(), infoColor);
							printInfo("---------------------\n", infoColor);
						}
						else
							printInfo("Leaderboard is empty: no one is playing right now.\n", infoColor);
					}
					else
						printError("Something went wrong: game inexistent.\n");
					break;
					
				case 7:
					if(messages.size()>0) {
						printInfo("------------MESSAGES------------", infoColor);
						for(String message : messages)
							printInfo(message, infoColor);
						printInfo("--------------------------------", infoColor);
					}
					else
						printInfo("There are no messages yet.", infoColor);
					terminal.println();
					break;
					
				case 8:
					sudoku.leaveNetwork();
					break;
					
				default:
					break;
				}
				
				last_game = game_name;
			}
			
		} catch (CmdLineException e) {
			//errore parsing argomenti
			e.printStackTrace();
		} catch (IOException e) {
			//errore bootstrap P2P
			e.printStackTrace();
		} finally {
			terminal.abort();
			System.exit(0);
		}
		
	}
	
	private static void printMenu() {
		terminal.printf("1 - Create a new game\n");
		terminal.printf("2 - Join game\n");
		terminal.printf("3 - Print your board by game name\n");
		terminal.printf("4 - Place a number\n");
		terminal.printf("5 - Leave a game\n");
		terminal.printf("6 - Print the leaderboard of a game\n");
		terminal.printf("7 - Print messages (count: "+messages.size()+")\n");
		terminal.printf("8 - Exit\n");
	}

	private static void printMatrix(Integer[][] matrix) {
		terminal.println("    1 2 3   4 5 6   7 8 9");
		for(int i=0; i<9; i++) {
			if(i%3 == 0) terminal.println("  o-------o-------o-------o");
			terminal.printf("%d ", i+1);
			for(int j=0; j<9; j++) {
				if(j%3 == 0) terminal.print("| ");
				if (matrix[i][j] == 0)
					terminal.print("  ");
				else
					terminal.printf("%d ", matrix[i][j]);
			}
			terminal.println("|");
		}
		terminal.println("  o-------o-------o-------o\n");		
	}
	
	private static void printError(String message) {
		printInfo(message, "red");
	}
	
	private static void printInfo(String message, String color) {
		terminal.executeWithPropertiesConfigurator(
				props -> props.setPromptColor(color),
		        t -> t.println(message)
		);
	}
}
