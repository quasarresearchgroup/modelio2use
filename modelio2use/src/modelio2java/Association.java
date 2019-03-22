package modelio2java;

import java.util.ArrayList;

public class Association {
	private String nome;
	private String owned;
	private String owner;
	public String roleOwned;
	public String roleOwner;
	public boolean hasAssocClass = false;
	public String nomeAssocClass;
	private ArrayList<String> mult = new ArrayList();
	
	public ArrayList<String> getMult() {
		return mult;
	}
	
	public void addMul(String a) {
		mult.add(a);
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setOwned(String owned) {
		this.owned = owned;
	}
	
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getOwned() {
		return owned;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String toString() {
		return (nome + ":" + owned + ":" + owner);
	}
}
