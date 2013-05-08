/**
 * Injekce poruch pro webove sluzby
 * Diplomovy projekt
 * Fakulta informacnich technologii VUT Brno
 * 3.2.2012
 */
package proxyUnit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import logging.ConsoleLog;

/**
 * Trida predstavuje vlakno proxy serveru starajici se bud o prichozi nebo odchozi pozadavky.
 * @author Martin Zouzelka (xzouze00@stud.fit.vutbr.cz)
 */
public class ProxyThread extends Thread {
	
	private static final int HTTP_CONTENT_LENGTH= 0;
	private static final int HTTP_CHUNKED_ENCODING= 1;
	
	private final int READ_HEADERS = 2;
	private final int READ_CONTENT = 3;
	
	private final int READ_CHUNK_SIZE = 4;
	private final int READ_CHUNK_CONTENT = 5;
	private static final int BUFFER_SIZE= 512;
	
	private int interactionId;
	private Socket incomingSocket;
	private Socket outgoingSocket;
	
	OutputStream outputStream;
	InputStream inputStream;
	
	private ProxyMonitoringUnit proxyUnit;
	private String rawMessage;

	
	public ProxyThread(int interactionId, ProxyMonitoringUnit unit, Socket incomingSocket, Socket outgoingSocket) {
		this.proxyUnit = unit;
		this.interactionId= interactionId;
		this.incomingSocket= incomingSocket;
		this.outgoingSocket= outgoingSocket;
		rawMessage= "";
	}

