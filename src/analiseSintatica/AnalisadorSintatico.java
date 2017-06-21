package analiseSintatica;

import java.util.ArrayList;
import analiseLexica.Token;

public class AnalisadorSintatico {

    private Token token; // Proximo token da lista
    private ArrayList<Token> tokens;    //lista com os tokens recebidos
    private ArrayList<String> erros;    //lista com os erros encontrados na an�lise.
    private int contTokens = 0;         //contador que aponta para o proximo token da lista
    private int aux = 0;

    public void analise(ArrayList<Token> tokens) {

        this.tokens = tokens; //recebe os tokens vindos do lexico.
        token = proximo();  //recebe o primeiro token da lista
        erros = new ArrayList<>(); //cria a lista de erros

        programa();

        if (!erros.isEmpty()) {
            System.out.println("Ocorreram erros na analise sintatica");
            System.out.println(erros); //imprime os erros na tela
        } else {
            System.out.println("Analise Sintatica feita com sucesso\n");
        }

    }

    private void programa() {
        if (token.getLexema().equals("program")) {
            token = proximo();
            blocos();
        } else {
            erroSintatico("Esperava Program");
        }
    }

    private void blocos() {
        if (token.getLexema().equals("var")) {
            blocoVar();
        } else if (token.getLexema().equals("const")) {
            blocoConst();
        } else if (token.getLexema().equals("function")) {
            Funcao();
        } else {
            erroSintatico("Esperava um bloco de constates|bloco de variavel|bloco de funcao");
            token = proximo();
            if (!token.getLexema().equals("EOF")) {
                blocos();
            }
        }
    }

    private void erroSintatico(String erro) {
        if (!token.getLexema().equals("EOF")) {
            erros.add("Linha: " + (token.getLinha()) + " " + erro + "\n"); //gera o erro normalizado e adiciona na lista de erros.
        } else {
            erros.add(erro);
        }
    }

    private void terminal(String esperado) {
        if ((!token.getLexema().equals("EOF")) && token.getLexema().equals(esperado)) { //verifica se o token atual e o que era esperado
            token = proximo();
        } else {
            erroSintatico("Falta o token: " + esperado);   //gera o erro se o token nao e o esperado
        }
    }

    private Token proximo() {
        if (contTokens < tokens.size()) { //verifica se ainda possuem tokens para a analise.
            return tokens.get(contTokens++);
        } else {
            return new Token(0, 0, "EOF", "EOF");  //cria um token de fim de arquivo.
        }
    }

    public ArrayList<String> getErros() {
        return this.erros;
    }

    private void blocoVar() {
        token = proximo();
        if (token.getLexema().equals("begin")) {
            token = proximo();
            while (!(token.getLexema().equals("end") || token.getLexema().equals("const") || token.getLexema().equals("function"))) {
                if (tipoPrimitivo()) {
                    declaracaoVariaveis();
                    if (token.getLexema().equals("end")) {
                        break;
                    }
                } else {
                    token = proximo();
                    if (token.getLexema().equals("EOF") || token.getLexema().equals("const") || token.getLexema().equals("function")) {
                        break;
                    }
                }
            }
            if (!token.getLexema().equals("end")) {
                erroSintatico("Bloco de variaveis nao possui end");
            } else {
                token = proximo();
            }
            if (token.getLexema().equals("const")) {
                blocoConst();
            }
            if (token.getLexema().equals("function")) {
                Funcao();
            }
        } else {
            erroSintatico("Bloco de variavel nao possui begin");

        }

        if (token.getLexema().equals("const")) {
            blocoConst();
        }
        if (token.getLexema().equals("function")) {
            Funcao();
        }
    }

    private void declaracaoVariaveis() {
        tipo();
        padraoVariavel();
        if (!(pontoVirgula())) {
            erroSintatico("Faltou ; "); //gera o erro se o tipo do token nao e o esperado
            if (token.getLexema().equals("end")) {
                return;
            }
            if (tipoPrimitivo()) {
                declaracaoVariaveis();
            }
        }

    }

