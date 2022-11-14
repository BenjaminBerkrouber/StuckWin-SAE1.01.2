class test2 {
    
    public static void main(String...args){
        int lignes=5;int colonnes=3;
        String[][] tab=new String[lignes][colonnes];
        for(int i=0;i<lignes;i++)
            for(int j=0;j<colonnes;j++)
                tab[i][j]=i+"."+j;
        int i=0,j=0,a=0,b=0;
        boolean end=false;
        do{
            System.out.print(tab[i][colonnes-1-j]+" ");
            if((j<colonnes-1)&&(i>0)){
                j++;i--;
            }
            else if(a<lignes-1){
                System.out.println();
                a++;
                i=a;
                j=0;
            }
            else if (a==lignes-1){
                System.out.println();
                a++;
                b++;
                i=a-1;
                j=b;   
            }
            else if(a==lignes){
                System.out.println();
                b++;
                i=a-1;
                j=b;
                a++;
            }
            else{
                System.out.println();
                end=true;
            }
        }while(!end);
    }
}