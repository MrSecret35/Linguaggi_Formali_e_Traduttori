public class Es6{

	public static boolean scan(String s){
		int state=0;

		for(int i=0;state >= 0 && i < s.length();i++){
			char ch = s.charAt(i);
			switch (state) {
				case 0:		/*stato q0: divisione per 3 con resto 0*/
					if (ch == '0')
						state = 0;
					else if (ch == '1')
						state = 1;
					else
						state = -1;
				break;
				
				case 1:		/*stato q1: divisione per 3 con resto 1*/
					if (ch == '0')
						state = 2;
					else if (ch == '1')
						state = 0;
					else
						state = -1;
				break;

				case 2:		/*stato q2: divisione per 3 con resto 2*/
					if (ch == '0')
						state = 1;
					else if (ch == '1')
						state = 2;
					else
						state = -1;
				break;
			
			}
		}
		
		return state == 0;

		
	}

	public static void main(String[] args){
		System.out.println(scan(args[0]) ? "OK" : "NOPE");
    }
}