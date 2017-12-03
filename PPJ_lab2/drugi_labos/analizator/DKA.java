
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DKA implements Serializable {
	
	private static final long serialVersionUID = 2268260805958615895L;
	private List<LR1Stavka> listaStavki;
	private List<Stanje> listaStanja;
	
	public DKA(List<LR1Stavka> listaStavki){
		this.listaStavki = listaStavki;
		this.generirajStanja();
	}
	
	private void generirajStanja(){
		listaStanja = new ArrayList<>();
		
		List<LR1Stavka> sadrzajPrvog = new ArrayList<>();
		sadrzajPrvog.add(listaStavki.get(0));
		
		for(int i = 0; i < sadrzajPrvog.size(); i++){
			for(LR1Stavka epsilonPrijelaz : 
				sadrzajPrvog.get(i).getStavkeUKojePrelaziSEpsilon()){
				if(!sadrzajPrvog.contains(epsilonPrijelaz))
					sadrzajPrvog.add(epsilonPrijelaz);
			}
		}
		
		listaStanja.add(new Stanje( listaStanja.size(), sadrzajPrvog));
		
		for(int i = 0; i < listaStanja.size(); i++){
			Map<String, List<LR1Stavka>> mapaPrijelaza = new HashMap<>();
			for (LR1Stavka lr1Stavka : listaStanja.get(i).getSadrzaj()) {
				Par prijelaz = lr1Stavka.getPrijelaz();
				if(prijelaz != null){
					if(mapaPrijelaza.get(prijelaz.getZnakKojiPokrecePrijelaz()) == null)
						mapaPrijelaza.put(prijelaz.getZnakKojiPokrecePrijelaz(), 
								new ArrayList<LR1Stavka>());
					mapaPrijelaza.get(prijelaz.getZnakKojiPokrecePrijelaz()).
					add(prijelaz.getSljedecaStavka());
				}
			}
			for(Map.Entry<String, List<LR1Stavka>> prijelazZaZnak : 
				mapaPrijelaza.entrySet()){
				List<LR1Stavka> stavke = prijelazZaZnak.getValue();
				for(int j = 0; j < stavke.size(); j++){
					for(LR1Stavka epsilonPrijelaz : 
						stavke.get(j).getStavkeUKojePrelaziSEpsilon()){
						if(!stavke.contains(epsilonPrijelaz))
							stavke.add(epsilonPrijelaz);
					}
				}
			}
			for(Map.Entry<String, List<LR1Stavka>> prijelazZaZnak : 
				mapaPrijelaza.entrySet()){
				List<LR1Stavka> stavke = prijelazZaZnak.getValue();
				String znakZaPrijelaz = prijelazZaZnak.getKey();
				boolean jeUListiStanja = false;
				for (Stanje stanje1 : listaStanja) {
					if(stanje1.sadrziSveStavke(stavke)){
						listaStanja.get(i).dodajPrijelaz(znakZaPrijelaz, 
								stanje1.getIndex());
						jeUListiStanja = true;
						break;
					}
				}
				if(!jeUListiStanja){
					Stanje novoStanje = new Stanje(listaStanja.size(), stavke);
					listaStanja.get(i).dodajPrijelaz(znakZaPrijelaz, novoStanje.getIndex());
					listaStanja.add(novoStanje);
				}
			}
		}
	}
	
	public List<Stanje> getStanja(){
		return listaStanja;
	}
}
