package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class EntryReader {

	private String codigo = "";

	private char[] caracters;
	private String fileToRead = "";

	public EntryReader(String fileToRead) {
		this.fileToRead = fileToRead;

	}

	public void readEntry() {

		try {

			FileInputStream leitorDeArquivo;
			leitorDeArquivo = new FileInputStream(fileToRead);
			InputStreamReader ler_texto = new InputStreamReader(leitorDeArquivo);
			BufferedReader br = new BufferedReader(ler_texto);
			String linha = br.readLine();

			while (linha != null) {
				codigo = codigo + linha + "\n";
				linha = br.readLine();
			}
			// codeStringToChar();
			// debug();

			br.close();
			leitorDeArquivo.close();
			ler_texto.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void debug(){
		System.out.println(caracters);
		System.out.println("Codigo lido:" + codigo);
	}

	public void codeStringToChar() {
		// codigo = addEspacosEntrada(codigo);
		caracters = codigo.trim().toCharArray();
		for (int i = 0; i < caracters.length; i++){
			if (caracters[i] == '\t')
				caracters[i] = ' ';
		}
	}

	public char[] getCaracteres() {
		return caracters;
	}
	
// 	public void readAllFiles() {

// 		File arquivos[];
// 		File diretorio = new File("/entrada");
		
// 		// Array com os diretórios de todos os arquivos
// 		arquivos = diretorio.listFiles();
		
// //		System.out.println("Numero de arquivos encontrados: " + arquivos.length);
// //		
// //		for(int i = 0; i < arquivos.length; i++){
// //			System.out.println(arquivos[i]);
// //		}
// 	}

	public String addEspacosEntrada(String textoEntrada) {

		String novaString = textoEntrada;
		
		// Seperando operadores complexos
		novaString = novaString.replaceAll("\\<\\>", " <> ");
		novaString = novaString.replaceAll("([[a-zA-Z_0-9]| ])\\<\\<([a-zA-Z_0-9])", "$1 << $2");
		novaString = novaString.replaceAll("([[a-zA-Z_0-9]| |_])\\<([a-zA-Z_0-9])", "$1 < $2");
		novaString = novaString.replaceAll("([[a-zA-Z_0-9]| |_])\\>([a-zA-Z_0-9])", "$1 > $2");
		novaString = novaString.replaceAll("\\<\\<\\<", " <<< ");
		novaString = novaString.replaceAll("\\>\\>\\>", " >>> ");
		novaString = novaString.replaceAll("\\<\\.\\>", " < . > ");
		

		// Seperando operadores aritmeticos 
		novaString = novaString.replaceAll("\\+", " + ");

		novaString = novaString.replaceAll("\\-", " -");

		novaString = novaString.replaceAll("\\*", " * ");

		novaString = novaString.replaceAll("/", " / ");

		// Separando delimitadores

		novaString = novaString.replaceAll("\\;", " ; ");
		novaString = novaString.replaceAll("\\,", " , ");
		novaString = novaString.replaceAll("\\(", " ( ");
		novaString = novaString.replaceAll("\\)", " ) ");

		// Separando os caracters constantes

		novaString = novaString.replaceAll("' ([a-zA-Z_0-9]) '", " '$1' ");

		novaString = novaString.replaceAll("' *([a-zA-Z_0-9])", " '$1");
		
		//Separando identificadores
		
		//novaString = novaString.replaceAll("(\\-)([[a-zA-Z_0-9]|_]*)", " $1 $2");
		
		novaString = novaString.replaceAll("(\\-)([a-zA-Z][[a-zA-Z_0-9]|_]*)", " $1 $2");
		
		novaString = novaString.replaceAll("(\\-)([0-9]*)", " $1$2");


		// Separando as cadeias constantes

		novaString = novaString.replaceAll("(\"[a-zA-Z][[a-zA-Z_0-9]| ]*\")", " $1 ");
		
		// Separador de simbolos

		//novaString = novaString.replaceAll("\\#", " # ");
		//novaString = novaString.replaceAll("\\%", " % ");
		//novaString = novaString.replaceAll("\\?", " ? ");
		//novaString = novaString.replaceAll("\\@", " @ ");
		//novaString = novaString.replaceAll("\\^", " ^ ");
		//novaString = novaString.replaceAll("\\~", " ~ ");
		novaString = novaString.replaceAll("\\|", " | ");
		novaString = novaString.replaceAll("(\\=)", " \\= ");
		novaString = novaString.replaceAll("\n", " \n");
		novaString = novaString.replaceAll("\t", " \t ");
		novaString = novaString.replaceAll("\\{", " {");
		novaString = novaString.replaceAll("\\}", "} ");
		
//		System.out.println(novaString);

		return novaString;

	}
	
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

}
