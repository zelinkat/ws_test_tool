/**
 * Injekce poruch pro webove sluzby
 * Diplomovy projekt
 * Fakulta informacnich technologii VUT Brno
 * 3.2.2012
 */
package data;

import proxyUnit.HttpMessage;


/**
 * Trida predstavuje konkretni typ poruchy XML zpravy. Pokud jsou splneny prislusne podminky,
 * preposlani zpravy je pozdrzeno o zadany pocet milisekund;
 * @author Martin Zouzelka (xzouze00@stud.fit.vutbr.cz)
 */
public class DelayFault extends Fault {

	
	private int numOfMilis;
	private String typeName;
	private String description;
	
	
	public DelayFault(int numOfMilis) {
		
		//super(faultId);
		this.numOfMilis= numOfMilis;
				
		typeName= "DelayFault";
		description= "The message is delayed for " + numOfMilis + " miliseconds.";
		
		
	}

		
	/**
	 * Ziskani popisu poruchy.
	 * @return popis poruchy
	 */
	@Override
	public String getDescription() {
		
		return description;
	}

	/**
	 * Metoda uspi vlakno starajici se o prenos zpravy o zadany pocet milisekund.
	 * @param message zprava
	 */
	@Override
	public void inject(HttpMessage message) {
		
		try {
			Thread.sleep(numOfMilis);
		}
		catch (InterruptedException ex) {
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
	}

	/**
	 * Ziskani typu poruchy.
	 * @return typ poruchy
	 */
	@Override
	public String toString() {
		
		return typeName;
	}
	
	
	
}