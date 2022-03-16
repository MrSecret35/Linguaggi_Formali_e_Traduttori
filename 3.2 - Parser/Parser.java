import java.io.*;

public class Parser {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;

    public Parser(Lexer l, BufferedReader br) {
        lex = l;
        pbr = br;
        move();
    }

    void move() {
        look = lex.lexical_scan(pbr);
        System.out.println("token = " + look);
    }

    void error(String s) {
	   throw new Error("near line " + lex.line + ": " + s);
    }

    void match(int t) {
    	if (look.tag == t) {
    	    if (look.tag != Tag.EOF) move();
    	} else error("syntax error1");
    }

    private void prog(){
        switch(look.tag){
            case '(':
                stat();
                match(Tag.EOF);
            break;

            default:
                error("syntax error"); 
        }
    }

    private void stat(){
        switch(look.tag){
            case '(':
                match('(');
                statp();
                match(')');
            break;

            default:
                error("syntax error"); 
        }
    }

    private void statp(){
        switch(look.tag){
            case '=':
                match(Token.assign.tag);
                match(Tag.ID);
                expr();
            break;

            case Tag.COND:
                match(Tag.COND);
                bexpr();
                stat();
                elseopt();
            break;

            case Tag.WHILE:
                match(Tag.WHILE);
                bexpr();
                stat();
            break;

            case Tag.DO:
                match(Tag.DO);
                statlist();
            break;

            case Tag.PRINT:
                match(Tag.PRINT);
                exprlist();
            break;

            case Tag.READ:
                match(Tag.READ);
                match(Tag.ID);
            break;

            default:
                error("syntax error"); 
        }
    }

    private void statlist(){
        switch(look.tag){
            case '(':
                stat();
                statlistp();
            break;

            default:
                error("syntax error"); 
        }
    }

    private void statlistp(){
        switch(look.tag){
            case '(':
                stat();
                statlistp();
            break;

            case ')':
            break;

            default:
                error("syntax error"); 
        }
    }

    private void bexpr(){
        switch(look.tag){
            case '(':
                match('(');
                bexprp();
                match(')');
            break;

            default:
                error("syntax error"); 
        }
    }

    private void bexprp(){
        switch(look.tag){
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
            break;

            default:
                error("syntax error"); 
        }
    }

    private void elseopt(){
        switch(look.tag){
            case '(':
                match('(');
                match(Tag.ELSE);
                stat();
                match(')');
            break;

            case ')':
            break;

            default:
                error("syntax error"); 
        }
    }

    private void expr(){
        switch(look.tag){
            case Tag.NUM:
                match(Tag.NUM);
            break;

            case Tag.ID:
                match(Tag.ID);
            break;

            case '(':
                match('(');
                exprp();
                match(')');
            break;

            default:
                System.out.println(look);
                error("syntax error"); 
        }
    }

    private void exprp(){
        switch(look.tag){
            case '+':
                match(Token.plus.tag);
                exprlist();
            break;

            case '-':
                match(Token.minus.tag);
                expr();
                expr();
            break;

            case '*':
                match(Token.mult.tag);
                exprlist();
            break;

            case '/':
                match(Token.div.tag);
                expr();
                expr();
            break;

            default:
                error("syntax error"); 
        }
    }

    private void exprlist(){
        switch(look.tag){
            case Tag.NUM:
                expr();
                exprlistp();
            break;

            case Tag.ID:
                expr();
                exprlistp();
            break;

            case '(':
                expr();
                exprlistp();
            break;

            default:
                error("syntax error"); 
        }
    }

    private void exprlistp(){
        switch(look.tag){
            case Tag.NUM:
                expr();
                exprlistp();
            break;

            case Tag.ID:
                expr();
                exprlistp();
            break;

            case '(':
                expr();
                exprlistp();
            break;

            case ')':
            break;

            default:
                error("syntax error"); 
        }
    }

		
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Doc"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Parser parser = new Parser(lex, br);
            parser.prog();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}