package utils;

/**
 * Classe responsável por gerenciar os Tokens do Compilador
 */
public class Token{
	private String type;
	private String lexeme;
	private int line;
	private int col;

	/**
	 * Construtor da classe Token
	 * @param tipo do token
	 * @param lexema do token
	 * @param linha do token
	 * @param posição na coluna
	 * @return null
	 */
	public Token(String lexeme, String type, int line, int col){
		this.type = type;
		this.lexeme = lexeme;
		this.line = line;
		this.col = col;
	}

	/*
		Getters
	 */
	public String getType(){
		return type;
	}
	public String getLexeme(){
		return lexeme;
	}
	public int getLine(){
		return line;
	}
	public int getCol(){
		return col;
	}

	/*
		Setters
	 */
	public void setType(String type){
		this.type = type;
	}
	public void setLexeme(String lexeme){
		this.lexeme = lexeme;
	}
	public void setLine(int line){
		this.line = line;
	}
	public void setCol(int col){
		this.col = col;
	}

}