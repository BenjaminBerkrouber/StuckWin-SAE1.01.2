public class test3 {    
    public static void main(String[] args){

        char[][] tab = {
            {'-', '-', '-', '-', 'R', 'R', 'R', 'R'},
            {'-', '-', '-', '.', 'R', 'R', 'R', 'R'},
            {'-', '-', '.', '.', '.', 'R', 'R', 'R'},
            {'-', 'B', 'B', '.', '.', '.', 'R', 'R'},
            {'-', 'B', 'B', 'B', '.', '.', '.', '-'},
            {'-', 'B', 'B', 'B', 'B', '.', '-', '-'},
            {'-', 'B', 'B', 'B', 'B', '-', '-', '-'},
        };
        
        for(int x=0; x<tab.length; x++){  
            for(int y=0; y<tab[x].length; y++){
                int xIso = ((x+1)-(y+1))*1/2;
                int yIso = (x+y+2)*1/2;
                System.out.print(x);
                System.out.print(y);
            }
            System.out.println("");
        }
        System.out.println("");
    }

    }
