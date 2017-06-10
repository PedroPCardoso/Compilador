package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import sintatic.AnalisadorSintatico;
import main.Arquivo;
import analyzers.Lexical;
import utils.EntryReader;
import utils.Token;

import javax.swing.JOptionPane;

public class Compilador {
	private boolean hasFiles = false;
	File entryFiles[];
	String[] pathToSave;
	Arquivo arquivo = new Arquivo();
	AnalisadorSintatico sintatico = new AnalisadorSintatico();
	
	public static void main(String[] args) throws IOException {

		Compilador n = new Compilador();
		n.readAllFiles("entrada");
	
		
		if (n.compilate()){
			System.out.println("**Compilacao concluida**");
		} else {
			System.out.println("Falha na compilacao");
		}
	}
	
	
//	Funcao de debug - Pode apagar pos termino
	public void printPaths(){
		if (entryFiles == null){
			System.out.println("Nao existem arquivos no atributo.");
			return;
		}
		if (entryFiles.length == 0){
			System.out.println("Nao existem arquivos no atributo.");
			return;
		}
		for (int i = 0; i < entryFiles.length; i++){
			//System.out.println(entryFiles[i]);
		}
	}
	
	
	
//	rotina responsavel por receber o caminho do 
	public void readAllFiles(String path) {
		
		File directory = new File(path);
		
		
		entryFiles = directory.listFiles();
		
		
		if (entryFiles.length > 0){
			hasFiles = true;
		}
		
	}
	
	public boolean compilate() throws IOException{
		if (!hasFiles){
			JOptionPane.showMessageDialog(null, "Não existem arquivos de entrada!");
			System.out.println("Não existem arquivos de entrada");
			return false;
		} else {
			System.out.println("Quantidade de arquivos: " + entryFiles.length);
			for (int i = 0; i < entryFiles.length; i++){
				String name = entryFiles[i].getName();
				System.out.println("Iniciando analise lexica em:"+ entryFiles[i].getName());
				EntryReader reader = new EntryReader(entryFiles[i].getPath());
				reader.readEntry();
				reader.codeStringToChar();
		
				Lexical lex = new Lexical(); 
				lex.analyze(reader.getCodigo());
				lex.saveResult(entryFiles[i].getName());
				
				sintatico = new AnalisadorSintatico();
				ArrayList<Token> listaTokens = lex.getTokens();
				System.out.println("Iniciando a analise sintatica ...");
				System.out.println("Analisando: " + name);
				sintatico.analise(listaTokens);
				arquivo.gravaSaidaSintatico(sintatico.getErros(), name);
			
			}
			
			printPaths();
			return true;
		}
	}

}