    private void padraoVariavel() {
        identificador("Identificador");
        opcVariavel(1);
        virgula();
    }

    private void opcVariavel(int n) {
        int opcao = n;
        aux = 0;
        if (token.getLexema().equals("[")) {
            declaracaoMatriz();
        }
        if (token.getLexema().equals("=")) {
            token = proximo();
            System.out.println("aqui" + token.getLexema());
            if (token.getLexema().equals("(")) {
                token = proximo();
                aux++;
            }
            if (token.getLexema().equals("-")) {
                token = proximo();
                System.out.println("sinal" + token.getLexema());
                if (!(token.getTipo().equals("Identificador") || token.getLexema().equals("("))) {
                    erroSintatico("atribuicao incorreta ");
                    System.out.println("errrrr" + token.getLexema());
                }
            }
            igualdade(opcao);

            if (token.getLexema().equals("(")) {
                token = proximo();
                aux--;
                System.out.println("Ultimo" + aux);
            }
            if (aux > 0) {
                erroSintatico("Faltou )");
            } else if (aux < 0) {
                erroSintatico("Faltou (");
            }

        } else if (token.getLexema().equals(",")) {
            virgula();
        }
    }

    private int igualdade(int opcaon) {
        int opcao = opcaon;
        //Se for uma matriz
        if (token.getLexema().equals("(")) {
            aux++;
            token = proximo();
            System.out.println("P" + aux);
        }
        if (token.getLexema().equals("[")) {
            atribuicaoMatriz();
        } //Se for uma atribuicao normal
        else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
            if (token.getTipo().equals("numero")) {
                token = proximo();
                if (temExpressaoA()) {
                    token = proximo();
                    igualdade(2);
                }
            } else {
                token = proximo();
                if (!token.getLexema().equals(";") && opcao == 2) {
                    erroSintatico("Atribuicao incorreta: " + token.getLexema());
                    while (!(verifica() || token.getLexema().equals(";") || token.getLexema().equals("EOF"))) {
                        erroSintatico("Atribuicao incorreta: " + token.getLexema());

                    }
                }

            }

        } //Se for uma declaracao de variaveis locais pode receber um id e chamada de funcoes
        else if (opcao == 2) {
            if (token.getTipo().equals("Identificador")) {
                token = proximo();
                if (termo()) {
                    token = proximo();
                }
                if (fator()) {
                    token = proximo();
                }
                if (token.getLexema().equals("(")) {
                    callFunction(1);
                }
                if (temExpressaoA()) {
                    token = proximo();
                    igualdade(2);
                }
                if (token.getLexema().equals(")")) {
                    aux--;
                    token = proximo();
                } else if (!token.getLexema().equals(";")) {
                    while (!(verifica() || token.getLexema().equals(";") || token.getLexema().equals("EOF"))) {
                        token = proximo();
                        erroSintatico("Atribuicao incorreta ");
                    }
                }
            } else {
                erroSintatico("Atribuicao incorreta 3");
                if (!token.getLexema().equals(";")) {
                    token = proximo();
                }
            }
        } else {
            erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado
            if (tipoPrimitivo() && opcao == 1) {
                declaracaoVariaveis();
            }

        }