	/**
	 * Metoda pro beh predpripraveneho proxy vlakna, jenz nasloucha na zadanem portu a v pripade navazani
	 * spojeni danou zpravu preda proxy monitorovaci jednotce.
	 */
	@Override
	public void run() {
		
		
		char[] buffer= new char[BUFFER_SIZE];
		ConsoleLog.Print("[ProxyThread] vlakno spusteno");	
		
		try {
			outputStream= outgoingSocket.getOutputStream();
			inputStream= incomingSocket.getInputStream();	
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			int bytesToBeRead= -1;
			int readMode = READ_HEADERS;
			int chunkSize = 0;
			HttpMessage httpMessage= null;
			int httpMode= -1;
			int messageCounter = 0;
			int bytesRead = -1;
			String changedMessage = "";
			String header = "";
			while(true) {
				
				
				if(readMode == READ_HEADERS){
					header = reader.readLine();
					
					if(header == null){
						incomingSocket.close();
						outgoingSocket.close();
						break;
					}
					
					if(header.equals("")){
						rawMessage += "\r\n";
						readMode = READ_CONTENT;
						String transferEncoding = null;
						ConsoleLog.Print("[ProxyTread] New message");
						httpMessage= HttpMessageParser.parseHttpHeader(rawMessage, incomingSocket);
						
						transferEncoding= httpMessage.getTransferEncoding();
						
						
						//hlavicka neobsahuje Content-Length
						if (httpMessage.getContentLength() == -1){
							//pokud obsahuje chunkove kodovani
							if (transferEncoding != null && transferEncoding.equals("chunked")){
								httpMode= HTTP_CHUNKED_ENCODING;
							//pokud neobsahuje chunkove kodovani
							}
						//hlavicka obsahuje pole Content-Length
						}else{
							
							httpMode= HTTP_CONTENT_LENGTH;
						}
						
						
					}else{
						
						rawMessage +=header+"\r\n";
						continue;
					}
					
				}
				
				
				//pokud byl stream uzavren jednou z komunikujicich stran...zavrit sokety
				

//				//TODO: vyzkouset...melo by byt v poradku, ale rychlejsi
//				String substring= new String(buffer);
//				testString+= substring;

//				ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
//				GZIPInputStream gzis = new GZIPInputStream(bais);
//				InputStreamReader reader = new InputStreamReader(gzis);
//				BufferedReader in = new BufferedReader(reader);
//				
//				String readLine;
//				while ((readLine = in.readLine()) != null) {
//					rawMessage+= readLine;
//				}
				
				
				
				
				//pokud nacitame novou http zpravu..rozparsujeme hlavicku a podle dostupnych udaju vybereme
				//preposilaci mod
				
				switch (httpMode) {
					//---------------------------------- HTTP CONTENT LENGTH -------------------------------
					case HTTP_CONTENT_LENGTH:
						System.out.println("zkusim neco precist");
						bytesRead= reader.read(buffer, 0, BUFFER_SIZE);
						System.out.println("zapisu");
						
						if (bytesRead == -1) {
							incomingSocket.close();
							outgoingSocket.close();
							break;
						}
						
						for (int i= 0; i < bytesRead; i++)
							rawMessage+= buffer[i];
						
						if (bytesToBeRead == -1) {
							//spocitame, kolik bytu ma byt jeste ze streamu nacteno ke zhotoveni http zpravy
							//velikost obsahu - (pocet jiz nactenych bytu - velikost nactene http hlavicky)
							bytesToBeRead= httpMessage.getContentLength() - bytesRead;
							
						}else if (bytesToBeRead > 0){
								bytesToBeRead-= bytesRead;
						}
						//pokud jiz byla cela http zprava nactena..
						if (bytesToBeRead == 0) {
							//rozparsujeme telo zpravy
							System.out.println("test" +httpMessage.getHttpHeader() + httpMessage.getContent());
							
							changedMessage =  processContent( interactionId + messageCounter, httpMessage, rawMessage);
							outputStream.write(changedMessage.getBytes());
								
							bytesToBeRead= -1;
							rawMessage= "";
							messageCounter++;	
							readMode = READ_HEADERS;
						}
					break;
						
					//----------------------------------- HTTP CHUNKED ENCODING ------------------------------	
					//TODO: chunked encoding zatim nefunguje
					case HTTP_CHUNKED_ENCODING:
						
						if(readMode == READ_CHUNK_CONTENT){
							char[] chunkBuffer= new char[chunkSize];
							bytesRead= reader.read(chunkBuffer, 0, chunkSize);
							
							for (int i= 0; i < bytesRead; i++)
								rawMessage+= buffer[i];
						}else{
							chunkSize = Integer.parseInt(reader.readLine(),16);
						}
						
							//rozparsujeme telo zpravy
						changedMessage =  processContent( interactionId + messageCounter, httpMessage, rawMessage);
					
						outputStream.write(changedMessage.getBytes());
						
						bytesToBeRead= -1;
						rawMessage= "";
						messageCounter++;
						
					break;
					
					default:
						//pokud na vstupu jiz nejsou v tuto chvili data..pro jistotu chvili pockame
						if (inputStream.available() == 0) {
							try {
								sleep(10);
							}
							catch (Exception ex) {
								System.err.println(ex.getMessage());
								System.exit(1);
							}
							//pokud stale nejsou na vstupu data..predpokladame, ze bylo vse jiz poslano
							if (inputStream.available() == 0) {
								
								changedMessage =  processContent( interactionId + messageCounter, httpMessage, rawMessage);
								outputStream.write(changedMessage.getBytes());
								
								bytesToBeRead= -1;
								rawMessage= "";
								messageCounter++;
							}
							
						}
						
					break;
				}
			}
					
			
		}
		catch (IOException ex) {

			
		}
		catch (ArrayIndexOutOfBoundsException ex) {

			
		}
		
		
		
	}
	
	
	private String processContent(int interactionId,HttpMessage message,String rawMessage) throws IOException{
		
		String changedMessage = null;
		HttpMessageParser.parseHttpContent(message, rawMessage, true);
		
		proxyUnit.newMessageNotifier(interactionId, message);
		
		if(message.isChanged())
			changedMessage= message.getChangedHttpHeader() + message.getChangedContent();
		else
			changedMessage=rawMessage;
		
		return changedMessage;
		
	}
	
	
	
	
}
