package it.davideacanfora.sudoku;

import java.io.Serializable;

import net.tomp2p.peers.PeerAddress;

public class Player implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String nickname;
	private Integer[][] matrix;
	private Integer score;
	private PeerAddress peerAddress;
	private Integer peerID;
	
	public Player(String nickname, Integer[][] startingMatrix, PeerAddress peerAddress, Integer peerID) {
		this.nickname = nickname;
		this.matrix = startingMatrix;
		this.score = 0;
		this.peerAddress = peerAddress;
		this.peerID = peerID;
	}
	
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getScore() {
		return score;
	}
	public void setScore(Integer score) {
		this.score = score;
	}
	public PeerAddress getPeerAddress() {
		return peerAddress;
	}
	public void setPeerAddress(PeerAddress peerAddress) {
		this.peerAddress = peerAddress;
	}
	public Integer getPeerID() {
		return peerID;
	}
	public void setPeerID(Integer peerID) {
		this.peerID = peerID;
	}

	public Integer[][] getMatrix() {
		return matrix;
	}
}
