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
    	} else error("syntax error");
    }

    public void start() {
        if(look.tag==Tag.NUM || look.tag=='('){
            expr();
            match(Tag.EOF);
        }else{
            error("syntax error");
        }
    }

    private void expr() {
	   if(look.tag==Tag.NUM || look.tag=='('){
            term();
            exprp();
        }else{
            error("syntax error");
        }
    }

    private void exprp() {
    	switch (look.tag) {
        	case '+':
                match(Token.plus.tag);
                term();
                exprp();
        	break;

            case '-':
                match(Token.minus.tag);
                term();
                exprp();
            break;

            case Tag.EOF:
            case ')':
            break;

            default:
                error("syntax error");
    	}
    }

    private void term() {
        if(look.tag==Tag.NUM || look.tag=='('){
            fact();
            termp();
        }else{
            error("syntax error");
        }
    }

    private void termp() {
        switch (look.tag) {
            case '*':
                match(Token.mult.tag);
                fact();
                termp();
            break;

            case '/':
                match(Token.div.tag);
                fact();
                termp();
            break;

            case '+':
            case '-':
            case Tag.EOF:
            case ')':
            break;

            default:
                error("syntax error");
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                match(Token.lpt.tag);
                expr();
                match(Token.rpt.tag);
            break;

            case Tag.NUM:
                match(Tag.NUM);
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
            parser.start();
            System.out.println("Input OK");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}