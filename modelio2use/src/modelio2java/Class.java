package modelio2java;

import java.util.ArrayList;

public class Class {
	
	private String nome;
	private String g;
	private ArrayList<Attribute> atb = new ArrayList();
	public boolean isAssocClass = false;
	public int assoc;
	
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setGen(String g) {
		this.g = g;
	}
	
	public String getGen() {
		return g;
	}
	
	public ArrayList<Attribute> getAtb(){
		return atb;
	}
	
	public void addAtb(Attribute a) {
		atb.add(a);
	}

}
