import java.io.*;



public class Translator {
    private Lexer lex;
    private BufferedReader pbr;
    private Token look;
    
    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();
    int count=0;

    enum operation {PLUS,MULT,PRINT}

    public Translator(Lexer l, BufferedReader br) {
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

    public void prog() { 
        switch(look.tag){
            case '(':
                int lnext_prog = code.newLabel();
                stat(lnext_prog);
                code.emitLabel(lnext_prog);
                match(Tag.EOF);
                try {
                    code.toJasmin();
                }
                catch(java.io.IOException e) {
                    System.out.println("IO error\n");
                };
            break;

            default:
                error("syntax error"); 
        }
    }

    private void stat(int lnext_prog){
        switch(look.tag){
            case '(':
                int lnext_stat = lnext_prog;
                match('(');
                statp(lnext_stat);
                match(')');
            break;

            default:
                error("syntax error"); 
        }
    }

    public void statp(int lnext_stat) {
        int lnext_statp=lnext_stat;
        int giusta;
        int sbagliata;
        switch(look.tag) {
            case '=':
                match(Token.assign.tag);
                if (look.tag==Tag.ID){
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }
                    match(Tag.ID);
                    expr();
                    code.emit(OpCode.istore,read_id_addr);
                }else
                    error("Error in grammar (stat) after read with " + look);
            break;

            case Tag.COND:
                match(Tag.COND);
                //giusta=code.newLabel();
                sbagliata=code.newLabel();
                int next=code.newLabel();

                bexpr(sbagliata);
                /*giusta*/
                stat(lnext_statp);
                code.emit(OpCode.GOto,next);

                /*sbagliata*/
                code.emitLabel(sbagliata);
                elseopt(lnext_statp);

                code.emitLabel(next);
            break;
	           
            case Tag.WHILE:
                int ciclo=code.newLabel();
                sbagliata=code.newLabel();

                match(Tag.WHILE);
                code.emitLabel(ciclo);
                bexpr(sbagliata);
                /*giusta*/
                stat(lnext_statp);
                code.emit(OpCode.GOto,ciclo);
                /*sbagliata*/
                code.emitLabel(sbagliata);
            break;

            case Tag.DO:
                match(Tag.DO);
                statlist(lnext_statp);
            break;

            case Tag.PRINT:
                match(Tag.PRINT);
                operation x=operation.PRINT;
                exprlist(x);

            break;

            case Tag.READ:
                match(Tag.READ);
                if (look.tag==Tag.ID) {
                    int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                    if (read_id_addr==-1) {
                        read_id_addr = count;
                        st.insert(((Word)look).lexeme,count++);
                    }                    
                    match(Tag.ID);
                    code.emit(OpCode.invokestatic,0);
                    code.emit(OpCode.istore,read_id_addr);   
                }
                else
                    error("Error in grammar (stat) after read with " + look);
            break;

            default:
                error("syntax error");

        }
    }

    private void statlist(int lnext_statp){
        switch(look.tag){
            case '(':
                int lnext_statlist=lnext_statp;
                stat(lnext_statlist);
                //lnext_statlist=code.newLabel();
                statlistp(lnext_statlist);
            break;

            default:
                error("syntax error in statlist"); 
        }
    }

    private void statlistp(int lnext_statlist){
        switch(look.tag){
            case '(':
                int lnext_statlistp=lnext_statlist;
                stat(lnext_statlistp);
                //lnext_statlist=code.newLabel();
                statlistp(lnext_statlistp);
            break;

            case ')':
            break;

            default:
                error("syntax error in statlistp"); 
        }
    }

    private void bexpr(int lnext_statp){
        switch(look.tag){
            case '(':
                int lnext_bexpr=lnext_statp;
                match('(');
                bexprp(lnext_bexpr);
                match(')');
            break;

            default:
                error("syntax error in bexpr"); 
        }
    }

