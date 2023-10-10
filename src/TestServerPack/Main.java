package TestServerPack;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Main {
	private static Scanner scanner = null;
	private static ClientSock sock = null;
	private static List<String> commandList = null;
	
	private static void init() {
		sock = new ClientSock("min.ddnsking.com", 9999, false);
		commandList = new ArrayList<String>( 10 );
		
		// 접근 경로.
		String accessXmlPath = "resource/commandList.xml";
		
		Path currentRelativePath = Paths.get("");
		String currentAbsoulutePath = currentRelativePath.toAbsolutePath().toString();
		
		String[] splitArray = currentAbsoulutePath.split("\\\\");
		if(splitArray[splitArray.length - 1].equals("bin")) {
			accessXmlPath = "../resource/commandList.xml";
		}
		
		//Parse Xml
		Document xml = null;
	    try {
	    	InputSource is = new InputSource(new FileReader(accessXmlPath));
	    	xml = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
	    	NodeList childNodelist = xml.getDocumentElement().getChildNodes();
	    	//child node 가 1개 이상인 경우
	    	if(childNodelist.getLength() > 0) {
	    		for(int nodeIndex = 0; nodeIndex < childNodelist.getLength(); nodeIndex++) {
	    			if(childNodelist.item(nodeIndex).getNodeName().equals("command")) {
	    				commandList.add(childNodelist.item(nodeIndex).getTextContent());     
	    			}
	    		}
	    	}
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	}
	
	private static void printCommandList() {
		try {
	        if (System.getProperty("os.name").contains("Windows"))
	            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
	        else
	            Runtime.getRuntime().exec("clear");
	    } 
		catch (IOException | InterruptedException ex) {}
		
		System.out.println("----------------------------------------");
		System.out.println("1. Server Start 명령어 날리기");
		System.out.println("2. Server Stop 명령어 날리기");
		System.out.println("3. Server Update 명령어 날리기");
		System.out.println("4. 종료");
		System.out.println("----------------------------------------");
	}
	
	private static boolean requestCommand(String command) {
		if(command == null || command.isEmpty())
			return true;
		
		if(!commandList.
				stream().
				anyMatch((String data) -> data.equals(command))) {
			System.out.println("일치하는 명령어 없음.");
			return false;
		}
		
		if(command.equals("4"))
			return false;
		
		// 명령어 처리 (여기서 네트워크처리까지)
		boolean sendResult = sock.sendData(command);
		if(!sendResult) {
			System.out.println("Send 실패.");
			return false;
		}
		
		String recvStr = sock.recvData();
		if(recvStr != null && !recvStr.isEmpty() ) {
			System.out.println(recvStr + " Command 처리 완료");
			System.out.print("아무거나 입력.\n");
			scanner.nextLine();
		}
		
		return true;
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		scanner = new Scanner(System.in);
		
		init();
		
		boolean run = true;
		while(run) {	
			printCommandList();
			System.out.printf("명령어 입력 : ");
			String input = scanner.nextLine();
			
			run = requestCommand(input);
		}
		scanner.close();
		sock.close();
	}
}