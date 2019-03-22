package modelio2java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class classGenerator {
	// Authors:
	// Rui Dias
	// Fernando Brito e Abreu
	//LOCALIZAÇÃO PROJETO
	private String loc = "C:\\Users\\dingus\\modelio\\workspace";
	//Nome do projeto
	private String nomeP = "BOA";
	private String target = "teste.use";
	private ArrayList<Class> classes = new ArrayList();
	private ArrayList<Association> assoc = new ArrayList();
	private ArrayList<Enumerate> enums = new ArrayList();
	
	public void start() {
		File folder = new File(loc + "\\" + nomeP + "\\data\\fragments\\"+nomeP+"\\model\\Standard.Class");
		File[] listOfFiles = folder.listFiles();
	    try {
	    	BufferedWriter writer = new BufferedWriter(new FileWriter(target));
			writer.write("model a");
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   

		for (File file : listOfFiles) {
		    if (file.isFile()) {
		        processClass(file.getName());
		    }
		}
		
		processEnumerate();
		
		clean();
		handleGen();
		normalize();
		handleAssocClass();
		writeClasses();
		writeAssoc();
	}
	
	//Ler ficheiro e processar a respetiva informação
	private void processClass(String file) {
		 try
		  {
		    BufferedReader reader = new BufferedReader(new FileReader(loc + "\\" + nomeP +"\\data\\fragments\\"+nomeP+"\\model\\Standard.Class\\"+ file));
		    String line;
		    Class c = new Class();
		    classes.add(c);
		    int init = 1;
		    int readingAssocClass = 0;
		    int readingA = 0;
		    String s;
		    Association a = null;
		    Attribute atb = null;
		    int mult = 0;
		    boolean readingAtb = false;
		    boolean readOwner = false;
		    boolean readAType = false;
		    boolean readRoleOwned = false;
		    while ((line = reader.readLine()) != null)
		    {
		    	if(init == 1) {
		    		if(line.split("ID name=\"").length>1) {
		    			s = line.split("ID name=\"")[1].split("\"")[0];
		    			c.setNome(s);
		    			init = 0;
		    		}
		    		
		    	}
		    	
		    	if(readingA == 1) {
		    		if(line.split("<ID name=\"").length>1 && !line.contains("<ID name=\"Generalization\" ") && readOwner && !readRoleOwned) {
			    		s = line.split("<ID name=\"")[1].split("\"")[0];
			    		a.roleOwned = s;
			    		if(s.isEmpty()) {
			    			a.roleOwned = a.getOwned() + assoc.indexOf(a);
			    		}
			    		readRoleOwned = true;
			    	}
		    		if(line.split("<ID name=\"").length>1 && !line.contains("<ID name=\"Generalization\" ") && !readOwner) {
			    		s = line.split("<ID name=\"")[1].split("\"")[0];
			    		a.setOwner(s);
			    		readOwner = true;
			    	}
		    		if(line.contains("<ID name=\"Generalization\" ")) {
			    		a.setNome("Generalization");
		    		}
		    		if(line.split("<ATT name=\"Aggregation\">").length>1 && !readAType) {
			    		s = line.split("<ATT name=\"Aggregation\">")[1].split("</ATT>")[0];
			    		a.setNome(s);
			    		readAType = true;
			    	}
		    		if(line.contains("<![CDATA[") && line.contains("Multiplicity")) {
		    			if(mult<4) {
		    				a.addMul(line.split("<!\\[CDATA\\[")[1].split("\\]\\]>")[0]);
		    				mult++;
		    			} else {
		    				mult = 0;
	    					readingA = 0;
		    			}
		    		}
		    		if(line.contains("LINK relation=\"ClassPart\"")) {
		    			readingAssocClass = 1;
		    		}
		    		if(readingAssocClass==1 && line.contains("<ID name=\"")) {
		    			a.hasAssocClass = true;
		    			a.nomeAssocClass = line.split("<ID name=\"")[1].split("\"")[0];
		    			readingAssocClass = 0;
		    		}
		    		if(line.contains("</OBJECT>")) {
		    			readingA = 0;
		    			mult = 0;
		    			readOwner = false;
		    			readAType = false;
		    			readRoleOwned = false;
		    			if(a.roleOwner.isEmpty()) {
		    				a.roleOwner = a.getOwner() + assoc.indexOf(a); 
		    			}
		    		}
		    	}
		    	
		    	if(line.contains("mc=\"Standard.AssociationEnd\"")  && !readingAtb && readingA==0) {
		    		readingA = 1;
		    		a = new Association();
		    		a.setOwned(c.getNome());
		    		assoc.add(a);
		    		s = line.split("<ID name=\"")[1].split("\"")[0];
		    		a.roleOwner = s;
		    		
		    	}
		    	
		    	if(line.contains("<COMP relation=\"Parent\">") && !readingAtb && readingA==0) {
		    		readingA = 1;
		    		a = new Association();
		    		a.setOwned(c.getNome());
		    		assoc.add(a);
		    	}
		    	
		    	if(readingAtb) {
		    		if(line.contains("</COMP>")) {
		    			readingAtb = false;
		    		}
		    		if(line.contains("<FOREIGNID name=\"") && !line.contains("<FOREIGNID name=\"description\"")) {
		    			atb.setType(line.split("<FOREIGNID name=\"")[1].split("\"")[0]);
		    		}
		    		else if(line.contains("mc=\"Standard.Enumeration\"") && !line.contains("<FOREIGNID name=\"description\"")) {
		    			atb.setType(line.split("<ID name=\"")[1].split("\"")[0]);
		    			atb.isEnum = true;
		    		}
		    	}
		    	
		    	if(line.contains("mc=\"Standard.Attribute\"")) {
		    		readingAtb= true;	
		    		atb = new Attribute();
	    			atb.setName(line.split("<ID name=\"")[1].split("\"")[0]);
		    		c.addAtb(atb);
		    	}
	
	
		    }
		    reader.close();
		  }
		  catch (Exception e)
		  {
		    e.printStackTrace();
		  }
	}
	
	private void processEnumerate() {
		try
		  {
			File folder = new File(loc + "\\" + nomeP + "\\data\\fragments\\"+nomeP+"\\model\\Standard.Package");
			File file = folder.listFiles()[0];
		    BufferedReader reader = new BufferedReader(new FileReader(file));
		    String line;
		    Enumerate a = null;
		    while ((line = reader.readLine()) != null)
		    {
		    	if(line.contains("mc=\"Standard.Enumeration\"")) {
		    		a = new Enumerate();
		    		enums.add(a);
		    		a.nome = line.split("<ID name=\"")[1].split("\"")[0];
		    	}
		    	if(line.contains("mc=\"Standard.EnumerationLiteral\"")) {
		    		a.values.add(line.split("<ID name=\"")[1].split("\"")[0]);
		    	}
		    }
		    reader.close();
		  }
		  catch (Exception e)
		  {
		    e.printStackTrace();
		  }
	}
	
	private boolean pointer = false;
	private void clean() {
		for(int i=0; i<assoc.size(); i++) {
			if(assoc.get(i).getNome()==null || assoc.get(i).getOwner()==null || assoc.get(i).getOwned()==null || assoc.get(i).getOwner().equals("")) {
				if(pointer) {
					assoc.get(i-1).addMul(assoc.get(i).getMult().get(0));
					assoc.get(i-1).addMul(assoc.get(i).getMult().get(1));
					pointer =  false;
				}
				assoc.remove(i);
				i--;
			} else {
				if(!assoc.get(i).getNome().equals("Generalization")) {
					pointer = true;
				}
			}
		}
	}
	
	private void handleGen() {
		for(int i=0; i<assoc.size(); i++) {
			if(assoc.get(i).getNome().equals("Generalization")) {
				for(int l=0; l<classes.size(); l++) {
					if(assoc.get(i).getOwned().equals(classes.get(l).getNome())) {
						classes.get(l).setGen(assoc.get(i).getOwner());
					}
				}
				assoc.remove(i);
				i--;
			}
		}
	}
	
	private void normalize() {
		for(int i=0; i<assoc.size(); i++) {
			for(int l=0; l<classes.size(); l++) {
				if(assoc.get(i).getOwner().equalsIgnoreCase(classes.get(l).getNome())) {
					assoc.get(i).setOwner(classes.get(l).getNome());
				}
			}
		}
	}
	
	private void handleAssocClass() {
		for(int i=0; i<assoc.size(); i++) {
			if(assoc.get(i).hasAssocClass) {
				for(int l=0; l<classes.size(); l++) {
					if(classes.get(l).getNome().equals(assoc.get(i).nomeAssocClass)) {
						classes.get(l).isAssocClass = true;
						classes.get(l).assoc = i;
					}
				}
			}
		}
	}
	
	private void writeClasses() {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(target, true));
			for(int z=0; z< enums.size(); z++) {
				writer.append(System.getProperty("line.separator"));
				writer.append("enum " + enums.get(z).nome + " {");
				for(int p=0; p<enums.get(z).values.size(); p++) {
					writer.append(enums.get(z).values.get(p));
					if(p==enums.get(z).values.size()-1) {
						writer.append("}");
					} else {
						writer.append(",");
					}
				}
			}
			for(int i=0; i<classes.size(); i++) {
				writer.append(System.getProperty("line.separator"));
				writer.append(System.getProperty("line.separator"));
				if(classes.get(i).isAssocClass) {
					writer.append("associationclass " + classes.get(i).getNome());
					writer.append(System.getProperty("line.separator"));
					writer.append("between");
					writer.append(System.getProperty("line.separator"));
					writer.append(assoc.get(classes.get(i).assoc).getOwned() + " [" + assoc.get(classes.get(i).assoc).getMult().get(2) + ".." + assoc.get(classes.get(i).assoc).getMult().get(3) + "] role " + assoc.get(classes.get(i).assoc).roleOwned); 
					writer.append(System.getProperty("line.separator"));
					writer.append(assoc.get(classes.get(i).assoc).getOwner() + " [" + assoc.get(classes.get(i).assoc).getMult().get(0) + ".." + assoc.get(classes.get(i).assoc).getMult().get(1) + "] role " + assoc.get(classes.get(i).assoc).roleOwner);
					assoc.remove(classes.get(i).assoc);
				} else {
					writer.append("class " + classes.get(i).getNome());
				}
				if(classes.get(i).getGen()!=null) {
					writer.append(" < " + classes.get(i).getGen());
				}
				writer.append(System.getProperty("line.separator"));
				writer.append("attributes");
				writer.append(System.getProperty("line.separator"));
				for(int l=0; l<classes.get(i).getAtb().size(); l++) {
					String t  = classes.get(i).getAtb().get(l).getType();
					if(t!=null) {
						if(!classes.get(i).getAtb().get(l).isEnum) {
							char a[] = classes.get(i).getAtb().get(l).getType().toCharArray();
							a[0] = Character.toUpperCase(a[0]);
							t = new String(a);
						} 
						writer.append(classes.get(i).getAtb().get(l).getName() + ": " + t);
						writer.append(System.getProperty("line.separator"));
					}
				}
				writer.append("operations");
				writer.append(System.getProperty("line.separator"));
				writer.append("end");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void writeAssoc() {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(target, true));
			writer.append(System.getProperty("line.separator"));
			for(int i=0; i<assoc.size(); i++) {
				switch(assoc.get(i).getNome()) {
					case "KindIsAggregation":
						writer.append(System.getProperty("line.separator"));
						writer.append("aggregation " + assoc.get(i).getOwned() + "_" + assoc.get(i).getOwner() + i + " between");
					break;
					case "KindIsComposition":
						writer.append(System.getProperty("line.separator"));
						writer.append("composition " + assoc.get(i).getOwned() + "_" + assoc.get(i).getOwner() + i + " between");
					break;
					case "KindIsAssociation":
						writer.append(System.getProperty("line.separator"));
						writer.append("association " + assoc.get(i).getOwned() + "_" + assoc.get(i).getOwner() + i + " between");
					break;
				}
				writer.append(System.getProperty("line.separator"));
				writer.append(assoc.get(i).getOwned() + " [" + assoc.get(i).getMult().get(2) + ".." + assoc.get(i).getMult().get(3) + "] role " + assoc.get(i).roleOwned); 
				writer.append(System.getProperty("line.separator"));
				writer.append(assoc.get(i).getOwner() + " [" + assoc.get(i).getMult().get(0) + ".." + assoc.get(i).getMult().get(1) + "] role " + assoc.get(i).roleOwner);
				writer.append(System.getProperty("line.separator"));
				writer.append("end");
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}


