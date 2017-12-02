package hr.fer.ppj.labos.lab2;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class GenerativnoStablo {
	private Stack<Cvor> stog;
	private final List<String> nezavrsni;
	private final List<String> zavrsni;
	private final List<String> sinkronizacijski;
	private final Akcija akcija;
	private final NovoStanje novoStanje;
	private Cvor korijen;
	private Map<String, Map<Integer, List<String>>> produkcije;
	
	public GenerativnoStablo(
			Akcija akcija, NovoStanje novoStanje, 
			Map<String, Map<Integer, List<String>>> produkcije) {
		this.nezavrsni = novoStanje.getNezavrsniZnakovi();
		this.zavrsni = akcija.getZavrsniIKraj();
		this.sinkronizacijski = akcija.getSinkronizacijski();
		this.stog = new Stack<>();
		this.akcija = akcija;
		this.novoStanje = novoStanje;
		this.produkcije = produkcije;
	}

	public void pomakni(String uniformniZnak, int stanje, int brojRetka,
			String leksickaJedinka){
		Cvor noviCvor = new Cvor(stanje, uniformniZnak, brojRetka, 
				leksickaJedinka, false);
		stog.push(noviCvor);
	}
	
	public void reduciraj(int indeksProdukcije, int sljedeceStanje){
		String lijeviZnakProdukcije = null;
		List<String> desnaStranaProdukcije = null;
		
		for(Map.Entry<String, Map<Integer, List<String>>> produkcija : produkcije.entrySet()){
			if(produkcija.getValue().containsKey(indeksProdukcije)){
				lijeviZnakProdukcije = produkcija.getKey();
				desnaStranaProdukcije = produkcija.getValue().get(indeksProdukcije);
				break;
			}
		}
		
		if(desnaStranaProdukcije.contains("$")){
			Cvor noviCvor = new Cvor(sljedeceStanje, lijeviZnakProdukcije, true);
			noviCvor.dodajDijete(new Cvor(-1, "$", true));
			stog.push(noviCvor);
		}
		else{
			Cvor noviCvor = new Cvor(sljedeceStanje, lijeviZnakProdukcije, true);
			for(int i = 0; i < desnaStranaProdukcije.size(); i++){
				noviCvor.dodajDijete(stog.pop());
			}
			stog.push(noviCvor);
		}
	}
	
	public void prihvati(){
		this.reduciraj(1, 0);
		this.korijen = stog.pop();
	}
	
	//ispis se koristi koristenjem korijena iz ovog razreda, pomak 
	//mora biti 0
	public void ispisiStablo(Cvor korijen, int pomak){
		if(korijen == null) System.out.println("prvo generiraj stablo");
		else{
			if(!(korijen.getUniformniZnak() == "<%>")){
				for(int i = 0; i < pomak; i++){
					System.out.print(" ");
				}
				System.out.println(korijen);
				pomak++;
			}
			for (Cvor dijete : korijen.getDjeca()) {
				this.ispisiStablo(dijete, pomak);
			}
		}
	}
	
	static class Cvor{
		
		private int stanje;
		private String uniformniZnak;
		private int brojRetka;
		private String leksickaJedinka;
		private boolean ispisatiSamoUniformniZnak; //da znam je li unutrasnji cvor ili list
		
		private List<Cvor> djeca;
		//kod ispisa paziti je li unutrasnji cvor ili list i je li $
		public Cvor(int stanje, String uniformniZnak, int brojRetka, String leksickaJedinka, 
				boolean ispisatiSamoUniformniZnak) {
			this.stanje = stanje;
			this.uniformniZnak = uniformniZnak;
			this.brojRetka = brojRetka;
			this.leksickaJedinka = leksickaJedinka;
			this.ispisatiSamoUniformniZnak = ispisatiSamoUniformniZnak;
			this.djeca = new LinkedList<>();		}
		
		public Cvor(int stanje, String uniformniZnak, boolean jeNezavrsniZnak){
			this(stanje, uniformniZnak, -1, null, jeNezavrsniZnak);
		}
		
		public void dodajDijete(Cvor dijete){
			djeca.add(dijete);
		}

		public int getStanje() {
			return stanje;
		}

		public String getUniformniZnak() {
			return uniformniZnak;
		}

		public int getBrojRetka() {
			return brojRetka;
		}

		public String getLeksickaJedinka() {
			return leksickaJedinka;
		}

		public boolean isIspisatiSamoUniformniZnak() {
			return ispisatiSamoUniformniZnak;
		}

		public List<Cvor> getDjeca() {
			return djeca;
		}

		public void isIspisatiSamoUniformniZnak(boolean jeNezavrsniZnak) {
			this.ispisatiSamoUniformniZnak = jeNezavrsniZnak;
		}
		
		@Override
		public String toString() {
			if(ispisatiSamoUniformniZnak) return uniformniZnak;
			else{
				StringBuilder sb = new StringBuilder();
				sb.append(uniformniZnak);
				sb.append(" ");
				sb.append(brojRetka);
				sb.append(" ");
				sb.append(leksickaJedinka);
				return sb.toString();
			}
		}
	}
}
