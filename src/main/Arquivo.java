package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import utils.Token;
    

public class Arquivo {
    	private String localFile;

        	public ArrayList<String> lerCodigos() {

		ArrayList<String> codigos = new ArrayList<>(); 
        File caminho = new File("test/Entrada/");
        for (File f : caminho.listFiles()) {
            codigos.add(f.getName());
        }
        return codigos;
    }
        
	public void gravaSaidaSintatico(ArrayList<String> erros, String name) throws IOException {
		this.localFile = name;
        FileWriter arq = new FileWriter("saida/Sintatico/"  + this.localFile , false);  
        PrintWriter gravar = new PrintWriter(arq);
        if (erros.isEmpty()) { 
            gravar.printf("\nNao existem erros Sintaticos\n");
        } else { 
            for (String erro : erros) {
                gravar.println("Erro: " + erro);
            }
        }
        arq.close();
	}
	public String getLocalFile(){
		return this.localFile;
	}

}
