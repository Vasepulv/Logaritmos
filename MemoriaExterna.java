package base;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.lang.Integer;
import java.nio.ByteOrder;

public class MemoriaExterna {
    private String currentDir =  System.getProperty("user.dir");
    private String filesDir = currentDir;
    private String fileName="fileName.bin";

    private int B;

    private int izq;
    private int diag;

    private int[] arriba;

    private int[] izquierda;

    private int n;

    private String X;
    private String Y;

    private StringGen x1;
    private StringGen y2;

    private String[] Y1;
    private String[] y1;

   private DataInputStream dataInput;
   private DataOutputStream dataOutput;

    private FileOutputStream fos;
    private FileInputStream fis;

    public MemoriaExterna(int n){
        B=(int)Math.pow(2,4);
        this.n=n;
        izq=1;
        diag=0;

        x1=new StringGen();
        y2=new StringGen();
        //X=x1.getSubString(1,1024);
        //Y=y2.getSubString(1,1024);

        X="aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbaaaaaaaaaaaaaaa";
        Y="bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
        Y1=Y.split("");
        y1=new String[B];

        arriba=new int[B];
        izquierda=new int[B];
    }

    public static void main(String[] args){
        MemoriaExterna m=new MemoriaExterna(64);
        int s=m.solucion();
        System.out.println(s);
    }

    /**
     * Escribe la primera fila de la cuadrilla
     */
    private void primeraFila(){
        int b=0;
        try{
            while (b<=n) {
                dataOutput.writeInt(b);
                b++;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Calcula los valores de la fila
     *
     * @param izq El valor de la izquierda de nuestro valor actual y que usamos para conserguirlo
     * @param diag El valor de la diagonal de nuestro valor actual, salvo que sea de la fila 1, en ese caso es 0
     * @param x el caracter del String x
     * @param Y el string que estamos comparando caracter a caracter con x
     * @return el ultimo valor de la fila
     */
    public int calcular(int izq, int diag, String x, String[] Y) {
        closeFile('r');
        for (int i = 0; i < izquierda.length ; i++) {
            int val = 1;
            if (Y[i].equals(x)) {
                val = 0;
            }
            int min = Math.min(izq, diag);
            izquierda[i]= min+val;
            escribir(izquierda[i]);
            izq = izquierda[i];
            diag=arriba[i];


        }
        return izquierda[izquierda.length-1];
    }

    private void closeFile(char c) {
        try {
            if (c == 'r'){
                fis.close();
                dataInput.close();
                fos=new FileOutputStream(filesDir+ "\\" +fileName,true);
                dataOutput=new DataOutputStream(fos);
            }
            else if (c == 'w'){
                fos.close();
                dataOutput.close();
                fis= new FileInputStream(filesDir+"\\" +fileName);
                dataInput=new DataInputStream(fis);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Escribe en memoria
     * @param i lo que vamos a escribir en memoria
     */
    void escribir(int i) {
        try{
            dataOutput.writeInt(i);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Regresa el menor valor de distancia de edicion de dos strings, X, Y.
     * @return El menor valor de distancia de edicion
     */
    public int solucion() {
        //X la dividimos en bloques, esto es X/B
        String[] x = X.split("");

        try {
            //Lectura
            fos=new FileOutputStream(filesDir+ "\\" +fileName,false);
            dataOutput=new DataOutputStream(fos);
            primeraFila();
            dataOutput.flush();
            dataOutput.close();
            fos.close();

            fis = new FileInputStream(filesDir + "\\" + fileName);
            dataInput=new DataInputStream(fis);
            int l = 0;
            for (int i = 0; i < n; i++) {
                while (l < (n/B)) {
                    cargarY(l);
                    cargarFilaArriba(l,i);
                    closeFile('r');
                    izq = calcular(izq, diag, x[i], y1);
                    diag=arriba[arriba.length-1];
                    dataOutput.flush();
                    closeFile('w');
                    l++;
                }
                l=0;

            }
            dataInput.close();
            fis.close();
            return izquierda[izquierda.length-1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void cargarY(int l) {
        int i=0;
        while (i< B){
            y1[i]=Y1[i+l*B];
            i++;
        }
    }

    /**
     * Esta carga la nueva fila de arriba, desde memoria
     */
    void cargarFilaArriba(int l, int i) {
        byte[] buffer=new byte[B];
        try{
            fis.skip(i*4*n+4);
            fis.skip(l*B*4);
            ByteBuffer b;
            for (int k=0;k<arriba.length;k++){
                    arriba[k]=dataInput.readInt();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}