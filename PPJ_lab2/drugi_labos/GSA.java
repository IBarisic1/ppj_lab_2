
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class GSA {

	public static final String NOVO_STANJE_PATH = "./analizator/NovoStanje.ser";
	public static final String AKCIJA_PATH = "./analizator/Akcija.ser";

	public static void main(String[] args) {
		Parser p = new Parser(args[0]);
		EpsilonNKA e = new EpsilonNKA(p.getNezavrsni(), p.getZapocinjeSkupovi(), p.getProdukcije(),
				p.getPrazniNezavrsni());
		DKA d = new DKA(e.getLR1Stavke());

//		for (Stanje s : d.getStanja()) {
//			System.out.println(s.getIndex());
//			for (LR1Stavka stavka : s.getSadrzaj()) {
//				System.out.println("  " + stavka);
//			}
//
//			System.out.println("PRIJELAZI:");
//
//			for (Map.Entry<String, Integer> prijelaz : s.getPrijelazi().entrySet()) {
//				System.out.println("  " + prijelaz.getKey() + ": " + prijelaz.getValue());
//			}
//		}
//		System.out.println();

		Akcija a = new Akcija(d, p.getZavrsni(), p.getSinkronizacijski(), p.getProdukcije());

//		System.out.println("AKCIJA:");
//		for (Par[] parovi : a.getTablica()) {
//			for (Par par : parovi) {
//				System.out.print(" " + par + " ");
//			}
//			System.out.println();
//		}

//		System.out.println(d.getStanja().size());

		NovoStanje n = new NovoStanje(d, p.getNezavrsni());

		try (ObjectOutputStream outNovoStanje = new ObjectOutputStream(new FileOutputStream(NOVO_STANJE_PATH));
				ObjectOutputStream outAkcija = new ObjectOutputStream(new FileOutputStream(AKCIJA_PATH))) {
			outNovoStanje.writeObject(n);
			System.err.println("Serialized data is saved in " + NOVO_STANJE_PATH);

			outAkcija.writeObject(a);
			System.err.println("Serialized data is saved in " + AKCIJA_PATH);

		} catch (IOException i) {
			i.printStackTrace();
		}

	}
}
