package tests;

import java.util.Scanner;

public class TestingRegex {
	
	public String matchL(String id){
		if (id.matches("[a-zA-Z_0-9\\s]")){
			return "Válido";
		}
		return "Inválido";
	}

	public String matchId(String id){
		if (id.matches("[a-zA-Z][[a-zA-Z_0-9]|_]*")){
			return "Válido";
		}
		return "Inválido";
	}

	public String matchChar(String id){
		if (id.matches("'[a-zA-Z_0-9\\s]'")){
			return "Válido";
		}
		return "Inválido";
	}
	

	public String a123124(String id){
			if (id.matches("[0-9]+")){
				return "Válido";
			}
			return "Inválido";
		}

	public String matchNumber(String id){
			if (id.matches("-[\\s]*[\\d]+|-[\\s]*[\\d]+\\.[\\d]+")){
				return "Válido";
			}
			return "Inválido";
		}

	public String simbo(String s){
		String simbolos = "\\#|\\$|\\%|\\?|\\@|\\^|\\~|\\|\\*|:|\\<|\\>|_|!|\\.";
		String simbolosOperadores = "\\+|\\-|\\=|\\&|\\||/|\\\\";
		String simbolosDelimitadores = "\\;|\\,|\\(|\\)|\\{|\\}|\\[|\\]";
		
		
		if (s.matches("\\/\\*[[a-zA-Z_0-9]| |\n|\t|\\'|\"|" + simbolos + "|" + simbolosDelimitadores + "|"
							+ simbolosOperadores + "]*\\*/")){
			return "Válido";
		}
		return "Inválido";
	}



	public String matchS(String s){
		if (s.matches("/\\*[a-zA-Z_0-9\\s]\\*/")){
			return "Válido";
		}
		return "Inválido";
	}

	
	public String matchString(String lexema){
		String simbolos = "\\#|\\$|\\%|\\?|\\@|\\^|\\~|\\|\\*|:|\\<|\\>|_|!|\\.";
		String simbolosOperadores = "\\+|\\-|\\=|\\&|\\||/|\\\\";
		String simbolosDelimitadores = "\\;|\\,|\\(|\\)|\\{|\\}|\\[|\\]";
		if (lexema.matches("\\/\\*[[a-zA-Z_0-9]| |\n|\t|\\'|\"|" + simbolos + "|" + simbolosDelimitadores + "|"
							+ simbolosOperadores + "]*\\\\*/")){
			
		}
		return "";
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		char c = 't';
		String entry = "";
		TestingRegex t = new TestingRegex();
		while (true){
			Scanner reader = new Scanner(System.in);
			System.out.print("Informe uma entrada ($ para sair): ");
			entry = reader.nextLine();
			if (entry.equals("959"))
				break;
			System.out.println("Validade do id '" + entry + "': " + t.a123124(entry));
			
		}
//		System.out.println(t.simbo("#"));
		
		System.out.println("Fim de Teste!!");
	}

}
