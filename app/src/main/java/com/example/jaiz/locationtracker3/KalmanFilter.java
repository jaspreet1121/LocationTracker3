package com.example.jaiz.locationtracker3;

import java.util.Random;

/**
 * Created by Jaiz on 12/30/2017.
 */

public class KalmanFilter {

    static Matrix X = new Matrix("G",9,1);
    static Matrix P = Matrix.multiply(new Matrix("I",9,9),(double) 5);

    public static Matrix Kalman(double h,double a_x,double a_y,double a_z){
        Random rn = new Random();

        double[][] z = {{a_x,a_y,a_z}};
        Matrix Z = new Matrix(z);
        Z = Matrix.transpose(Z);

        //print statements
        //Z.printMatrix();



        Matrix I9 = new Matrix("I",9,9);

        Matrix Q = new Matrix("I",9,9);
        Q = Matrix.multiply(Q,(double) 0.5);

        Matrix R = new Matrix("I",3,3);


        double h2 = h*h/2;
        double[][] ph = {{1,0,0,h,0,0,h2,0,0},
                {0,1,0,0,h,0,0,h2,0},
                {0,0,1,0,0,h,0,0,h2},
                {0,0,0,1,0,0,h,0,0},
                {0,0,0,0,1,0,0,h,0},
                {0,0,0,0,0,1,0,0,h},
                {0,0,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,1,0},
                {0,0,0,0,0,0,0,0,1}};
        Matrix PHI = new Matrix(ph);

        double[][] h_array = {{0,0,0,0,0,0,1,0,0},
                {0,0,0,0,0,0,0,1,0},
                {0,0,0,0,0,0,0,0,1}};
        Matrix H = new Matrix(h_array);



        /*print statements
        P.printMatrix();
        X.printMatrix();
        */

        Matrix temp_K = Matrix.add(R,Matrix.multiply(H,Matrix.multiply(P,Matrix.transpose(H))));
        Matrix K = Matrix.multiply(P,Matrix.multiply(Matrix.transpose(H),Matrix.invert(temp_K)));

        Matrix Z_HAT = Matrix.multiply(H,X);
        //Z_HAT.printMatrix();

        X = Matrix.add(X,Matrix.multiply(K,Matrix.subtract(Z,Z_HAT)));
        P = Matrix.multiply(Matrix.subtract(I9,Matrix.multiply(K,H)),P);

        X = Matrix.multiply(PHI,X);
        P = Matrix.add(Matrix.multiply(PHI,Matrix.multiply(P,Matrix.transpose(PHI))),Q);

        /*print statements
        P.printMatrix();
        X.printMatrix();
        */

        return Z_HAT;
    }

}
