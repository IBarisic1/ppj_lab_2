package hr.fer.ppj.labos.lab2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hr.fer.ppj.labos.lab2.EpsilonNKA.LR1Stavka;

public class Stanje implements Serializable{
	
	private static final long serialVersionUID = 1359383227590106801L;
	private int index;
	private List<LR1Stavka> sadrzaj;
	private Map<String, Integer> prijelazi;
	
	public Stanje(int index, List<LR1Stavka> sadrzaj){
		this.index = index;
		this.sadrzaj = sadrzaj;
		prijelazi = new HashMap<>();
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
	
	public void dodajPrijelaz(String znakZaPrijelaz, int indeksStanja){
		prijelazi.put(znakZaPrijelaz, indeksStanja);
	}
	
	public boolean sadrziSveStavke(List<LR1Stavka> stavke){
		if(stavke.size() != sadrzaj.size()) return false;
		for (LR1Stavka lr1Stavka : stavke) {
			if(!sadrzaj.contains(lr1Stavka)) return false;
		}
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Stanje){
			Stanje s = (Stanje) obj;
			return s.getIndex() == index;
		}
		return false;
	}
}
