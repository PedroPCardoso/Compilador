package analyzers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import utils.Token;


/**
 * Analisador Léxico responsável por classificar os tokens de entrada
 * @return
 */
public class Lexical {

	/*
		Arraylist responsavel por armazenar os tokens encontrados na entrada
	 */
	private ArrayList<Token> tokenList = new ArrayList<Token>();
	private ArrayList<Token> errorList = new ArrayList<Token>();
	private ArrayList<Token> commentList = new ArrayList<Token>();


	private ArrayList<String> elementList = new ArrayList<String>();
	private ArrayList<String> reservedWords = new ArrayList<String>();
	private ArrayList<String> opAritmetic = new ArrayList<String>();
	private ArrayList<String> opRelational = new ArrayList<String>();
	private ArrayList<String> opLogic = new ArrayList<String>();
	private ArrayList<String> delimiters = new ArrayList<String>();

	private ArrayList<String> lexemDelimitators = new ArrayList<String>();

	private int lines = 1; 

	private int lexemType;
	/*
		Declaração do construtor caso seja necessário implementação futura
	 */
	public Lexical(){}

	/**
	 * MÃ©todo responsÃ¡vel por iniciar a anÃ¡lise lÃ©xica da entrada
	 * @param entry [Array de caracteres]
	 */
	public void analyze(String entry){
		entry = entry.trim();
		//System.out.println("Quantidade de Chars na Entrada: " + entry.length());
		char[] t = entry.toCharArray();
		
		//System.out.println("Código de Entrada: " + entry + "\n\n");

		initAnalyzer();

		char currentChar;
		String currentLexem = "";

		for (int i = 0; i < entry.length(); i++){
			currentLexem = "";
			currentChar = entry.charAt(i);

				// Cadeia de Comentário
			if (currentChar == '/' && ((i+1 < entry.length()) && (entry.charAt(i+1) == '*'))){
				int currentLine = lines;
				// System.out.println("Comentario Bloco");
				currentLexem += entry.charAt(i);
				currentLexem += entry.charAt(i+1);
				i++;
				resetDelimitators();
				boolean correct = false;
				for (int j = i+1; j < entry.length(); j++){
					if (entry.charAt(j) == '\n')
						lines++;
					if (entry.charAt(j) == '*' && ( (j+1 < entry.length()) && (entry.charAt(j+1) == '/'))){
						currentLexem += "*/";
						i = j+1;
						commentList.add(new Token(currentLexem, "Comentario de Bloco", currentLine, 0));
						correct = true;
						break;
					} else {
						currentLexem += entry.charAt(j);
					}
					i = j;
				}
				if (!correct){
					errorList.add(new Token(currentLexem, "Comentario Bloco inválido", currentLine, 0));
				}
				

				// Comentário em Linha
			} else if (currentChar == '/' && ((i+1 < entry.length()) && (entry.charAt(i+1) == '/'))){
				int currentLine = lines;
				boolean inserted = false;
				resetDelimitators();
				for (int j = i+2; j < entry.length(); j++){
					i = j;
					if (entry.charAt(j) != '\n' ){
						currentLexem += entry.charAt(j);
					} else {
						inserted = true;
						commentList.add(new Token(currentLexem, "Comentario de Cadeia", currentLine, 0));	
						lines++;
						break;
						// createToken
					}
				}
				if(!inserted){
					commentList.add(new Token(currentLexem, "Comentario de Cadeia", currentLine, 0));	
				}

				// ID ou palavra reservada
			} else if (String.valueOf(entry.charAt(i)).matches("[a-zA-Z]") ){
				// System.out.println("Id");
				int currentLine = lines;
				boolean inserted = false;
				setDelimitators(entry.charAt(i));
				// System.out.println(lexemDelimitators.indexOf(String.valueOf(entry.charAt(i))));
				
				for (int j = i; j < entry.length(); j++){
					i = j;

					if (lexemDelimitators.indexOf(String.valueOf(entry.charAt(j))) == -1){
						currentLexem += entry.charAt(j);
					} else {
						inserted = true;
						if (reservedWords.indexOf(String.valueOf(currentLexem)) >= 0){
							tokenList.add(new Token(currentLexem, "Palavra Reservada", currentLine, 0));
						} else {
							if (currentLexem.matches("[a-zA-Z][[a-zA-Z_0-9]|_]*")){
								tokenList.add(new Token(currentLexem, "Identificador", currentLine, 0));
							} else {
								errorList.add(new Token(currentLexem, "Identificador mal formado", currentLine, 0));
							}
							i--;
						}
						break;
					}

				}
				// se for o ultimo token
				if (!inserted){
					if (reservedWords.indexOf(String.valueOf(currentLexem)) >= 0){
						tokenList.add(new Token(currentLexem, "Palavra Reservada", currentLine, 0));
					} else {
						if (currentLexem.matches("[a-zA-Z][[a-zA-Z_0-9]|_]*")){
							tokenList.add(new Token(currentLexem, "Identificador", currentLine, 0));
						} else {
							errorList.add(new Token(currentLexem, "Identificador mal formado", currentLine, 0));
						}
					}
				}

				// Char
			} else if (currentChar == '\''){
				int currentLine = lines;
				// System.out.println("Char");
				boolean inserted = false;
				currentLexem += entry.charAt(i);
				for (int j = i+1; j < entry.length(); j++){
					i = j;
					if (entry.charAt(j) == '\n'){
						errorList.add(new Token(currentLexem, "Char incompleto", currentLine, 0));
						lines++;
						break;
					}
					if (entry.charAt(j) != '\''){
						currentLexem += entry.charAt(j);
					} else {
						currentLexem += '\'';
						if (currentLexem.matches("'[a-zA-Z_0-9\\s]?'")){
							tokenList.add(new Token(currentLexem, "Char", currentLine, 0));							
						} else {
							errorList.add(new Token(currentLexem, "char inválido", currentLine, 0));
						}
						inserted = true;
						break;
					}
				}
				if (i+1 == entry.length() && !inserted){
					if (currentLexem.matches("'[a-zA-Z_0-9\\s]'")){
						tokenList.add(new Token(currentLexem, "char válido", currentLine, 0));							
					} else {
						errorList.add(new Token(currentLexem, "char inválido", currentLine, 0));
					}
				}
				
			} else if (currentChar == '\"'){
				// Cadeia de Caractere
				int currentLine = lines;
				currentLexem += "\"";
								
				for (int j = i+1; j < entry.length(); j++){
					i = j;
					if (entry.charAt(j) == '\"'){
						currentLexem += "\"";						
						String symbols = "\\#|\\$|\\%|\\?|\\@|\\^|\\~|\\|\\*|:|\\<|\\>|_|!|\\.";
						String symbolsOp = "\\+|\\-|\\=|\\&|\\||/|\\\\";
						String symbolsDelimitators = "\\;|\\,|\\(|\\)|\\{|\\}|\\[|\\]";
						
						
						if (currentLexem.matches("\"[[a-zA-Z_0-9]|\\s|\t|\\'|" + symbols + "|" + symbolsOp + "|"
											+ symbolsDelimitators + "]*\"")){
							tokenList.add(new Token(currentLexem, "Cadeia de Caractere", currentLine, 0));
						} else {
							errorList.add(new Token(currentLexem, "Cadeia de Caractere inválida", currentLine, 0));
						}
						break;
					} else if (entry.charAt(j) == '\n'){

						errorList.add(new Token(currentLexem, "Cadeia de Caractere não terminada", currentLine, 0));
						lines++;
						break;
					} else {
						currentLexem += entry.charAt(j);
					}	
				}

			} else if (delimiters.indexOf(String.valueOf(currentChar)) != -1){
				// Delimitador
				int currentLine = lines;
				currentLexem += currentChar;
				tokenList.add(new Token(currentLexem, "Delimitador", currentLine, 1));
				currentLexem = "";
				continue;

			} else if (String.valueOf(entry.charAt(i)).matches("[0-9]")){
				// Numero positivo
				int currentLine = lines;
				setDelimitators(entry.charAt(i));
				currentLexem += entry.charAt(i);				
				for (int j = i+1; j < entry.length(); j++){					
					i = j;			
					if ( lexemDelimitators.indexOf(String.valueOf(entry.charAt(j))) == -1){
						currentLexem += entry.charAt(j);
					} else {
						if (entry.charAt(j) == '\n')
							lines++;
						if (currentLexem.matches("[\\d]+|-?[\\d]+\\.[\\d]+")){
							tokenList.add(new Token(currentLexem, "Numero", currentLine, 1));
						} else {
							errorList.add(new Token(currentLexem, "Numero mal formado", currentLine, 1));
						}
										
						// i = j-1;
						currentLexem = "";
						break;
					}
				}
				if (i+1 == entry.length()){
					if (currentLexem.matches("[\\d]+|-?[\\d]+\\.[\\d]+")){
						tokenList.add(new Token(currentLexem, "Numero", currentLine, 1));
					} else {
						errorList.add(new Token(currentLexem, "Numero mal formado", currentLine, 1));
					}
				}

			} else if (currentChar == '-'){
				int currentLin1e = lines;
				int tempJ = -1;
				boolean isOperator = false;
				if (i+1 == entry.length()){						
					tokenList.add(new Token("-", "Operador Aritmetico", currentLin1e, 0));
					isOperator = true;
				}

				for (int j = i+1; j < entry.length(); j++){
					if (entry.charAt(j) != ' ' && entry.charAt(j) != '\t'){
						tempJ = j;
						break;											
					}
					if (j+1 == entry.length()){						
						tokenList.add(new Token("-", "Operador Aritmetico", currentLin1e, 0));
						isOperator = true;
					}
				}
				
				if (!isOperator){
					
					String number = "";
					if (!(String.valueOf(entry.charAt(tempJ)).matches("[0-9]+"))){
						tokenList.add(new Token("-", "Operador Aritmetico", currentLin1e, 0));							
						continue;					
					} else {
						setDelimitators('9');
						number += entry.charAt(tempJ);
						int currentLine = lines;
						for (int j = tempJ+1; j < entry.length(); j++){
							if (entry.charAt(j) == '\n'){
								lines++;
							}			
							if (lexemDelimitators.indexOf(String.valueOf(entry.charAt(j))) == -1){
								number += entry.charAt(j);
							} else {
								if (number.matches("[\\d]+|-?[\\d]+\\.[\\d]+")){									
									String lexNegative = "-";
									lexNegative += number;
									tokenList.add(new Token(lexNegative, "numero negativo", currentLine, 0));						
									i = j;
								} else {
									System.out.println("eh operador");
									tokenList.add(new Token("-", "operador aritmetico", currentLine, 0));						
								}
								break;
							}
							if (entry.length() == j+1){
								if (number.matches("[\\d]+|-?[\\d]+\\.[\\d]+")){
									i = j;
									String lexNegative = "-";
									lexNegative += number;
									tokenList.add(new Token(lexNegative, "Numero", currentLine, 0));						
								} else {
									tokenList.add(new Token("-", "Operador Aritmetico", currentLine, 0));						
								}							
							}
						}

					}					
				}

			} else if (currentChar == '+' || currentChar == '*' || currentChar == '/' || currentChar == '%'){
				tokenList.add(new Token(String.valueOf(currentChar), "Operador aritmetico", lines, 0));
			} else if (currentChar == '!'){
				if ( i+1 != entry.length() && entry.charAt(i+1) == '='){
					tokenList.add(new Token("!=", "Operador Relacional", lines, 0));
					i++;
				} else {					
					tokenList.add(new Token("!", "Operador Lógico", lines, 0));
				}

			} else if (currentChar == '=' || currentChar == '<' || currentChar == '>'){

				if (currentChar == '='){
					tokenList.add(new Token("=", "Operador Relacional", lines, 0));					
				} else if (currentChar == '>'){
					if (i+1 != entry.length() && entry.charAt(i+1) == '='){
						tokenList.add(new Token(">=", "Operador Relacional", lines, 0));	
						i++;				
					} else {						
						tokenList.add(new Token(">", "Operador Relacional", lines, 0));					
					}

				} else {
					if (i+1 != entry.length() && entry.charAt(i+1) == '='){
						tokenList.add(new Token("<=", "Operador Relacional", lines, 0));	
						i++;				
					} else {						
						tokenList.add(new Token("<", "Operador Relacional", lines, 0));					
					}
				}

			} else if (currentChar == '&' || currentChar == '|'){

				if (currentChar == '&'){
					if (i+1 != entry.length() && entry.charAt(i+1) == '&'){
						tokenList.add(new Token("&&", "Operador Lógico", lines, 0));					
						i++;						
					} else{
						errorList.add(new Token("&", "Operador Lógico incompleto", lines, 0));											
					}  
				} else {
					if (i+1 != entry.length() && entry.charAt(i+1) == '|'){
						tokenList.add(new Token("||", "Operador Lógico", lines, 0));					
						i++;						
					} else{
						errorList.add(new Token("|", "Operador Lógico incompleto", lines, 0));											
					}  
				}

			} else if (currentChar == ' ' || currentChar == '\n' || currentChar == '\t'){
				if (currentChar == '\n')
					lines++;
				continue;
			}
			else {
				System.out.println("Erro lexema n esperado: " + currentChar);
				errorList.add(new Token(String.valueOf(currentChar), "Simbolo não esperado", lines, 0));
			}
		}

		printTokens();
	}


