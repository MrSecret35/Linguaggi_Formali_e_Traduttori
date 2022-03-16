import java.io.*; 
import java.util.*;

public class Lexer {

    public static int line = 1;
    private char peek = ' ';
    
    private void readch(BufferedReader br) {
        try {
            peek = (char) br.read();
        } catch (IOException exc) {
            peek = (char) -1; // ERROR
        }
    }

    public Token lexical_scan(BufferedReader br) {
        while (peek == ' ' || peek == '\t' || peek == '\n'  || peek == '\r') {
            if (peek == '\n') line++;
            readch(br);
        }
        
        switch (peek) {
            case '!':
                peek = ' ';
                return Token.not;

            case '(':
                peek = ' ';
                return Token.lpt;

            case ')':
                peek = ' ';
                return Token.rpt;

            case '{':
                peek = ' ';
                return Token.lpg;

            case '}':
                peek = ' ';
                return Token.rpg;

            case '+':
                peek = ' ';
                return Token.plus;

            case '-':
                peek = ' ';
                return Token.minus;

            case '*':
                peek = ' ';
                return Token.mult;

            case '/':
                readch(br);
                switch(peek){
                    case '*':
                        if(!comment(br)){
                            System.err.println("Erroneous character: !(*/)");
                                return null;
                        }
                        peek=' ';
                        return lexical_scan(br);

                    case '/':
                        do{
                            readch(br);
                        }while(peek != '\n' && (peek!=(char)-1));

                        return lexical_scan(br);

                    default:
                        return Token.div;
                }
                

            case '=':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.eq;
                } else {
                    //peek = ' ';
                    return Token.assign;
                }
                

            case ';':
                peek = ' ';
                return Token.semicolon;

            case '&':
                readch(br);
                if (peek == '&') {
                    peek = ' ';
                    return Word.and;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

            case '|':
                readch(br);
                if (peek == '|') {
                    peek = ' ';
                    return Word.or;
                } else {
                    System.err.println("Erroneous character"
                            + " after & : "  + peek );
                    return null;
                }

            case '<':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.le;
                } else if(peek == '>'){
                    peek = ' ';
                    return Word.ne;
                }else{
                    return Word.lt;
                }

            case '>':
                readch(br);
                if (peek == '=') {
                    peek = ' ';
                    return Word.ge;
                } else {
                    return Word.gt;
                }
                
            case (char)-1:
                return new Token(Tag.EOF);

            default:
                if (Character.isLetter(peek) || peek=='_') {
                    String c= "";
                    do{
                        c=c + peek;
                        readch(br);
                    }while(Character.isDigit(peek) || Character.isLetter(peek) || peek=='_');//(peek != ' ' && peek != '\t' && peek != '\n'  && peek != '\r' && (peek!=(char)-1));/*(ident(c+peek));*/

                    switch(c){
                        case "cond": return Word.cond;
                        case "when": return Word.when;
                        case "then": return Word.then;
                        case "else": return Word.elsetok;
                        case "while": return Word.whiletok;
                        case "do": return Word.dotok;
                        case "seq": return Word.seq;
                        case "print": return Word.print;
                        case "read": return Word.read;
                        default: 
                            if(ident(c)){
                                return new Word(Tag.ID, c);
                            }else{
                                System.err.println("Erroneous string: "+ c);
                                return null;
                            }
                        
                    }
                    
	// ... gestire il caso degli identificatori e delle parole chiave //

                } else if (Character.isDigit(peek)) {
                    boolean n=true;
                    String c= "";
                    do{
                        c=c + peek;
                        readch(br);
                        if(Character.isLetter(peek) || peek=='_'){
                            System.err.println("Erroneous character(Not Number after): " +c);
                            n=false;
                        }
                    }while((Character.isDigit(peek) || Character.isLetter(peek) || peek=='_') && n);

                    if(n){
                        return new NumberTok(c);
                    }else{
                        //System.err.println("Erroneous String(Not Number): " +c);
                        return null;
                    }
                } else {
                        System.err.println("Erroneous character: " 
                                + peek );
                        return null;
                }
         }
    }
    
    public boolean ident(String s){
        int state=0;

        for(int i=0;state >= 0 && i < s.length();i++){
            char ch = s.charAt(i);

            switch(state){

                case 0:
                    if(ch>='a' && ch<='z'){
                        state=3;
                    }else if(ch=='_'){
                        state=2;
                    }else{
                        state=-1;
                    }
                break;
                /*
                case 1:
                    if((ch>='a' && ch<='z') || (ch>='0' && ch<='9') || ch=='_'){
                        state=3;
                    }else{
                        state=-1;
                    }
                break;
                */
                case 2:
                    if(ch=='_'){
                        state=2;
                    }else if((ch>='a' && ch<='z') || (ch>='0' && ch<='9')){
                        state =3;
                    }else{
                        state =-1;
                    }
                break;

                case 3:
                    if((ch>='a' && ch<='z') || (ch>='0' && ch<='9') || ch=='_'){
                        state =3;
                    }else{
                        state =-1;
                    }
                break;
            }
        }
        return state == 3;
    }
		
        
    public boolean comment(BufferedReader br){

        boolean c=false;
        
        int state=0;
        while((!c) && (peek!=(char)-1)){
            readch(br);
            switch(state){
                case 0:
                    if(peek=='*'){
                        state=1;
                    }else{
                        state=0;
                    }
                break;

                case 1:
                    if(peek=='*'){
                        state=1;
                    }else if(peek == '/'){
                        state=2;
                    }else{
                        state=0;
                    }
                break;

                case 2:
                    c=true;
                break;
            }
        }
        
        /*
        do{
            readch(br);
            if(peek =='*'){
                readch(br);
                if(peek =='/'){
                    c=true;
                }
            }
        }while(!c && (peek != (char)-1));
    */
        return c;

    }
    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Doc.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Token tok;
            do {
                tok = lex.lexical_scan(br);
                System.out.println("Scan: " + tok);
            } while (tok.tag != Tag.EOF);
            br.close();
        } catch (IOException e) {e.printStackTrace();}    
    }

}
