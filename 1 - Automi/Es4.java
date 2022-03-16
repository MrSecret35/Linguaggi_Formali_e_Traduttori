public class Es4{

	public static boolean scan(String s){
		int state=0;
		for(int i=0;state >= 0 && i < s.length();i++){
			char ch = s.charAt(i);
            
			switch(state){

                case 0:
                    if(ch=='0' || ch=='2' || ch=='4' || ch=='6' || ch=='8' ){//pari
                        state=1;
                    }else if(ch=='1' || ch=='3' || ch=='5' || ch=='7' || ch=='9' ){//dispari
                        state=2;
                    }else if(ch ==' '){
                        state=0;
                    }else{
                        state=-1;
                    }
                break;

                case 1:

                    if(ch=='0' || ch=='2' || ch=='4' || ch=='6' || ch=='8' ){//pari
                        state=1;
                    }else if(ch=='1' || ch=='3' || ch=='5' || ch=='7' || ch=='9' ){//dispari
                        state=2;
                    }else if(ch>='A' && ch<='K'){
                        state =3;
                    }else if(ch ==' '){
                        state=4;
                    }else{
                        state=-1;
                    }
                break;

                case 2:
                    if(ch=='1' || ch=='3' || ch=='5' || ch=='7' || ch=='9'){//dispari
                        state=2;
                    }else if(ch=='0' || ch=='2' || ch=='4' || ch=='6' || ch=='8' ){//pari
                        state=1;
                    }else if(ch>='L' && ch<='Z'){
                        state =3;
                    }else if(ch == ' '){
                        state=5;
                    }else{
                        state=-1;
                    }
                break;

                case 3:
                    if(ch>='a' && ch<='z'){
                        state=3;
                    }else{
                        state=-1;
                    }
                break;

                case 4:
                    if(ch>='A' && ch<='K'){
                        state =3;
                    }else if(ch ==' '){
                        state=4;
                    }else{
                        state=-1;
                    }
                break;

                case 5:
                    if(ch>='L' && ch<='Z'){
                        state =3;
                    }else if(ch ==' '){
                        state=5;
                    }else{
                        state=-1;
                    }
                break;
            }
        }
        return state == 3;
		
	}

	public static void main(String[] args){
        //System.out.println("Erorr: args[0] non  puo' contenere spazi");
        String s="";
        for (int i=0; i<args.length; i++) {
            s=s+args[i];
        }
		System.out.println(scan(s) ? "OK" : "NOPE");
    }
}