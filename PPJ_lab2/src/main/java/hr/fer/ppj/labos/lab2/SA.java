package hr.fer.ppj.labos.lab2;

import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class SA {
	public static void main(String[] args) {
		 Akcija tablicaAkcija = null;
		 NovoStanje tablicaNovoStanje = null;
	        try (ObjectInputStream akcijaIn = new ObjectInputStream(new FileInputStream(GSA.AKCIJA_PATH));
	        		ObjectInputStream novoStanjeIn = new ObjectInputStream(new FileInputStream(GSA.NOVO_STANJE_PATH))){
	            tablicaAkcija = (Akcija) akcijaIn.readObject();
	            tablicaNovoStanje = (NovoStanje) novoStanjeIn.readObject();
	        }catch(Exception i) {
	            i.printStackTrace();
	        }

	}
}
