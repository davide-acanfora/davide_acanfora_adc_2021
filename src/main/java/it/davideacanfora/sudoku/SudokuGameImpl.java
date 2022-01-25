package it.davideacanfora.sudoku;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.tomp2p.dht.FutureGet;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.FutureBootstrap;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.rpc.ObjectDataReply;
import net.tomp2p.storage.Data;

public class SudokuGameImpl implements SudokuGame
{	
	//variabile istanza del peer
	private Peer peer;
	private PeerDHT dht;
	private int peerID;
	
	//POSSIBILITà DI SETTARE MASTER PORT DA DOCKER?
	//IN TAL CASO: SFRUTTARE I SYSTEM ARGS DAL MAIN, PASSARLO POI AL COSTRUTTORE DI QUESTA CLASSE
	private int MASTER_PORT = 4000;
	
	private HashMap<String, JoinedGame> joined_games = new HashMap<String, JoinedGame>();
	
	//Costruttore principale
	public SudokuGameImpl(int peerID, String master_address, MessageListener listener) throws IOException {
		this.peerID = peerID;
		
		//inizializzazione peer e dht
		peer = new PeerBuilder(Number160.createHash(peerID)).ports(MASTER_PORT+this.peerID).start();
		dht = new PeerBuilderDHT(peer).start();
		
		FutureBootstrap fb = this.peer.bootstrap().inetAddress(InetAddress.getByName(master_address)).ports(MASTER_PORT).start();
		fb.awaitUninterruptibly();
		if(fb.isSuccess()) {
			peer.discover().peerAddress(fb.bootstrapTo().iterator().next()).start().awaitUninterruptibly();
		} else {
			//qualcosa è andato storto nel bootstrap
			throw new IOException("Eccezione durante la fase di bootstrap");
		}
		
		if (listener!=null)
			this.peer.objectDataReply(new ObjectDataReply() {
				public Object reply(PeerAddress sender, Object request) throws Exception {
					return listener.parseMessage(request);
				}
			});
	}
	
	//Costruttore privo di MessageListener, utile per la fase di test
	public SudokuGameImpl(int peerID, String master_address) throws IOException {
		this(peerID, master_address, null);
	}

