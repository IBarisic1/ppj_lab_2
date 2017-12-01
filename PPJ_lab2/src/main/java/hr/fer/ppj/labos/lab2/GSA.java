package hr.fer.ppj.labos.lab2;

import java.util.Map;

import hr.fer.ppj.labos.lab2.Akcija.Par;
import hr.fer.ppj.labos.lab2.EpsilonNKA.LR1Stavka;

public class GSA {
	
	public static void main(String[] args) {
		Parser p = new Parser(args[0]);
		EpsilonNKA e = new EpsilonNKA(p.getNezavrsni(), p.getZapocinjeSkupovi(), 
				p.getProdukcije(), p.getPrazniNezavrsni());
		DKA d = new DKA(e.getLR1Stavke());
		
		for (Stanje s : d.getStanja()) {
			System.out.println(s.getIndex());
			for (LR1Stavka stavka : s.getSadrzaj()) {
				System.out.println("  " + stavka);
			}
			
			System.out.println("PRIJELAZI:");
			
			for (Map.Entry<String, Integer> prijelaz : s.getPrijelazi().entrySet()) {
				System.out.println("  " + prijelaz.getKey() + ": " + prijelaz.getValue());
			}
		}
		System.out.println();
		
		Akcija a = new Akcija(d, p.getZavrsni(), p.getProdukcije());
		
		System.out.println("AKCIJA:");
		for (Par[] parovi : a.getTablica()) {
			for (Par par : parovi) {
				System.out.print(" " + par + " ");
			}
			System.out.println();
		}
		
		
		//testiran DKA, testirana tablica akcija, napisati i testirati 
		//tablicu novoStanje
		
	}
}