        return aux;
    }

    private boolean temExpressaoA() {
        if (termo()) {
            return true;
        } else if (fator()) {
            return true;
        } else {
            return false;
        }
    }

    private void virgula() {
        if (token.getLexema().equals(",")) {
            token = proximo();
            padraoVariavel();
        }
    }

    private boolean pontoVirgula() {
        if (token.getLexema().equals(";")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }

    private void blocoConst() {
        token = proximo();
        if (token.getLexema().equals("begin")) {
            token = proximo();
            while (!(token.getLexema().equals("end") || token.getLexema().equals("function") || token.getLexema().equals("EOF"))) {
                if (tipoPrimitivo()) {
                    declaracao_const();
                } else {
                    token = proximo();
                }
            }
            if (token.getLexema().equals("end")) {
                token = proximo();
            } else {
                erroSintatico("Bloco de constantes nao possui end");
            }
            if (token.getLexema().equals("function")) {
                Funcao();
            }
        } else {
            erroSintatico("Bloco de constates nao possui begin");
        }
        if (token.getLexema().equals("function")) {
            Funcao();
        }
    }

    private void declaracao_const() {
        token = proximo();
        padraoConst();
    }

    private void padraoConst() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexema().equals("=")) {
                token = proximo();
                if (token.getLexema().equals("[")) {
                    atribuicaoMatriz();
                } else if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
                    token = proximo();
                } else {
                    erroSintatico("atribuicao de tipo incompativel"); //gera o erro se o tipo do token nao e o esperado
                    if (token.getLexema().equals("real") || token.getLexema().equals("integer") || token.getLexema().equals("string") || token.getLexema().equals("char") || token.getLexema().equals("boolean")) {
                        declaracao_const();
                    }
                }
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    padraoConst();
                }
            } else {
                erroSintatico("Constantes precisam ser atribuidas"); //gera o erro se o tipo do token nao e o esperado
            }
            if (token.getLexema().equals(";")) {
                token = proximo();
                if (token.getLexema().equals("real") || token.getLexema().equals("integer") || token.getLexema().equals("string") || token.getLexema().equals("char") || token.getLexema().equals("boolean")) {
                    declaracao_const();
                }
            }
        }

    }
    //Estrutura de uma funcao

    private void blocoFuncao() {
        token = proximo(); // pega o proximo que deveria ser um ID
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                if (!token.getLexema().equals(")")) {
                    parametroFuncao();
                }
                if (token.getLexema().equals(")")) {
                    token = proximo();
                } else {
                    erroSintatico("Faltou ) na funcao");
                }
                if (token.getLexema().equals(":")) {
                    temRetorno();
                }
                if (token.getLexema().equals("begin")) {
                    token = proximo();
                    bloco();
                    while (!(token.getLexema().equals("EOF") || token.getLexema().equals("end") || token.getLexema().equals("function"))) {
                        bloco();
                    }
                    if (token.getLexema().equals("function")) {
                        erroSintatico("Funcoes nao podem ser declaradas dentro de funcoes");
                        Funcao();
                    }
                    if (token.getLexema().equals("end")) {
                        token = proximo();
                    } else {
                        erroSintatico("Faltou end na funcao");
                    }

                } else {
                    erroSintatico("Faltou begin na funcao");
                }
            } else {
                erroSintatico("Faltou ( na funcao");
            }
        } else {
            erroSintatico("A funcao deve ser incializada com um nome");
        }
    }

    //Contem tudo que pode estar dentro de uma funcao
    private void bloco() {
        System.out.println("Entrou bloco" + token.getLexema());
        if (token.getLexema().equals("read")) {
            read();
        } else if (token.getLexema().equals("write")) {
            write();
        } else if (token.getTipo().equals("Identificador")) {
            opcId();
        } else if (token.getLexema().equals("if")) {
            ifElse();
        } else if (token.getLexema().equals("while")) {
            _while();
        } else if (tipo()) {
            variaveisLocais();
        } else if (token.getLexema().equals("EOF")) {

        } // Esta aceitando bloco vazio
        else if (token.getLexema().equals("end")) {

        } else {
            erroSintatico("Esperava componente do bloco ");
            System.out.println(token.getLexema());
            token = proximo();
        }
    }

    private void variaveisLocais() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            opcVariavel(2);
            virgula();
            if (!(pontoVirgula())) {
                erroSintatico("Faltou ; "); //gera o erro se o tipo do token nao e o esperado
            }
        } else {
            erroSintatico("Esperava Id");
        }
    }

    private void opcId() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                callFunction(0);
            } else if (token.getLexema().equals("->")) {

                token = proximo();
                retorno();
            } else if (token.getLexema().equals(";")) {
                token = proximo();
            } else if (token.getLexema().equals("=")) {
                opcVariavel(2);
                virgula();
                if (!(pontoVirgula())) {
                    //gera o erro se o tipo do token nao e o esperado
                    erroSintatico("Faltou ; na atribuicao");
                }
            } else {

            }
        } else {
            erroSintatico("Esperava um Id");
        }
    }

    private void estruturaCondicional() {
        if (token.getLexema().equals("begin")) {
            token = proximo();
            bloco();
            if (token.getLexema().equals("end")) {
                token = proximo();
            } else {
                erroSintatico("Esperava end");
            }
        } else {
            erroSintatico("Esperava begin");
        }
    }

    private void _while() {
        if (token.getLexema().equals("while")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                parametrosIf();
                if (token.getLexema().equals(")")) {
                    token = proximo();
                    if (token.getLexema().equals("do")) {
                        estruturaCondicional();
                    } else {
                        erroSintatico("Esperava do");
                    }
                } else {
                    erroSintatico("Esperava )");
                }
            } else {
                erroSintatico("Esperava (");
            }
        }

    }

    private void ifElse() {
        _If();
        _Else();

    }

    private void _If() {
        if (token.getLexema().equals("if")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                parametrosIf();
                if (token.getLexema().equals(")")) {
                    token = proximo();
                    estruturaCondicional();
                } else {
                    erroSintatico("Esperava )");
                }
            } else {
                erroSintatico("Esperava (");
            }
        }

    }

    private void _Else() {
        if (token.getLexema().equals("else")) {
            token = proximo();
            if (token.getLexema().equals("begin")) {
                token = proximo();
                if (token.getLexema().equals("end")) {
                    token = proximo();
                } else {
                    bloco();
                    if (!token.getLexema().equals("end")) {
                        erroSintatico("Esperava end");
                    }
                }
            } else {
                erroSintatico("Esperava begin");
            }
        }
    }

    //Opcoes de parametros if ou while
    private void parametrosIf() {
        System.out.println("lexem"+token.getLexema());
        exp_rel();
        
    }

    private void parametroFuncao() {
        if (tipoPrimitivo()) {
            token = proximo();
            if (token.getTipo().equals("Identificador")) {
                token = proximo();
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    parametroFuncao();
                } else if (!token.getLexema().equals(")")) {
                    parametroFuncao();
                }
            } else {
                erroSintatico("Parametro de funcao incorreto");
            }
        } else {
            erroSintatico("Parametro de funcao incorreto");
            token = proximo();
            if (!(token.getLexema().equals(")") || token.getLexema().equals("EOF") || token.getLexema().equals("function"))) {
                parametroFuncao();
            }
            if (token.getLexema().equals("function")) {
                Funcao();
            }
        }
    }

    private void temRetorno() {
        if (tipoPrimitivo()) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                if (token.getLexema().equals(")")) {
                    token = proximo();

                } else {
                    erroSintatico("Esperava)");
                }
            } else {
                erroSintatico("Esperava (");
            }
        }
    }

    private void retorno() {
        if (token.getLexema().equals("(")) {
            token = proximo();
            parametroRetorno();

            if (token.getLexema().equals(")")) {
                token = proximo();
            } else {
                erroSintatico("Esperava )");
                if (!token.getLexema().equals(";")) {
                    verifica();
                }
            }

        }
        if (!token.getLexema().equals(";")) {
            erroSintatico("Esperava ;");
            while (!(verifica() || token.getLexema().equals(";") || token.getLexema().equals(")") | token.getLexema().equals("EOF"))) {
                erroSintatico("Retorno incorreto");
            }
            if (token.getLexema().equals(")")) {
                token = proximo();
                if (!(token.getLexema().equals(";"))) {
                    erroSintatico("Esperava ;");
                } else {
                    token = proximo();
                }

            }
        } else {
            token = proximo();
        }

    }

    private void parametroRetorno() {
        //Falta colocar expressoes
        if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getLexema().equals("true") || token.getLexema().equals("false")) {
            token = proximo();
        } else if (!token.getLexema().equals(")")) {
            erroSintatico("Esperava parametro do retorno ou )");
            verifica();
        }
    }

    private boolean verifica() {
        if (token.getLexema().equals("read") || token.getLexema().equals("write") || token.getLexema().equals("if") || token.getLexema().equals("while") || token.getTipo().equals("Identificador") || tipo()) {
            bloco();
            return true;
        } else {
            token = proximo();
            return false;
        }
    }

    private void Funcao() {
        blocoFuncao();
        if (token.getLexema().equals("function")) {
            token = proximo();
            Funcao();
        }
    }

    //Verifica tipo sem retornar um erro
    private boolean tipo() {
        switch (token.getLexema()) {
            case "integer":
                terminal("integer");
                return true;

            case "string":
                terminal("string");
                return true;

            case "real":
                terminal("real");
                return true;
            case "boolean":
                terminal("boolean");
                return true;
            case "char":
                terminal("char");
                return true;
            default:
                return false;
        }

    }

    private void identificador(String esperado) {
        if (!token.getLexema().equals("EOF") && token.getTipo().equals(esperado)) { //verifica se o tipo do token atual e o que era esperado
            token = proximo();
        } else {
            erroSintatico("falta " + esperado); //gera o erro se o tipo do token nao e o esperado
        }

    }

    public boolean tipoPrimitivo() {
        switch (token.getLexema()) {
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

    private void declaracaoMatriz() {
        switch (token.getLexema()) {
            case "[":
                token = proximo();
                if (token.getTipo().equals("numero")) {
                    token = proximo();
                } //loop infinito quando a matriz ta errada
                else if (!(token.getTipo().equals("numero"))) {
                    erroSintatico("declaracao de matriz incorreta");
                }
                if (token.getTipo().equals("numero")) {
                    token = proximo();
                }
                terminal("]");
                if (token.getLexema().equals("[")) {
                    declaracaoMatriz();
                }
                break;
            default:
                break;
        }
    }

    private void atribuicaoMatriz() {
        token = proximo();
        if (token.getTipo().equals("char") || token.getTipo().equals("string") || token.getTipo().equals("numero") || token.getLexema().equals("false") || token.getLexema().equals("true")) {
            token = proximo();
            if (token.getLexema().equals(",")) {
                atribuicaoMatriz();
            } else {
                terminal("]");
            }
        } else {
            erroSintatico("atribuicao de matriz incorreta");
        }

    }

    private void write() {
        token = proximo();
        if (token.getLexema().equals("(")) {
            token = proximo();
            parametroWrite();
            if (!(token.getLexema().equals(")") || token.getLexema().equals(";"))) {
                if (!token.getLexema().equals(")")) {
                    // adicionar as outras opcoes aqui
                    erroSintatico("expressao write mal formada esperava )");
                    bloco();
                } else {
                    erroSintatico("expressao write mal formada esperava ;");
                    bloco();

                }
            } else if (token.getLexema().equals(";")) {
                erroSintatico("expressao write mal formada esperava )");
                token = proximo();
            } else if (token.getLexema().equals(")")) {
                token = proximo();
                if (!token.getLexema().equals(";")) {
                    erroSintatico("expressao write mal formada esperava ;");

                } else {
                    bloco();
                }

            }
        } else {
            erroSintatico("write nao possui (");
        }

    }

    private void parametroWrite() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroWrite();
            }
            if (token.getTipo().equals("Identifier")) {
                erroSintatico("Esperava ,");
                token = proximo();
            } else if (!token.getLexema().equals(")")) {
                erroSintatico("Esperava Id ou ) ");
                token = proximo();
            }
        } //FALTA COLOCAR EXPRESSOES ARITMETICAS( tirar numero)
        else if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getTipo().equals("number")) {
            token = proximo();
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroWrite();
            } else if (!token.getLexema().equals(")")) {
                erroSintatico("Esperava , ou ) ");
            }
        } else {
            erroSintatico("Parametro write incorreto, falta Id ");
        }
    }

    private void read() {
        token = proximo();
        if (token.getLexema().equals("(")) {
            token = proximo();
            System.out.println("read:" + token.getLexema());
            parametroRead();
            if (!(token.getLexema().equals(")") || token.getLexema().equals(";") || token.getLexema().equals("EOF"))) {
                if (!token.getLexema().equals(")")) {
                    // adicionar as outras opcoes aqui
                    erroSintatico("expressao read mal formada esperava )");
                    bloco();
                }
            } else if (token.getLexema().equals(";")) {
                erroSintatico("expressao read mal formada esperava )");
                token = proximo();
            } else if (token.getLexema().equals(")")) {
                token = proximo();
                if (!token.getLexema().equals(";")) {
                    erroSintatico("expressao read mal formada esperava ;");
                    bloco();
                } else {
                    token = proximo();
                }

            }
        } else {
            erroSintatico("read nao possui (");
        }
    }

    private void parametroRead() {
        if (token.getTipo().equals("Identificador")) {
            token = proximo();
            if (token.getLexema().equals("[")) {
                declaracaoMatriz();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroRead();
            }
            if (token.getTipo().equals("Identifier")) {
                erroSintatico("Esperava ,");
                token = proximo();
            } else if (!token.getLexema().equals(")")) {
                erroSintatico("Esperava Id ou ) ");
                token = proximo();
            }
        } else {
            erroSintatico("Parametro read incorreto, falta Id ");
            token = proximo();
            if (token.getLexema().equals(",")) {
                token = proximo();
                parametroRead();
            }
        }
    }

    private void parameterCallFunction() {
        if (token.getTipo().equals("string") || token.getTipo().equals("char") || token.getLexema().equals("true") || token.getLexema().equals("false") || token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
            if (token.getTipo().equals("Identificador")) {
                token = proximo();
                if (token.getLexema().equals("[")) {
                    declaracaoMatriz();
                }
            } else {

                token = proximo();
            }
            if (token.getLexema().equals(",")) {
                token = proximo();
                parameterCallFunction();
            }
        } else {
            erroSintatico("Parametro invalido :" + token.getLexema());
            if (!verifica()) {
                if (token.getLexema().equals(",")) {
                    token = proximo();
                    parameterCallFunction();
                }
            }
        }
    }

    private void callFunction(int opc1) {
        int opc = opc1;
        if (token.getLexema().equals("(")) {
            token = proximo();
            if (!token.getLexema().equals(")")) {

                parameterCallFunction();
            }
        }
        if (token.getLexema().equals(")")) {
            token = proximo();
            if (!token.getLexema().equals(";") && opc == 0) {
                erroSintatico("falta ;");
            } else {
                if (opc == 0) {
                    token = proximo();
                }
            }

        } else {
            System.out.println("erroaqui" + token.getLexema());
            erroSintatico("falta )");
            //colocar if verificando se o atual nao e alguma das outras opcoes
            token = proximo();
        }
    }

    private void expressoes() {
        exp();
        exp_Log();
        exp_rel();
    }

    //Expressoes Aritmeticas

    private void exp() {
        exp_A1();
        expAux1();
    }

    private void exp_A1() {
        valorNumerico();
        exp_Aux4();
    }

    private void expAux1() {
        exp_SomSub();
        
    }

    private void exp_SomSub() {
        if (fator()) {
            exp();
        }
    }
    private boolean fator(){
        if(token.getLexema().equals("+")||token.getLexema().equals("-")){
            token = proximo();
            return true;
        }
        else{
            return false;
        }
    }
    private void exp_Aux4() {
        exp_MultDiv();
    }

    private void exp_MultDiv() {
        if (termo()) {
             token = proximo();
            exp();
        }
    }

    private boolean termo() {
        if (token.getLexema().equals("*") || token.getLexema().equals('/') || token.getLexema().equals("%")) {
            return true;
        }
        return false;
    }

    private boolean valorNumerico() {
        if (token.getLexema().equals("(")) {
            token = proximo();
            exp();
            if (!token.getLexema().equals(")")) {
                erroSintatico("Faltou )");
                return true;
            } else {
                token = proximo();
                return true;
            }
        } else if (idAritmetico()) {
            return true;
        } else if (token.getTipo().equals("Identificador")) {
            verifId();
            return true;
        } else if (token.getTipo().equals("numero")) {
            token = proximo();
            return true;
        }else{
            return false;
        }
    }

    private void verifId() {
        if (token.getLexema().equals("(")) {
            callFunction(1);
        } else if (token.getLexema().equals("[")) {
            declaracaoMatriz();
        } else {
            token = proximo();
        }
    }

    private boolean idAritmetico() {
        if (token.getLexema().equals("-")) {
            token = proximo();
            if (token.getTipo().equals("Identificador") || token.getTipo().equals("numero")) {
                token = proximo();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
    //Expressoes Relacionais
    public boolean exp_rel() {
       
        exp();
        if (exp_relaux()) {
            return true;
        } else {
            return false;
        }
    }
    public boolean exp_relaux() {
        if (operadorRelacional()) {
            exp();
            return true;
        } else if (token.getLexema().equals("==")) {
            token = proximo();
            if (token.getLexema().equals("true")) {
                token = proximo();
                return true;
            } else if (token.getLexema().equals("false")) {
                token = proximo();
                return true;
            } else {
                token = proximo();
                exp();
                return true;
            }
        } else {
            return false;
        }
    }
    public boolean operadorRelacional() {
        if (token.getLexema().equals("!=")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals(">")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals("<")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals(">=")) {
            token = proximo();
            return true;
        } else if (token.getLexema().equals("<=")) {
            token = proximo();
            return true;
        } else {
            return false;
        }
    }

    //Expressoes logicas
    private void exp_Log() {
        if (negacao()) {
            System.out.println("Entrou negacao");
        } else if (possibilidades()) {
            System.out.println("Entrou poss");
        } else if (token.getLexema().equals("(")) {
            exp_Log();
            if (token.getLexema().equals(")")) {
                token = proximo();
            } else {
                erroSintatico("Esperava )");
            }
        } else if ((operadorLogico() || operadorRelacional())) {
            expressoes();
        }
    }
    private void p2() {
        if (operadorLogico()) {
            possibilidades_aux();
        } else {
            erroSintatico("Esperava um operador logico");
        }
    }
    private boolean possibilidades() {
        if (exp_rel()) {
            p2();
            return true;
        } else if (negacao()) {
            p2();
            return true;
        } else if (token.getLexema().equals("true")) {
            p2();
            return true;
        } else if (token.getLexema().equals("false")) {
            p2();
            return true;
        }else if(exp()){
            if (operadorLogico()) {
            if(!possibilidades2()){
                possibilidades();
            }
            } else {
            erroSintatico("Esperava um operador logico");
        }
        }
            else {
            return false;
        }
    }
    private void possibilidades_aux() {
        possibilidades2();
        possibilidades();
    }
    private boolean possibilidades2() {
        if(exp_rel()){
            return true;
        }
        else if(negacao()){
            return true;
        }
        else if(exp()){
            return true;
        }
        else if (token.getLexema().equals("true")) {
            token = proximo();
            return true;
        }
        else if (token.getLexema().equals("false")) {
            token = proximo();
            return true;
        }
        else{
            return false;
        }
    }
    public boolean operadorLogico() {
        if (token.getLexema().equals("&&")) {
            token = proximo();
        } else if (token.getLexema().equals("||")) {
            token = proximo();
        } else {
            return false;
        }
    }
    public void ops() {
        if (!(operadorLogico() || operadorRelacional())) {
            erroSintatico("Esperava um operador logico ou relacional");
        }
    }
    private boolean negacao() {
        if (token.getLexema().equals("!")) {
            token = proximo();
            if (token.getLexema().equals("(")) {
                token = proximo();
                negacaoAux();
                if (token.getLexema().equals(")")) {
                    token = proximo();
                    return true;
                } else {
                    erroSintatico("Esperava )");
                }
            } else {
                erroSintatico("Esperava (");
            }
        } else {
            return false;
        }
    }
    private void negacaoAux() {
        expressoes();
    }
}