	public Integer[][] generateNewSudoku(String _game_name) {
		GameState game = new GameState();
		
		try {
			FutureGet futureGet = dht.get(Number160.createHash(_game_name)).start();
			futureGet.awaitUninterruptibly();
			if (futureGet.isSuccess() && futureGet.isEmpty()) { 
				dht.put(Number160.createHash(_game_name)).data(new Data(game)).start().awaitUninterruptibly();
				return game.getInitialMatrix();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean join(String _game_name, String _nickname) {
		try {
			//ottieni lo stato della partita da dht
			FutureGet futureGet = dht.get(Number160.createHash(_game_name)).start().awaitUninterruptibly();
			//verifica se esiste
			if (futureGet.isSuccess() && !futureGet.isEmpty()) { 
				GameState game = (GameState) futureGet.dataMap().values().iterator().next().object();
				//verifica se sto già partecipando a questa partita (tramite peerID)
				//verifica se qualcuno sta già usando il mio nickname
				if (game.isPeerIDPresent(this.peerID) || game.isNicknamePresent(_nickname)) return false;
				
				//mi aggiungo alla lista dei player della partita
				game.addPlayer(new Player(_nickname, game.getInitialMatrix(), this.peer.peerAddress(), this.peerID));
				
				//aggiorno lo stato della partita per aggiornare la sua nuova lista dei partecipanti
				dht.put(Number160.createHash(_game_name)).data(new Data(game)).start().awaitUninterruptibly();
				
				//aggiungo questa partita ai games a cui partecipo
				if (!joined_games.containsKey(_game_name))
					//this.joined_games.put(_game_name, new JoinedGame(_game_name, _nickname, game.getInitialMatrix()));
					this.joined_games.put(_game_name, new JoinedGame(_game_name, _nickname));
				else
					return false;
				
				//segnala agli altri giocatori il nostro ingresso
				for(Player p : game.getPlayers())
					if(!p.getNickname().equals(_nickname)) dht.peer().sendDirect(p.getPeerAddress()).object("["+_game_name+"] "+_nickname+" has joined the game").start().awaitUninterruptibly();
				
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public Integer[][] getSudoku(String _game_name) {
		try {
			//ottieni lo stato della partita da dht
			FutureGet futureGet = dht.get(Number160.createHash(_game_name)).start().awaitUninterruptibly();
			//verifica se esiste
			if (futureGet.isSuccess() && !futureGet.isEmpty()) {
				GameState game = (GameState) futureGet.dataMap().values().iterator().next().object();
				//ottieni la matrice di gioco associata al mio peerID
				Player player = game.getPlayerByPeerID(this.peerID);
				if(player!=null)
					return player.getMatrix();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Integer placeNumber(String _game_name, int _i, int _j, int _number) {
		try {
			FutureGet futureGet = dht.get(Number160.createHash(_game_name)).start().awaitUninterruptibly();
			if (futureGet.isSuccess() && !futureGet.isEmpty()) {
				GameState game = (GameState) futureGet.dataMap().values().iterator().next().object();
				Player player = game.getPlayerByPeerID(peerID);
				if(player==null) //se non sono nella lista dei partecipanti
					return 999;
				Integer[][] matrix = player.getMatrix();
				int score = 0;
				String message = "["+_game_name+"] " + player.getNickname();
				
				if(game.isCorrectNumber(_i, _j, _number)) {
					matrix[_i][_j] = _number;
					if(!game.isAlreadyPlaced(_i, _j)) {
						game.setPlaced(_i, _j);
						score = 1;
						message += " earned 1 point";
						
						if (game.isCompleted())
							message += " and completed the board!!!";
					}
				}
				else {
					score = -1;
					message += " lost 1 point";
				}
				
				//aggiorna lo stato della partita
				player.setScore(player.getScore()+score);
				dht.put(Number160.createHash(_game_name)).data(new Data(game)).start().awaitUninterruptibly();
				//manda un messaggio
				if (score != 0) {
					for(Player p : game.getPlayers())
						if(!p.equals(player)) dht.peer().sendDirect(p.getPeerAddress()).object(message).start().awaitUninterruptibly();
				}
				
				return score;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 999; //per segnalare un errore
	}
	
	private boolean leaveJoinedGame(JoinedGame joined_game) {
		try {
			//ottieni la partita da dht
			FutureGet futureGet = dht.get(Number160.createHash(joined_game.getGameName())).start().awaitUninterruptibly();
			//verifica se esiste
			if (futureGet.isSuccess() && !futureGet.isEmpty()) { 
				GameState game = (GameState) futureGet.dataMap().values().iterator().next().object();
				//verifica se sto già partecipando a questa partita e se sto utilizzando il nickname memorizzato
				if (game.isPeerIDPresent(this.peerID) && game.isNicknamePresent(joined_game.getNickname())) return false;
				
				//mi rimuovo dalla lista dei player partecipanti
				if (!game.removePlayerByPeerID(this.peerID)) return false;
				
				//aggiorno lo stato della partita per aggiornare la sua nuova lista dei partecipanti
				dht.put(Number160.createHash(joined_game.getGameName())).data(new Data(game)).start().awaitUninterruptibly();
				
				//rimuovo questa partita dai games a cui partecipo
				joined_games.remove(joined_game.getGameName());
				
				//segnala agli altri giocatori la nostra uscita
				for(Player p : game.getPlayers())
					if(!p.getNickname().equals(joined_game.getNickname())) dht.peer().sendDirect(p.getPeerAddress()).object("["+joined_game.getGameName()+"] "+joined_game.getNickname()+" has left the game").start().awaitUninterruptibly();
				
				return true;
			}
			else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean leaveAllJoinedGames() {
		boolean errorFlag = false;
		for(JoinedGame game: joined_games.values())
			//se trovo errori ad almeno ad una partita da cui provo ad uscire.. 
			if (!leaveJoinedGame(game)) errorFlag = true; //imposto il flag per segnalare che ci sono stati errori
		return !errorFlag;
	}
	
	public boolean leaveNetwork() {
		boolean isSuccessful = leaveAllJoinedGames();
		dht.peer().announceShutdown().start().awaitUninterruptibly();
		return isSuccessful && peer.shutdown().awaitUninterruptibly().isSuccess();
	}

	public boolean leave(String game_name) {
		for(JoinedGame joined_game: joined_games.values())
			if (joined_game.getGameName().equals(game_name)) return leaveJoinedGame(joined_game);
		return false;
	}

	public ArrayList<Player> getLeaderboard(String _game_name) {
		try {
			//ottieni lo stato della partita da dht
			FutureGet futureGet = dht.get(Number160.createHash(_game_name)).start().awaitUninterruptibly();
			//verifica se esiste
			if (futureGet.isSuccess() && !futureGet.isEmpty()) {
				ArrayList<Player> leaderboard = new ArrayList<>();
				
				GameState game = (GameState) futureGet.dataMap().values().iterator().next().object();
				Iterator<Player> iterator = game.getPlayers().iterator();
				while(iterator.hasNext()) {
					int pos=0;
					Player player = iterator.next();
					boolean flag = true;
					while (flag && pos<leaderboard.size()) {
						if (player.getScore()>leaderboard.get(pos).getScore())
							flag=false;
						else
							pos++;
					}
					leaderboard.add(pos, player);
				}
				return leaderboard;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
