public class NumberTok extends Token {
	public String lexeme = "";
	public NumberTok(String n){
		super(Tag.NUM);
		lexeme=n;
	}
	public String toString() { return "<" + tag + ", " + lexeme + ">"; }

}
