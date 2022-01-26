package it.davideacanfora.sudoku;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;


public class SudokuGameImplTest {
	
	protected static SudokuGameImpl peer0, peer1, peer2;
	
	@BeforeAll
	public static void setup() throws IOException {
		//utilizziamo il costruttore senza MessageListener, in quanto non ci serve per i test
		peer0 = new SudokuGameImpl(0, "127.0.0.1");
		peer1 = new SudokuGameImpl(1, "127.0.0.1");
		peer2 = new SudokuGameImpl(2, "127.0.0.1");
	}
	
	@Test
	public void testCaseGenerateNewSudoku(TestInfo testInfo){
		assertNotNull(peer0.generateNewSudoku("partita"));
		assertNotNull(peer1.generateNewSudoku("torneo"));
		assertNotNull(peer2.generateNewSudoku("sudoku"));
	}
	
	@Test
	public void testCaseJoinGame(TestInfo testInfo){
		assertNotNull(peer0.generateNewSudoku("nuova"));
		assertTrue(peer0.join("nuova", "Alice"));
		assertTrue(peer1.join("nuova", "Bob"));
		assertTrue(peer2.join("nuova", "Davide"));
	}
	
	@Test
	public void testCaseJoinInexistentGame(TestInfo testInfo){
		assertFalse(peer0.join("inexistent", "Alice"));
		assertFalse(peer1.join("partitona", "Bob"));
		assertFalse(peer2.join("giochiamo", "Davide"));
	}
	
	@Test
	public void testCaseJoinDifferentPlayerSameNickname(TestInfo testInfo){
		assertNotNull(peer0.generateNewSudoku("test_nickname"));
		assertTrue(peer0.join("test_nickname", "Alice"));
		assertFalse(peer1.join("test_nickname", "Alice"));
	}
	
	@Test
	public void testCaseJoinSamePlayerDifferentNickname(TestInfo testInfo){
		assertNotNull(peer0.generateNewSudoku("test_player"));
		assertTrue(peer0.join("test_player", "Alice"));
		assertFalse(peer0.join("test_player", "Bob"));
	}
	
	@Test
	public void testCaseGetSudoku(TestInfo testInfo){
		assertNotNull(peer0.generateNewSudoku("test_get_sudoku"));
		assertTrue(peer0.join("test_get_sudoku", "Alice"));
		assertNotNull(peer0.getSudoku("test_get_sudoku"));
		assertNull(peer1.getSudoku("test_get_sudoku"));
	}
	
	
	@AfterEach
	public void leaveAllJoinedGames() {
		assertTrue(peer0.leaveAllJoinedGames());
		assertTrue(peer1.leaveAllJoinedGames());
		assertTrue(peer2.leaveAllJoinedGames());
	}
	
	@AfterAll
	public static void leaveNetwork() {
		assertTrue(peer0.leaveNetwork());
		assertTrue(peer1.leaveNetwork());
		assertTrue(peer2.leaveNetwork());
	}
}
