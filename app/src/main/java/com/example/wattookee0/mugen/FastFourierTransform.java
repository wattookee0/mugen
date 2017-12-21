package com.example.wattookee0.mugen;

/**
 * Created by wattookee0 on 12/18/2017.
 * Cooley Tukey FFT Algorithm
 */

public class FastFourierTransform {
    int length, m, i = 0;

    // Lookup tables. Only need to recompute when size of FFT changes.
    double[] cos_LUT;
    double[] sin_LUT;

    public FastFourierTransform(int new_length) {
        length = new_length;
        this.m = (int) (Math.log(length) / Math.log(2));

        // Make sure n is a power of 2
        if (length != (1 << m))
            throw new RuntimeException("FFT length must be power of 2");

        // precompute tables
        cos_LUT = new double[length/2];
        sin_LUT = new double[length/2];

        while (i < length/2) {
            cos_LUT[i] = Math.cos(-2 * Math.PI * i / length);
            sin_LUT[i] = Math.sin(-2 * Math.PI * i / length);
            i++;
        }

    }

    public void crunch(double[] real, double[] imaginary) {
        int i, j, k, n1, n2, a;
        double cosine, sine, t1, t2;

        // Bit-reverse
        j = 0;
        n2 = length / 2;
        for (i = 1; i < length - 1; i++) {
            n1 = n2;
            while (j >= n1) {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j) {
                //swap [i] for [j]
                t1 = real[i];
                real[i] = real[j];
                real[j] = t1;
                //swap [i] for [j]
                t1 = imaginary[i];
                imaginary[i] = imaginary[j];
                imaginary[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++) {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++) {
                cosine = cos_LUT[a];
                sine = sin_LUT[a];
                a += 1 << (m - i - 1);

                for (k = j; k < length; k = k + n2) {
                    t1 = cosine * real[k + n1] - sine * imaginary[k + n1];
                    t2 = sine * real[k + n1] + cosine * imaginary[k + n1];
                    real[k + n1] = real[k] - t1;
                    imaginary[k + n1] = imaginary[k] - t2;
                    real[k] = real[k] + t1;
                    imaginary[k] = imaginary[k] + t2;
                }
            }
        }
    }
}
