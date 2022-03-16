public class Es9{

	public static boolean scan(String s){
		int state=0;


		for(int i=0;state >= 0 && i < s.length();i++){
			char ch = s.charAt(i);
			switch(state){

				case 0:
					if(ch=='P') state=1;
					else state=6;
				break;

				case 1:
					if(ch=='a') state=2;
					else state=7;
				break;

				case 2:
					if(ch=='o') state=3;
					else state=8;
				break;

				case 3:
					if(ch=='l') state=4;
					else state=9;
				break;

				case 4:
					state=5;
				break;

				case 5:
					state =-1;
				break;

				case 6:
					if(ch=='a') state=7;
					else state=-1;
				break;

				case 7:
					if(ch=='o') state=8;
					else state=-1;
				break;
				case 8:
					if(ch=='l') state=9;
					else state=-1;
				break;

				case 9:
					if(ch=='o') state=5;
					else state=-1;
				break;
				
			}

		}

		return state==5;
		
	}

	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}