package hr.fer.ppj.labos.lab2;

public class GSA {
	
	public static void main(String[] args) {
		Parser p = new Parser(args[0]);
		EpsilonNKA e = new EpsilonNKA(p.getNezavrsni(), p.getZapocinjeSkupovi(), 
				p.getProdukcije(), p.getPrazniNezavrsni());
		//testirati DKA, testirati tablicu akcija, napisati i testirati 
		//tablicu novoStanje
	}
}
