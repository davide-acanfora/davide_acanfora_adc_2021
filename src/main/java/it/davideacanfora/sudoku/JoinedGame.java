package it.davideacanfora.sudoku;

public class JoinedGame {
	private String game_name;
	private String nickname;
	
	public JoinedGame(String game_name, String nickname) {
		this.game_name = game_name;
		this.nickname = nickname;
	}

	public String getGameName() {
		return game_name;
	}

	public void setGameName(String game_name) {
		this.game_name = game_name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
}