	public void createToken(String lexem, int type){
		if (type == 0){
			if (reservedWords.indexOf(lexem) >= 0){
				Token t = new Token(lexem, "Palavra Reservada", lines, 0);
				tokenList.add(t);
			} else if (lexem.matches("[a-zA-Z][[a-zA-Z_0-9]|_]*")){
				Token t = new Token(lexem, "Identificador", lines, 0);
				tokenList.add(t);				
			} else {
				Token t = new Token(lexem, "Identificador mal formado", lines, 0);
				errorList.add(t);
			}
		}

	}

	private void printTokens(){
		//System.out.println("tokens: " + tokenList.size() );
		for (int i = 0; i < tokenList.size(); i++){
			//printToken(tokenList.get(i));
		}
		//System.out.println("Erros: " + errorList.size() );
		for (int i = 0; i < errorList.size(); i++){
			//printToken(errorList.get(i));
		}
	}

	private void printToken(Token token){
		//System.out.println("Lexema: '" + token.getLexeme() + "'");
		//System.out.println("Tipo: '" + token.getType() + "'");
		//System.out.println("Linha: " + token.getLine());
	}

	private void resetDelimitators(){
		lexemDelimitators = new ArrayList<String>();		
	}

	private void setDelimitators(char first){
		String c = String.valueOf(first);
		// Character.toString(first);
		// String.valueOf(entry.charAt(1)).equals("\t")
		lexemDelimitators = new ArrayList<String>();
		if (c.matches("[a-zA-Z]")){
			lexemType = 0;
			// lexemDelimitators.add("\t");
			lexemDelimitators.add("\n");
			lexemDelimitators.add("|");
			lexemDelimitators.add("&");
			lexemDelimitators.add("\t");
			lexemDelimitators.add(" ");
			lexemDelimitators.addAll(opRelational);
			lexemDelimitators.addAll(opLogic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(delimiters);
			// System.out.println(lexemDelimitators);
		} else if (c.matches("[0-9]")){
			lexemType = 1;
			// lexemDelimitators.add("\t");
			lexemDelimitators.add("\n");
			lexemDelimitators.add("\t");
			lexemDelimitators.add("|");
			lexemDelimitators.add("&");
			lexemDelimitators.add(" ");
			lexemDelimitators.addAll(opRelational);
			lexemDelimitators.addAll(opLogic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(delimiters);
		}
		else if (first == '\''){
			lexemDelimitators.add("\'");
			// lexemDelimitators.add("\t");
			lexemDelimitators.add("\n");
		} else if (c.matches("-")){
			lexemType = 0;
			// lexemDelimitators.add("\t");
			lexemDelimitators.add("\n");
			lexemDelimitators.add("|");
			lexemDelimitators.add("&");
			lexemDelimitators.addAll(opRelational);
			lexemDelimitators.addAll(opLogic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(opAritmetic);
			lexemDelimitators.addAll(delimiters);
			// System.out.println(lexemDelimitators);
		}
	}
	
	public void saveResult(String fileName) throws IOException{        
		Path p = Paths.get("saida/Resultado de " + fileName);
		String tokens = "";
		String lexicalErrors = "";
		
		for (int i = 0; i < tokenList.size(); i++){			
			tokens += "0" + tokenList.get(i).getLine() + " " + tokenList.get(i).getLexeme() + " " + tokenList.get(i).getType() + "\n\r";			
		}
		
		for (int i = 0; i < errorList.size(); i++){			
			lexicalErrors += "0" + errorList.get(i).getLine() + " " + errorList.get(i).getLexeme() + " " + errorList.get(i).getType() + "\n\r";
		}
		
		//System.out.println(tokens);
		//System.out.println(lexicalErrors);
		String output = tokens + "\n\r\n\r" + lexicalErrors;
		try {
			Files.write(p, output.getBytes());
			System.out.println("Arquivos gravados.");
		} catch (Exception e) {
			System.out.println("\n\rFalha ao escrever: " + e);
		}
	}

	private void teste(){
		// Verificar se todos os simbolos fazem parte do elementos entre 32 e 126 da tabela ascii
		String teste = "+-*/%!=<>&;,.()[]|\"_";

		for (int i = 0; i < teste.length(); i++){
			int c = teste.charAt(i);
			System.out.println(c);
		}

		String t = "teste ";
		char a = '1';
		t += a;
		System.out.println("String final: " + t);
	}
	// Criei esse metodo para retornar a lista de tokens 
	public ArrayList<Token> getTokens(){
		return tokenList;
	}
	public ArrayList<Token> geterroTokens(){
		return errorList;
	}

	private void initAnalyzer(){
		// Palavras Reservadas
		reservedWords.add("program");
		reservedWords.add("const");
		reservedWords.add("var");
		reservedWords.add("function");
		reservedWords.add("begin");
		reservedWords.add("end");
		reservedWords.add("if");
		reservedWords.add("then");
		reservedWords.add("else");
		reservedWords.add("while");
		reservedWords.add("do");
		reservedWords.add("read");
		reservedWords.add("write");
		reservedWords.add("integer");
		reservedWords.add("real");
		reservedWords.add("boolean");
		reservedWords.add("true");
		reservedWords.add("false");
		reservedWords.add("string");
		reservedWords.add("char");

		// Operadores Ariméticos
		opAritmetic.add("+");
		opAritmetic.add("*");
		opAritmetic.add("/");
		opAritmetic.add("-");
		opAritmetic.add("%");

		// Operadores Relacionais
		opRelational.add("!=");
		opRelational.add("=");
		opRelational.add("<");
		opRelational.add("<=");
		opRelational.add(">");
		opRelational.add(">=");

		// Operadores Lógicos
		opLogic.add("!");
		opLogic.add("&&");
		opLogic.add("||");
		
		// Delimitadores
		delimiters.add(";");
		delimiters.add(",");
		delimiters.add("(");
		delimiters.add(")");
		delimiters.add("[");
		delimiters.add("]");

	}
}