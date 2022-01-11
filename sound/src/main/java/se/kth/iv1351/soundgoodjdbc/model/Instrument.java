/*
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.soundgoodjdbc.model;

/**
 * An account in the bank.
 */
public class Instrument {
    private int id;
    private String brand;
    private String name;
    private int price;

    /**
     * Creates an account for the specified holder with the specified balance and account
     * number.
     *
     * @param name     The account number.
     * @param brand The account holder's holderName.
     * @param id    The initial balance.
     */
    public Instrument(String name, String brand, int id, int price) {
        this.name = name;
        this.brand = brand;
        this.id = id;
        this.price = price;
    }

    /**
     * @return The account number.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The id.
     */
    public int getId() {
        return id;
    }
    
    /**
     * @return The price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * @return The holder's name.
     */
    public String getBrand() {
        return brand;
    }
}