    private void bexprp(int lnext_bexpr){
        switch(look.tag){
            case Tag.RELOP:
                int lnext_bexprp=lnext_bexpr;
                Word p= (Word) look;
                match(Tag.RELOP);
                expr();
                expr();
                switch(p.lexeme){
                    case "<":
                        //code.emit(OpCode.if_icmplt,lnext_bexprp);

                        code.emit(OpCode.if_icmpge,lnext_bexprp);

                    break;

                    case ">":
                        //code.emit(OpCode.if_icmpgt,lnext_bexprp);

                        code.emit(OpCode.if_icmple,lnext_bexprp);
                    break;

                    case "<=":
                        //code.emit(OpCode.if_icmple,lnext_bexprp);

                        code.emit(OpCode.if_icmpgt,lnext_bexprp);
                    break;

                    case ">=":
                        //code.emit(OpCode.if_icmpge,lnext_bexprp);

                        code.emit(OpCode.if_icmplt,lnext_bexprp);
                    break;

                    case "==":
                        //code.emit(OpCode.if_icmpeq,lnext_bexprp);

                        code.emit(OpCode.if_icmpne,lnext_bexprp);
                    break;

                    case "<>":
                        //code.emit(OpCode.if_icmpne,lnext_bexprp);

                        code.emit(OpCode.if_icmpeq,lnext_bexprp);
                    break;

                    default:
                        error("syntax error in bexprp");
                }
                
            break;

            default:
                error("syntax error in bexprp"); 
        }
    }

    private void elseopt(int lnext_statp){
        switch(look.tag){
            case '(':
                match('(');
                int lnext_elseopt=lnext_statp;
                match(Tag.ELSE);
                stat(lnext_elseopt);
                match(')');
            break;

            case ')':
            break;

            default:
                error("syntax error in elseopt"); 
        }
    }

    private void expr(){
        switch(look.tag){
            case Tag.NUM:
                NumberTok j= (NumberTok)look;
                int fact_val=Integer.valueOf(j.lexeme);
                code.emit(OpCode.ldc,fact_val);
                match(Tag.NUM);
            break;

            case Tag.ID:
                int read_id_addr = st.lookupAddress(((Word)look).lexeme);
                if (read_id_addr==-1){
                    throw new Error("id non inizializzato" );
                }else{
                    match(Tag.ID);
                    code.emit(OpCode.iload,read_id_addr);
                }
                
            break;

            case '(':
                match('(');
                exprp();
                match(')');
            break;

            default:
                error("syntax error in expr"); 
        }
    }

    private void exprp() {
        operation x;
        switch(look.tag) {
            case '+':
                match(Token.plus.tag);
                x=operation.PLUS;
                exprlist(x);
                /*
                for (int i=0; i<x-1; i++) {
                    code.emit(OpCode.iadd);
                }
                */
            break;

            case '-':
                match('-');
                expr();
                expr();
                code.emit(OpCode.isub);
            break;

            case '*':
                match(Token.mult.tag);
                x=operation.MULT;
                exprlist(x);
                /*
                for (int i=0; i<x-1; i++) {
                    code.emit(OpCode.imul);
                }
                */
            break;

            case '/':
                match(Token.div.tag);
                expr();
                expr();
                code.emit(OpCode.idiv);
            break;

            default:
                error("syntax error in exprp");
        }
    }

    private void exprlist(operation x){
        switch(look.tag){
            case Tag.NUM:
                expr();
                if(x==operation.PRINT){
                    code.emit(OpCode.invokestatic,1);
                }
                exprlistp(x);
            break;

            case Tag.ID:
                expr();
                if(x==operation.PRINT){
                    code.emit(OpCode.invokestatic,1);
                }
                exprlistp(x);
            break;

            case '(':
                expr();
                if(x==operation.PRINT){
                    code.emit(OpCode.invokestatic,1);
                }
                exprlistp(x);
            break;

            default:
                error("syntax error in exprlist"); 
        }
    }

    private void exprlistp(operation x){
        switch(look.tag){
            case Tag.NUM:
                expr();
                switch(x){
                    case PRINT:
                        code.emit(OpCode.invokestatic,1);
                    break;
                    case PLUS:
                        code.emit(OpCode.iadd);
                    break;
                    case MULT:
                         code.emit(OpCode.imul);
                    break;
                }
                exprlistp(x);
            break;

            case Tag.ID:
                expr();
                switch(x){
                    case PRINT:
                        code.emit(OpCode.invokestatic,1);
                    break;
                    case PLUS:
                        code.emit(OpCode.iadd);
                    break;
                    case MULT:
                         code.emit(OpCode.imul);
                    break;
                }
                exprlistp(x);
            break;

            case '(':
                expr();
                if(x==operation.PRINT){
                    code.emit(OpCode.invokestatic,1);
                }
                exprlistp(x);
            break;

            case ')':
            break;

            default:
                error("syntax error in exprlistp"); 
        }
    }

    public static void main(String[] args) {
        Lexer lex = new Lexer();
        String path = "Doc.lft"; // il percorso del file da leggere
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            Translator translator = new Translator(lex, br);
            translator.prog();
            System.out.println("Input OK ");
            br.close();
        } catch (IOException e) {e.printStackTrace();}
    }
}