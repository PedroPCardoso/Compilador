package sintatic;

import java.util.ArrayList;
import utils.Token;


public class AnalisadorSintatico {

    private Token token; // Proximo token da lista
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na analise.
    private int contTokens = 0;         //contador que aponta para o proximo token da lista

    public void analise(ArrayList<Token> tokens) {

        this.tokens = tokens; //recebe os tokens vindos do lexico.
        token = proximo();  //recebe o primeiro token da lista
        erros = new ArrayList<>(); //cria a lista de erros

        programa();

        if (erros.size() != 0) {
            System.out.println("Ocorreram erros na analise sintatica");
            System.out.println("Erros Sintaticos:");
            System.out.println(erros); //imprime os erros na tela
            System.out.println("\r");
        } else {
            System.out.println("Analise Sintatica feita com sucesso\n");
        }

    }

    private void erroSintatico(String erro) {
        if (!token.getLexeme().equals("EOF")) {
            erros.add("Linha: " + (token.getLine()) + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            erros.add(erro);
        }
    }

    private String verificaTipo(String esperado) {
        if ((!token.getLexeme().equals("EOF")) && token.getType().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            String t = token.getLexeme();
            token = proximo();
            return t;
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado 
            return null;
        }
    }

    private void terminal(String esperado) {
        if ((!token.getLexeme().equals("EOF")) && token.getLexeme().equals(esperado)) { //verifica se o token atual e o que era esperado
            token = proximo();
        } else {
            erroSintatico("Falta o token: " + esperado);   //gera o erro se o token nao e o esperado 
        }
    }

    private Token proximo() {
        if (contTokens < tokens.size()) { //verifica se ainda possuem tokens para a analise.
            return tokens.get(contTokens++);
        } else {
            return new Token("EOF", "EOF",0, 0);  //cria um token de fim de arquivo. 
        }
    }

    public ArrayList<String> getErros() {
        return this.erros;
    }

    private void programa() {
        if (token.getLexeme().equals("program")) {
            token = proximo();
            if(!token.getLexeme().equals("EOF")){
            switch (token.getLexeme()) {
                case "var":
                    blocoVar();
                    break;
                case "const":
                    blocoConst();
                    break;
                case "function":
                    Funcao();
                    break;
                default:
                erroSintatico("Inicialize um bloco de constante, variavel ou funcao");	
            }
            }
            
        } else {
            erroSintatico("Esperava Program");
        }
    }

    //bloco de variaveis
    private void blocoVar() {
        token = proximo();
        if (token.getLexeme().equals("begin")) {
            token = proximo();
            while (!(token.getLexeme().equals("end") || token.getLexeme().equals("const") || token.getLexeme().equals("function"))) {
                if (tipoPrimitivo()) {
                    declaracaoVariaveis();
                    if (token.getLexeme().equals("end")) {
                        break;
                    }
                } else {
                    token = proximo();
                    if (token.getLexeme().equals("EOF") || token.getLexeme().equals("const") || token.getLexeme().equals("function")) {
                        break;
                    }
                }
            }
            if (!token.getLexeme().equals("end")) {
                erroSintatico("Bloco de variaveis nao possui end");
            } else {
                token = proximo();
            }
            if (token.getLexeme().equals("const")) {
                blocoConst();
            }
            if (token.getLexeme().equals("function")) {
                Funcao();
            }
        } else {
            erroSintatico("Bloco de variavel nao possui begin");

        }

        if (token.getLexeme().equals("const")) {
            blocoConst();
        }
        if (token.getLexeme().equals("function")) {
            Funcao();
        }
    }

    private void declaracaoVariaveis() {
        //verifica se e um tipo

        String tipo = token.getLexeme();
        tipo();
        padraoVariavel();
        if (!(pontoVirgula())) {
            erroSintatico("Faltou ; "); //gera o erro se o tipo do token nao e o esperado 
            if (token.getLexeme().equals("end")) {
                return;
            }
            if (tipoPrimitivo()) {
                declaracaoVariaveis();
            }
        }

    }

    private void padraoVariavel() {
        identificador("Identificador");
        opcVariavel();
        virgula();
    }

    private void opcVariavel() {
        if (token.getLexeme().equals("[")) {
            declaracaoMatriz();
        }
        if (token.getLexeme().equals("=")) {
            token = proximo();
            if (token.getLexeme().equals("[")) {
                atribuicaoMatriz();
            } else if (token.getType().equals("Char") || token.getType().equals("Cadeia de Caractere") || token.getType().equals("Numero") || token.getLexeme().equals("false") || token.getLexeme().equals("true")) {
                System.out.println("Atribuicao tipo" + token.getLexeme());

                token = proximo();

            } else {
                erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado 

                if (tipoPrimitivo()) {
                    declaracaoVariaveis();
                }
            }
        } else if (token.getLexeme().equals(",")) {
            virgula();
        }
    }

    private void virgula() {
        if (token.getLexeme().equals(",")) {
            token = proximo();
            padraoVariavel();
        }
    }

    private boolean pontoVirgula() {
        if (token.getLexeme().equals(";")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }

    private void blocoConst() {
        token = proximo();
        if (token.getLexeme().equals("begin")) {
            token = proximo();
            while (!(token.getLexeme().equals("end") || token.getLexeme().equals("function") || token.getLexeme().equals("EOF"))) {
                if (tipoPrimitivo()) {
                    declaracao_const();
                } else {
                    token = proximo();
                }
            }
            if (token.getLexeme().equals("end")) {
                token = proximo();
            } else {
                erroSintatico("Bloco de constantes nao possui end");
            }
            if (token.getLexeme().equals("function")) {
                Funcao();
            }
        } else {
            erroSintatico("Bloco de constates nao possui begin");
        }
        if (token.getLexeme().equals("function")) {
            Funcao();
        }
    }

    private void declaracao_const() {
        token = proximo();
        padraoConst();
    }

    private void padraoConst() {
        if (token.getType().equals("Identificador")) {
            token = proximo();
            if (token.getLexeme().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexeme().equals("=")) {
                token = proximo();
                if (token.getLexeme().equals("[")) {
                    atribuicaoMatriz();
                } else if (token.getType().equals("Char") || token.getType().equals("Cadeia de Caractere") || token.getType().equals("Numero") || token.getLexeme().equals("false") || token.getLexeme().equals("true")) {
                    System.out.println("Atribuicao tipo" + token.getLexeme());
                    token = proximo();
                } else {
                    erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado 
                    if (tipoPrimitivo()) {
                        declaracao_const();
                    }
                }
            } else {
                erroSintatico("Constantes precisam ser atribuidas"); //gera o erro se o tipo do token nao e o esperado 
            }
            if (token.getLexeme().equals(";")) {
                token = proximo();
            }
        }

    }
    
    private void blocoFuncao(){
        token = proximo (); // pega o proximo que deveria ser um ID 
        if(token.getType().equals("Identificador")){
            token = proximo();
            if (token.getLexeme().equals("(")){
               token = proximo();
               if(!token.getLexeme().equals(")")){
                   parametroFuncao();
               }
               if (token.getLexeme().equals(")")) {
                   token = proximo();
               }
               else{
                   erroSintatico("Faltou ) na funcao");
               }
               if (token.getLexeme().equals(":")){
                   temRetorno();
               }
               if (token.getLexeme().equals("begin")){
            	   System.out.println(token.getLexeme());
                   bFuncao();
                   if(token.getLexeme().equals("end")){
                       token = proximo();
                   }
                   else{
                       erroSintatico("Faltou end na funcao");
                   }
               }
               else{
                   erroSintatico("Faltou begin na funcao"); 
               }
            }
            else{
               erroSintatico("Faltou ( na funcao");  
            }
        }
        else{
           erroSintatico("A funcao deve ser incializada com um nome"); 
        }
    }
    
    private void bFuncao(){
        token = proximo();
        System.out.println("Entrou aqui 1"+ token.getLexeme());
        switch (token.getLexeme()){
        case "read":
         read();
        
        
        }
    }
    
    private void parametroFuncao(){
        if (tipoPrimitivo()){
                token = proximo();
            if(token.getType().equals("Identificador")){
                token = proximo ();
                if(token.getLexeme().equals(",")){
                    token = proximo();
                    parametroFuncao();
                }
                else if(!token.getLexeme().equals(")")){
                    parametroFuncao();
                }
            }
            else{
               erroSintatico("Parametro de funcao incorreto");  
            }
        }
         else{
            erroSintatico("Parametro de funcao incorreto");  
            token = proximo();
            if(!(token.getLexeme().equals(")")||token.getLexeme().equals("EOF")||token.getLexeme().equals("function"))){
                parametroFuncao();
            }
            if(token.getLexeme().equals("function")){
                Funcao();
            }
            }
    }
    
    private void temRetorno(){
        if (tipoPrimitivo()){
            token = proximo();
             }
            }
    
    private void Funcao() {
        blocoFuncao();
        if(token.getLexeme().equals("function")){
            token = proximo();
            Funcao();
        }
    }

    private void tipo() {
        switch (token.getLexeme()) {
            case "integer":
                terminal("integer");
                break;
            case "cadeia":
                terminal("cadeia");
                break;
            case "real":
                terminal("real");
                break;
            case "boolean":
                terminal("boolean");
                break;
            case "char":
                terminal("char");
                break;
            default:
                erroSintatico("falta palavra reservada: integer, cadeia, real, boolean, char");
                break;
        }

    }

    private void identificador(String esperado) {
        if (!token.getLexeme().equals("EOF") && token.getType().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            token = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado 
        }

    }

    public boolean tipoPrimitivo() {
        switch (token.getLexeme()) {
            case "integer":
                return true;
            case "string":
                return true;
            case "real":
                return true;
            case "boolean":
                return true;
            case "char":
                return true;

            default:
                erroSintatico("falta palavra reservada: integer, cadeia, real, boolean, char");
                return false;
        }
    }

    private void sincroniza() {
        while (!(token.getLexeme().equals(";"))) {
            token = proximo();
        }
    }

    private void declaracaoMatriz() {
        switch (token.getLexeme()) {
            case "[":
                token = proximo();
                if (token.getType().equals("Numero")) {
                    token = proximo();
                } 
                else if (!(token.getType().equals("Numero"))) {
                    erroSintatico("matriz incorreta");
                }
                if (token.getType().equals("numero")) {
                    token = proximo();
                }
                terminal("]");
                if (token.getLexeme().equals("[")) {
                    declaracaoMatriz();
                }
                break;
            default:
                break;
        }
    }

    private void atribuicaoMatriz() {
        switch (token.getLexeme()) {
            case "[":
                token = proximo();
                if (token.getType().equals("Char") || token.getType().equals("Cadeia de Caractere") || token.getType().equals("Numero") || token.getLexeme().equals("false") || token.getLexeme().equals("true")) {
                    token = proximo();
                } else {
                    erroSintatico("atribuicao de matriz incorreta");
                }
                if (token.getType().equals("numero")) {
                    token = proximo();
                }
                terminal("]");
                if (token.getLexeme().equals("[")) {
                    declaracaoMatriz();
                }
                break;
            default:
                break;
        }
    }

    private void write() {
        token = proximo();
        if (token.getLexeme().equals("(")) {
            token = proximo();
            parametroWrite();
            if (!(token.getLexeme().equals(")") || token.getLexeme().equals(";"))) {
                if (!token.getLexeme().equals(")")) {
                    // adicionar as outras opcoes aqui
                    erroSintatico("expressao write mal formada esperava )");
                    bFuncao();
                } else {
                    erroSintatico("expressao write mal formada esperava ;");
                    bFuncao();

                }
            } else if (token.getLexeme().equals(";")) {
                erroSintatico("expressao write mal formada esperava )");
                token = proximo();
            } else if (token.getLexeme().equals(")")) {
                token = proximo();
                if (!token.getLexeme().equals(";")) {
                    erroSintatico("expressao write mal formada esperava ;");
                    
                } else {
                    bFuncao();
                }

            }
        } else {
            erroSintatico("write nao possui (");
        }

    }

    private void parametroWrite() {
        if (token.getType().equals("Identificador")) {
            System.out.println("lexema e:" + token.getLexeme());
            token = proximo();
            if (token.getLexeme().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexeme().equals(",")) {
                token = proximo();
                parametroWrite();
            }
            if (token.getType().equals("Identifier")) {
                erroSintatico("Esperava ,");
                token = proximo();
            } else if (!token.getLexeme().equals(")")) {
                erroSintatico("Esperava Id ou ) ");
                token = proximo();
            }
        } // no write podemos atribuir "amora" cadeia, 'o'char, 1numero, 
        else if (token.getType().equals("string") || token.getType().equals("char") || token.getType().equals("number")) {
            token = proximo();
            if (token.getLexeme().equals(",")) {
                token = proximo();
                parametroWrite();
            } else if (!token.getLexeme().equals(")")) {
                erroSintatico("Esperava , ou ) ");
            }
        } else {
            erroSintatico("Parametro write incorreto, falta Id ");
        }
    }

    private void read() {
        token = proximo();
        if (token.getLexeme().equals("(")) {
            token = proximo();
            parametroRead();
            if (!(token.getLexeme().equals(")") || token.getLexeme().equals(";"))) {
                if (!token.getLexeme().equals(")")) {
                    // adicionar as outras opcoes aqui
                    erroSintatico("expressao read mal formada esperava )");
                    bFuncao();
                } else {
                    erroSintatico("expressao read mal formada esperava ;");
                    bFuncao();

                }
            } else if (token.getLexeme().equals(";")) {
                erroSintatico("expressao read mal formada esperava )");
                token = proximo();
            } else if (token.getLexeme().equals(")")) {
                token = proximo();
                if (!token.getLexeme().equals(";")) {
                    erroSintatico("expressao read mal formada esperava ;");
                    bFuncao();
                } else {
                    bFuncao();
                }

            }
        } else {
            erroSintatico("read nao possui (");
        }
    }

    private void parametroRead() {
        if (token.getType().equals("Identificador")) {
            System.out.println("lexema e:" + token.getLexeme());
            token = proximo();
            if (token.getLexeme().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexeme().equals(",")) {
                token = proximo();
                parametroRead();
            }
            if (token.getType().equals("Identifier")) {
                erroSintatico("Esperava ,");
                token = proximo();
            } else if (!token.getLexeme().equals(")")) {
                erroSintatico("Esperava Id ou ) ");
                token = proximo();
            }
        } else {
            erroSintatico("Parametro read incorreto, falta Id ");
        }
    }



}
