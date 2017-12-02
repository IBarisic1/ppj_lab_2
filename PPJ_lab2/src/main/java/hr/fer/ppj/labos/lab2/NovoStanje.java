package hr.fer.ppj.labos.lab2;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class NovoStanje implements Serializable {
	
	private static final long serialVersionUID = -4210169076773587890L;
	private Integer[][] tablica;
	private List<String> nezavrsniZnakovi;

	public NovoStanje(DKA dka, List<String> nezavrsniZnakovi) {

		List<Stanje> stanjaDka = dka.getStanja();
		this.nezavrsniZnakovi = nezavrsniZnakovi;

		tablica = new Integer[stanjaDka.size()][nezavrsniZnakovi.size()];

		for (Stanje stanje : stanjaDka) {
			for (Map.Entry<String, Integer> prijelaz : stanje.getPrijelazi().entrySet()) {
				String znak = prijelaz.getKey();
				Integer novoStanje = prijelaz.getValue();
				if (nezavrsniZnakovi.contains(znak))
					tablica[stanje.getIndex()][nezavrsniZnakovi.indexOf(znak)] = novoStanje;
			}

		}
	}

	public Integer[][] getTablica() {
		return tablica;
	}

	public List<String> getNezavrsniZnakovi() {
		return nezavrsniZnakovi;
	}
	
}
