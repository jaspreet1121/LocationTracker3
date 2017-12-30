package com.example.jaiz.locationtracker3;

import java.util.Random;

/**
 * Created by Jaiz on 12/30/2017.
 */

public class Matrix {
    private double[][] matrix;
    private int numColumn, numRow;

    //Default Constructor with initial elements as zero
    public Matrix(int n,int m){
        matrix = new double[n][m];

        for (int i = 0;i<n;i++){
            for (int j = 0;j<m;j++){
                matrix[i][j] = 0;
            }
        }

        numColumn = m;
        numRow = n;
    }

    public Matrix(String type,int n,int m){
        this(n, m);
        Random rn  = new Random();
        if (type == "I") {
            for (int i = 0;i<n;i++){
                matrix[i][i] = 1;
            }
        }
        else if (type == "G"){
            for (int i = 0;i<n;i++){
                for (int j = 0;j<m;j++){
                    matrix[i][j] = rn.nextGaussian();
                }
            }
        }
    }

    public Matrix(double a[][]){
        matrix = a;
        numRow = a.length;
        numColumn = a[0].length;
    }

    //Accessor Methods
    public int getRow(){
        return numRow;
    }
    public int getColumn(){
        return numColumn;
    }
    public double getElement(int n,int m){
        return matrix[n][m];
    }
    public double[][] getMatrix(){
        return matrix;
    }

    //Mutator method
    public void setElement(double x, int n,int m){
        matrix[n][m] = x;
    }



    public static Matrix add(Matrix m1,Matrix m2){
        Matrix m3 = new Matrix(m1.getRow(),m1.getColumn());
        for (int i = 0;i<m1.getRow();i++){
            for (int j = 0;j<m1.getColumn();j++){
                m3.setElement((m1.getElement(i,j)+m2.getElement(i,j)),i,j);
            }
        }
        return m3;
    }

    public static Matrix subtract(Matrix m1,Matrix m2){
        Matrix m3 = new Matrix(m1.getRow(),m1.getColumn());
        for (int i = 0;i<m1.getRow();i++){
            for (int j = 0;j<m1.getColumn();j++){
                m3.setElement((m1.getElement(i,j)-m2.getElement(i,j)),i,j);
            }
        }
        return m3;
    }


    public static Matrix multiply(Matrix m1,Matrix m2){
        Matrix m3 = new Matrix(m1.getRow(),m2.getColumn());
        if (m1.getColumn()==m2.getRow()){
            for (int i = 0;i<m3.getRow();i++){
                for (int j =0;j<m3.getColumn();j++){
                    double sum = 0;
                    for(int k = 0;k<m1.getColumn();k++){

                        sum += m1.getElement(i,k) * m2.getElement(k,j);
                    }
                    m3.setElement(sum,i,j);
                }
            }
            return m3;
        }else {
            return m3;
        }
    }

    public static Matrix multiply(Matrix m1, double a){
        Matrix out = new Matrix(m1.getRow(),m1.getColumn());
        for (int i = 0;i<m1.getRow();i++){
            for (int j = 0;j<m1.getColumn();j++){
                out.setElement(a * m1.getElement(i,j),i,j);
            }
        }
        return out;
    }

    public static Matrix transpose(Matrix m){
        Matrix out = new Matrix(m.getColumn(),m.getRow());
        for (int i = 0;i<out.getRow();i++){
            for (int j = 0;j<out.getColumn();j++){
                out.setElement(m.getElement(j,i),i,j);
            }
        }
        return out;
    }


    //method to invert a matrix

    public static Matrix invert(Matrix m){
        Matrix out = new Matrix(m.getRow(),m.getColumn());
        double[][] a = invertArray(m.getMatrix());
        for (int i = 0;i<out.getRow();i++){
            for (int j = 0;j<out.getColumn();j++){
                out.setElement(a[i][j],i,j);
            }
        }
        return out;
    }

    private static double[][] invertArray(double a[][])
    {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i)
            b[i][i] = 1;

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i=0; i<n-1; ++i)
            for (int j=i+1; j<n; ++j)
                for (int k=0; k<n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i]*b[index[i]][k];

        // Perform backward substitutions
        for (int i=0; i<n; ++i)
        {
            x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
            for (int j=n-2; j>=0; --j)
            {
                x[j][i] = b[index[j]][i];
                for (int k=j+1; k<n; ++k)
                {
                    x[j][i] -= a[index[j]][k]*x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.

    private static void gaussian(double a[][], int index[])
    {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i=0; i<n; ++i)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i=0; i<n; ++i)
        {
            double c1 = 0;
            for (int j=0; j<n; ++j)
            {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j=0; j<n-1; ++j)
        {
            double pi1 = 0;
            for (int i=j; i<n; ++i)
            {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1)
                {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i)
            {
                double pj = a[index[i]][j]/a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l=j+1; l<n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
            }
        }
    }


    public void printMatrix(){
        for (int i = 0;i<matrix.length;i++){
            System.out.print("\n|");
            for (int j = 0;j<matrix[0].length;j++){
                System.out.print(matrix[i][j]+"\t");
            }
            System.out.print("|");
        }
        System.out.println(matrix.length+"X"+matrix[0].length);
    }


}
