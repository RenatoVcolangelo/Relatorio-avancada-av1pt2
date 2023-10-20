package io.sim;

import org.python.modules.synchronize;

public class Conta {
	
	private double saldo;
	private int id;
	private String login;
	private String senha;
	
	public Conta(double _saldo, int id, String senha){
		this.saldo = _saldo;
		this.id = id;
		this.login = "Conta"+id;
		this.senha = senha;
	}

	public String getLogin(){
		return login;
	}

	public String getSenha(){
		return senha;
	}
	
	
	public  synchronized double  getSaldo() {
		return saldo;
	}

	public synchronized void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public int getId(){
		return id;
	}
	

		
}