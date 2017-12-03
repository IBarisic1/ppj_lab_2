
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Parser implements Serializable {
	
	private static final long serialVersionUID = 9065750387758906132L;
	private final List<String> nezavrsni;
	private final List<String> zavrsni;
	private final List<String> sinkronizacijski;
	private Map<String, Map<Integer, List<String>>> produkcije;
	private Set<String> prazniNezavrsni;
	private Map<String, Set<String>> zapocinjeSkupovi;
	
	public Parser(String put){
		List<String> sveLinije = null;
		
		try(BufferedReader br = new BufferedReader(
				new InputStreamReader(new FileInputStream(put)))){
			//definicija liste u koju skupljam linije
			Supplier<List<String>> definicija = new Supplier<List<String>>() {
				@Override
				public List<String> get() {
					return new ArrayList<String>();
				}
			};
			//skupljanje u jednu listu
			sveLinije = br.lines().collect(Collectors.toCollection(definicija));
		}catch(IOException ie){
			ie.printStackTrace();
		}
		
		String[] poljeNezavrsnih = sveLinije.get(0).split(" ");
		nezavrsni = new ArrayList<>();
		for(int i = 1; i < poljeNezavrsnih.length; i++){
			nezavrsni.add(poljeNezavrsnih[i]);
		}
		
		String[] poljeZavrsnih = sveLinije.get(1).split(" ");
		zavrsni = new ArrayList<>();
		for(int i = 1; i < poljeZavrsnih.length; i++){
			zavrsni.add(poljeZavrsnih[i]);
		}
		
		String[] poljeSinkronizacijskih = sveLinije.get(2).split(" ");
		sinkronizacijski = new ArrayList<>();
		for(int i = 1; i < poljeSinkronizacijskih.length; i++){
			sinkronizacijski.add(poljeSinkronizacijskih[i]);
		}
		
		sveLinije.add(3, "<%>");
		sveLinije.add(4, " " + nezavrsni.get(0));
		nezavrsni.add(0, "<%>");
		this.izvuciProdukcije(sveLinije);
		
//		System.out.println("V: " + nezavrsni);
//		System.out.println("T: " + zavrsni);
//		System.out.println("Sink: " + sinkronizacijski);
//		
//		for (String string : produkcije.keySet()) {
//			System.out.print(string + " ::= ");
//			for (Integer i : produkcije.get(string).keySet()) {
//				System.out.print(i + " " + produkcije.get(string).get(i) + "|");
//			}
//			System.out.println();
//		}
		
		this.odrediPrazneNezavrsne();
//		System.out.println(prazniNezavrsni);
		
		this.odrediZapocinjeSkupove();
		
		
	}
	
	private void odrediPrazneNezavrsne() {
		prazniNezavrsni = new HashSet<>();
		
		//dodaj u listu praznih nezavrsnih one koji imaju e produkcije;
		for (String znak : nezavrsni) {
			Map<Integer, List<String>> produkcijeZaZnak = produkcije.get(znak);
			for (Integer index : produkcijeZaZnak.keySet()) {
				if(produkcijeZaZnak.get(index).contains("$")){
					prazniNezavrsni.add(znak);
					break;
				}
			}
		}
		
		//dokle god ima promjene u listi praznih nezavrsnih, 
		//provjeravaj ima li praznih znakova
		boolean dodanPrazanNezavrsni;
		do{
			dodanPrazanNezavrsni = false;
			for (String znak : nezavrsni) {
				if(!prazniNezavrsni.contains(znak)){
					boolean sadrziSvePrazne = true;
					Map<Integer, List<String>> produkcijeZaZnak = produkcije.get(znak);
					for (Integer index : produkcijeZaZnak.keySet()) {
						for (String znakProdukcije : produkcijeZaZnak.get(index)) {
							if(!prazniNezavrsni.contains(znakProdukcije)){
								sadrziSvePrazne = false;
								break;
							}
						}
						if(sadrziSvePrazne){
							prazniNezavrsni.add(znak);
							dodanPrazanNezavrsni = true;
							break;
						}
					}
				}
			}
		}while(dodanPrazanNezavrsni);
	}

	private void izvuciProdukcije(List<String> sveLinije){
		produkcije = new LinkedHashMap<>();
		String trenutniNezZnak = null;
		int i = 3; 
		int indeksProdukcije = 1;
		
		do{
			String sljedećaLinija = sveLinije.get(i);
			if(!sljedećaLinija.startsWith(" "))//nije produkcija
				trenutniNezZnak = sljedećaLinija;
			else{//podijeli produkciju na znakove i dodaj u listu
				String[] dijeloviProdukcije = sljedećaLinija.split(" ");
				List<String> produkcija = Arrays.asList(Arrays.copyOfRange(
						dijeloviProdukcije, 1, dijeloviProdukcije.length));
				//skini mapu i pogledaj postoji, ako ne napravi novu
				//koja god da je varijanta, dodaj u mapu novu produkciju
				Map<Integer, List<String>> noviUnos = produkcije.get
						(trenutniNezZnak);
				if(noviUnos == null) noviUnos = new HashMap<>();
				noviUnos.put(indeksProdukcije, produkcija);
				produkcije.put(trenutniNezZnak, noviUnos);
				indeksProdukcije++;
			}
			i++;
		}while(i < sveLinije.size());
	}
	
	private void odrediZapocinjeSkupove(){
		List<String> sviZnakovi = new ArrayList<String>
						(zavrsni.size() + nezavrsni.size());
		sviZnakovi.addAll(nezavrsni);
		sviZnakovi.addAll(zavrsni);
		
//		sortiranje za provjeru zapocinjeIzravnoZnakom i zapocinjeZnakom
//		(njihovi su po abecedi)
//		Collections.sort(sviZnakovi);
		
		boolean[][] zapocinjeIzravnoZnakom = 
				new boolean[sviZnakovi.size()][sviZnakovi.size()];
		
		//prolazi kroz produkciju i zapisuj koji izravno zapocinju
		for (String znak : nezavrsni) {
			for (Integer i : produkcije.get(znak).keySet()) {
				List<String> desnaStrana = produkcije.get(znak).get(i);
				int j = 0;
				String prviZnak;
				do{
					prviZnak = desnaStrana.get(j);
					if(!prviZnak.equals("$")){
						zapocinjeIzravnoZnakom[sviZnakovi.indexOf(znak)][sviZnakovi.indexOf(prviZnak)] = true;
					}
					j++;
				}while(prazniNezavrsni.contains(prviZnak) && j < desnaStrana.size());
			}
		}
		
		for(int i = 0; i < zapocinjeIzravnoZnakom.length; i++){
			zapocinjeIzravnoZnakom[i][i] = true;
		}
		
//		for (boolean[] bs : zapocinjeIzravnoZnakom) {
//			for (boolean b : bs) {
//				if(b) System.out.print("1 ");
//				else System.out.print("0 ");
//			}
//			System.out.println();
//		}
		
		boolean[][] zapocinjeZnakom = zapocinjeIzravnoZnakom.clone();
		
		//dok ima promjene u zapocinjeZnakom, dotle gledaj tranzitivnost
		boolean imaPromjene;
		do{
			imaPromjene = false;
			for(int i = 0; i < zapocinjeZnakom.length; i++){
				for(int j = 0; j < zapocinjeZnakom.length; j++){
					if(zapocinjeZnakom[i][j]){
						for(int k = 0; k < zapocinjeZnakom.length; k++){
							if(zapocinjeZnakom[j][k]){
								if(!zapocinjeZnakom[i][k]){
									zapocinjeZnakom[i][k] = true;
									imaPromjene = true;
								}
							}
						}
					}
				}
			}
		}while(imaPromjene);
		
//		System.out.println();
//		for (boolean[] bs : zapocinjeZnakom) {
//			for (boolean b : bs) {
//				if(b) System.out.print("1 ");
//				else System.out.print("0 ");
//			}
//			System.out.println();
//		}
		
		zapocinjeSkupovi = new HashMap<>();
		//dodaj skupove u mapu sa skupovima
		for(int i = 0; i < sviZnakovi.size(); i++){
			String nezZnak = sviZnakovi.get(i);
			Set<String> skupZapocinje = new HashSet<>();
			for(int j = 0; j < zapocinjeZnakom.length; j++){
				if(!nezavrsni.contains(sviZnakovi.get(j))){
					if(zapocinjeZnakom[i][j]) skupZapocinje.add(sviZnakovi.get(j));
				}
			}
			zapocinjeSkupovi.put(nezZnak, skupZapocinje);
		}
		
//		for (String znak : sviZnakovi) {
//			System.out.println(zapocinjeSkupovi.get(znak));
//		}
	}

	public List<String> getNezavrsni() {
		return nezavrsni;
	}

	public List<String> getZavrsni() {
		return zavrsni;
	}

	public List<String> getSinkronizacijski() {
		return sinkronizacijski;
	}

	public Map<String, Map<Integer, List<String>>> getProdukcije() {
		return produkcije;
	}

	public Set<String> getPrazniNezavrsni() {
		return prazniNezavrsni;
	}

	public Map<String, Set<String>> getZapocinjeSkupovi() {
		return zapocinjeSkupovi;
	}
	
	
}
