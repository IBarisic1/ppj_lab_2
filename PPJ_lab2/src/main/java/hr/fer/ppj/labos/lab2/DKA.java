package hr.fer.ppj.labos.lab2;

import java.util.ArrayList;
import java.util.List;

import hr.fer.ppj.labos.lab2.EpsilonNKA.LR1Stavka;

public class DKA {
	
	private List<LR1Stavka> listaStavki;
	private List<Stanje> listaStanja;
	
	public DKA(List<LR1Stavka> listaStavki){
		this.listaStavki = listaStavki;
		this.generirajStanja();
	}
	
	private void generirajStanja(){
		int index = 0;
		listaStanja = new ArrayList<>();
		for (LR1Stavka stavka : listaStavki) {
			if(!stavka.isJeLiDodanaUStanjeDKA()){
				List<LR1Stavka> sadrzaj = new ArrayList<>();
				sadrzaj.add(stavka);
				for(int i = 0; i < sadrzaj.size(); i++){
					List<LR1Stavka> listaEpsilon = sadrzaj.get(i).
							getStavkeUKojePrelaziSEpsilon();
					for (LR1Stavka lr1Stavka : listaEpsilon) {
						if(!sadrzaj.contains(lr1Stavka)){
							sadrzaj.add(lr1Stavka);
							lr1Stavka.setJeLiDodanaUStanjeDKA(true);
						}
					}
				}
				Stanje s = new Stanje(index, sadrzaj);
				listaStanja.add(s);
				index++;
			}
		}
		for (Stanje stanje : listaStanja) {
			stanje.nadjiPrijelaze(listaStanja);
		}
	}
	
	public List<Stanje> getStanja(){
		return listaStanja;
	}
}
