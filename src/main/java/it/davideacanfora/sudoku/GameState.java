package it.davideacanfora.sudoku;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameState implements Serializable {
	private static final long serialVersionUID = 1L;

	//matrice contenente la soluzione completa della partita
	private Integer[][] solution;
	
	//la matrice iniziale proposta ai partecipanti
	private Integer[][] initial;
	
	//matrice che mantiene lo stato di ogni cella per verificare se qualcuno ha già inserito il numero giusto in essa
	private boolean[][] placed;
	
	//lista dei giocatori/partecipanti (peers) alla partita
	private HashSet<Player> players; //ognuno inserirà il proprio con players.add(PLAYER) al momento della join
	
	public GameState() {
		//genero la soluzione finale
		this.solution = generateNewGame();
		//genero la matrice che sarà presentata ai giocatori ad inizio partita
		this.initial = generateInitialMatrix(solution, 40);
		//tengo traccia dei numeri rimasti nella matrice proposta (poiché già inseriti)
		this.placed = new boolean[9][9];
		
		for (int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				this.placed[i][j] = initial[i][j] != 0; 
		
		players = new HashSet<Player>();
	}
	
	private Integer[][] generateNewGame() {
		Random random = new Random();
		Integer[][] game = new Integer[9][9];
		
		//generiamo la prima riga casuale della board
		Integer[] riga = {1,2,3,4,5,6,7,8,9};
		List<Integer> lista = Arrays.asList(riga);
		Collections.shuffle(lista);
		lista.toArray(riga);
		
		//la repliciamo (shiftandola) in tutte le altre righe per creare una soluzione valida
		game[0] = riga;
		game[1] = shift(game[0], 3);
		game[2] = shift(game[1], 3);
		game[3] = shift(game[2], 1);
		game[4] = shift(game[3], 3);
		game[5] = shift(game[4], 3);
		game[6] = shift(game[5], 1);
		game[7] = shift(game[6], 3);
		game[8] = shift(game[7], 3);
		
		//scambiamo di posto randomicamente le righe e le colonne per non far identificare il pattern
		//righe
		for (int i=0; i<9; i+=3) {
			if (random.nextBoolean()) swapMatrixRow(game, i, i+1);
			if (random.nextBoolean()) swapMatrixRow(game, i, i+2);
			if (random.nextBoolean()) swapMatrixRow(game, i+1, i+2);
		}
		
		//colonne
		for (int i=0; i<9; i+=3) {
			if (random.nextBoolean()) swapMatrixColumn(game, i, i+1);
			if (random.nextBoolean()) swapMatrixColumn(game, i, i+2);
			if (random.nextBoolean()) swapMatrixColumn(game, i+1, i+2);
		}
		
		return game;		
	}
	
	private Integer[] shift(Integer[] input, int times) {
		List<Integer> lista = Arrays.asList(input.clone());
		Collections.rotate(lista, times);
		Integer[] output = new Integer[9];
		return lista.toArray(output);
	}
	
	private void swapMatrixRow(Integer[][] matrix, int oldPos, int newPos) {
		Integer[] temp = matrix[oldPos];
		matrix[oldPos] = matrix[newPos];
		matrix[newPos] = temp;
	}
	
	private void swapMatrixColumn(Integer[][] matrix, int oldPos, int newPos) {
		for(int i=0; i<9; i++) {
			int temp = matrix[i][oldPos];
			matrix[i][oldPos] = matrix[i][newPos];
			matrix[i][newPos] = temp;
		}
	}
	
	private Integer[][] generateInitialMatrix(Integer[][] matrix, int N) {
		Integer[][] temp = new Integer[9][9];
		for(int i=0; i<9; i++)
			temp[i] = matrix[i].clone(); 
		
		Random random = new Random();
		
		for(int i=0; i<N; i++) {
			int row = random.nextInt(9);
			int column = random.nextInt(9);
			if (temp[row][column] != 0)
				temp[row][column] = 0;
			else
				i--;
		}
		
		return temp;
	}
	
	public Integer[][] getInitialMatrix(){
		Integer[][] temp = new Integer[9][9];
		for(int i=0; i<9; i++)
			temp[i] = this.initial[i].clone();
		return temp;
	}
	
	public boolean isPeerIDPresent(Integer peerID) {
		Iterator<Player> iterator = players.iterator();
		while(iterator.hasNext())
			if (iterator.next().getPeerID().equals(peerID))
				return true; 
		return false;
	}
	
	public boolean isNicknamePresent(String nickname) {
		Iterator<Player> iterator = players.iterator();
		while(iterator.hasNext())
			if (iterator.next().getNickname().equals(nickname))
				return true;
		return false;
	}
	
	public void addPlayer(Player player) {
		players.add(player);
	}

	public Player getPlayerByPeerID(int peerID) {
		Iterator<Player> iterator = players.iterator();
		while(iterator.hasNext()) {
			Player player = iterator.next();
			if (player.getPeerID().equals(peerID))
				return player; 
		}
		return null;
	}

	public HashSet<Player> getPlayers() {
		return players;
	}

	public void setPlayers(HashSet<Player> players) {
		this.players = players;
	}

	public boolean removePlayerByPeerID(int peerID) {
		Iterator<Player> iterator = players.iterator();
		while(iterator.hasNext()) {
			Player player = iterator.next();
			if (player.getPeerID().equals(peerID))
				return players.remove(player);
		}
		return false;
	}
	
	public boolean isCorrectNumber(int _i, int _j, int _number) {
		return this.solution[_i][_j].equals(_number);
	}
	
	public boolean isAlreadyPlaced(int _i, int _j) {
		return this.placed[_i][_j];
	}

	public void setPlaced(int _i, int _j) {
		this.placed[_i][_j] = true;		
	}

	public boolean isCompleted() {
		for (int i=0; i<9; i++)
			for(int j=0; j<9; j++)
				if (!this.placed[i][j]) return false;
		return true;
	}
}
