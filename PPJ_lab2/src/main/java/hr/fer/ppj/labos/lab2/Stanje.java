package hr.fer.ppj.labos.lab2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.ppj.labos.lab2.EpsilonNKA.LR1Stavka;

public class Stanje {
	
	private int index;
	private List<LR1Stavka> sadrzaj;
	private Map<String, Integer> prijelazi;
	
	public Stanje(int index, List<LR1Stavka> sadrzaj){
		this.index = index;
		this.sadrzaj = sadrzaj;
		prijelazi = new HashMap<>();
	}
	
	public void nadjiPrijelaze(List<Stanje> listaStanja){
		for (LR1Stavka stavka : sadrzaj) {
			if(stavka.getPrijelaz() != null){ //ako je stavka potpuna, nema prijelaz, tj prijelaz je null
				String znakZaPrijelaz = stavka.getPrijelaz().getZnakKojiPokrecePrijelaz();
				LR1Stavka prelaziU = stavka.getPrijelaz().getSljedecaStavka();
				for (Stanje stanje : listaStanja) {
					if(stanje.getSadrzaj().contains(prelaziU)){
						prijelazi.put(znakZaPrijelaz, stanje.getIndex());
						break;
					}
				}
			}
		}
	}
	
	public int getIndex(){
		return index;
	}
	
	public List<LR1Stavka> getSadrzaj(){
		return sadrzaj;
	}
	
	public Map<String, Integer> getPrijelazi(){
		return prijelazi;
	}
}
