package hr.fer.ppj.labos.lab2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hr.fer.ppj.labos.lab2.EpsilonNKA.LR1Stavka;

public class Akcija {
	private Map<String, Map<Integer, List<String>>> produkcije;
	private Par[][] tablica;
	private List<String> zavrsniIKraj;
	
	public Akcija(DKA dka, List<String> zavrsni, 
			Map<String, Map<Integer, List<String>>> produkcije){
		
		this.produkcije = produkcije;
		List<Stanje> stanjaDka = dka.getStanja();
		zavrsniIKraj = new ArrayList<>(zavrsni);
		zavrsniIKraj.add("#");
		
		tablica = new Par[stanjaDka.size()][zavrsniIKraj.size()];
		
		for (Stanje stanje : stanjaDka) {
			for (Map.Entry<String, Integer> prijelaz : stanje.getPrijelazi().entrySet()){
				String znak = prijelaz.getKey();
				Integer novoStanje = prijelaz.getValue();
				if(zavrsniIKraj.contains(znak))
					tablica[stanje.getIndex()][zavrsniIKraj.indexOf(znak)] = 
					new Par(AkcijaParsera.POMAKNI, novoStanje);
			}
			
			for (LR1Stavka stavka : stanje.getSadrzaj()) {
				if(stavka.getPrijelaz() == null){//potpuna LR stavka
					
					if(stavka.getZnakLijeveStraneProdukcije().equals("<%>")){
						tablica[stanje.getIndex()][zavrsniIKraj.indexOf("#")] = 
								new Par(AkcijaParsera.PRIHVATI, 0);
					}
					
					else{

						//provjeravat lijevu stranu stavke da se 
						//vidi treba li za nju dodati prihvati - ako je
						//lijeva strana novi pocetni nezavrsni znak
						
						//dodavat redukcije, paziti na pomakni/reduciraj 
						//i reduciraj/reduciraj: za prvi slucaj izaberi pomakni,
						//za drugi izaberi reduciraj koji ima manji indeks.
						
						for (String znakIzaProdukcije : stavka.getZnakoviIzaProdukcije()) {
							int indeksZnakaIzaProdukcije = zavrsniIKraj.indexOf(znakIzaProdukcije);
							Par promatraniPar = tablica[stanje.getIndex()][indeksZnakaIzaProdukcije];
							if(promatraniPar == null){
								tablica[stanje.getIndex()][indeksZnakaIzaProdukcije] = 
										new Par(AkcijaParsera.REDUCIRAJ, stavka.getIndeksProdukcije());
							}else if(promatraniPar.getAkcija() == AkcijaParsera.REDUCIRAJ && 
									promatraniPar.getIndex() > stavka.getIndeksProdukcije()){
								promatraniPar = new Par(AkcijaParsera.REDUCIRAJ, stavka.getIndeksProdukcije());
							}
						}
					}
					
				}
			}
		}
		
		for (int i = 0; i < tablica.length; i++) {
			for (int j = 0; j < tablica[i].length; j++) {
				if(tablica[i][j] == null){
					System.out.println("tu");
					tablica[i][j] = new Par(AkcijaParsera.ODBACI, 0);
				}
			}
		}
		
	}
	
	class Par{	
		private char akcija;
		private int index;
		
		public Par(char akcija, int index){
			this.akcija = akcija;
			this.index = index;
		}

		public char getAkcija() {
			return akcija;
		}

		public int getIndex() {
			return index;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(akcija);
			sb.append(index);
			return sb.toString();
		}
	}

	public Map<String, Map<Integer, List<String>>> getProdukcije() {
		return produkcije;
	}

	public Par[][] getTablica() {
		return tablica;
	}
}
