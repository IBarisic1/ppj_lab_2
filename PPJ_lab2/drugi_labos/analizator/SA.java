
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class SA {
	public static void main(String[] args) {
		Akcija tablicaAkcija = null;
		NovoStanje tablicaNovoStanje = null;
		try (ObjectInputStream akcijaIn = new ObjectInputStream(new FileInputStream(GSA.AKCIJA_PATH));
				ObjectInputStream novoStanjeIn = new ObjectInputStream(new FileInputStream(GSA.NOVO_STANJE_PATH))) {
			tablicaAkcija = (Akcija) akcijaIn.readObject();
			tablicaNovoStanje = (NovoStanje) novoStanjeIn.readObject();
		} catch (Exception i) {
			i.printStackTrace();
		}

		ParserTabliceUniformnihZnakova parser = new ParserTabliceUniformnihZnakova(args[0]); // TODO
																								// u
																								// eclipseu																						// podesi
																								// ulazni
																								// argument-put
																								// do
																								// datoteke
																								// s
																								// uniformnim
																								// znakovima
		GenerativnoStablo stablo = new GenerativnoStablo(tablicaAkcija, tablicaNovoStanje,
				parser.getUniformniZnakoviUlaznogNiza());
		
		stablo.ispisiStablo(stablo.getKorijen(), 0);
	}
}
