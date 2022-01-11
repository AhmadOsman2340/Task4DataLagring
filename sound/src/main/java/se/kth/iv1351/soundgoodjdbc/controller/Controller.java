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

package se.kth.iv1351.soundgoodjdbc.controller;

import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.soundgoodjdbc.integration.SoundgoodDAO;
import se.kth.iv1351.soundgoodjdbc.integration.SoundgoodDBException;
import se.kth.iv1351.soundgoodjdbc.model.Instrument;
import se.kth.iv1351.soundgoodjdbc.model.InstrumentException;

/**
 * This is the application's only controller, all calls to the model pass here.
 * The controller is also responsible for calling the DAO. Typically, the
 * controller first calls the DAO to retrieve data (if needed), then operates on
 * the data, and finally tells the DAO to store the updated data (if any).
 */
public class Controller {
    private final SoundgoodDAO soundGoodDb;

    /**
     * Creates a new instance, and retrieves a connection to the database.
     * 
     * @throws SoundgoodDBException If unable to connect to the database.
     */
    public Controller() throws SoundgoodDBException {
        soundGoodDb = new SoundgoodDAO();
    }

    /**
     * Creates a new account for the specified account holder.
     *
     * @param holderName The account holder's name.
     * @param string3 
     * @param string2 
     * @param string 
     * @throws InstrumentException If unable to create account.
     */
    public void rent(String studentId, String instrumentId, String fromDate, String toDate) throws InstrumentException {
        String failureMsg = "Could not rent the instrument with id: " + instrumentId;

        if (instrumentId == null) {
            throw new InstrumentException(failureMsg);
        }

        try {
        	List<Instrument> availableInstruments = soundGoodDb.findAllNotRented();
        	if (!instrumentAvailable(Integer.parseInt(instrumentId), availableInstruments)) {
        		failureMsg = "The instrument with id: " + instrumentId + " is already rented.";
        		throw new Exception(failureMsg);
        	}
        	
        	List<Integer> activeRentalsByUser = soundGoodDb.getActiveRentalsByUser(Integer.parseInt(studentId));
        	if(activeRentalsByUser.size() > 1) {
        		failureMsg = "There are more than one instruments already rented to the student id: " + studentId;
        		throw new Exception(failureMsg);
        	}
            soundGoodDb.rent(Integer.parseInt(studentId), Integer.parseInt(instrumentId), fromDate, toDate);
        } catch (Exception e) {
            throw new InstrumentException(failureMsg, e);
        }
    }

    private boolean instrumentAvailable(int instrumentId, List<Instrument> availableInstruments) {
    	for(Instrument instrument : availableInstruments) {
    		if(instrument.getId() == instrumentId)
    			return true;
    	}
		return false;
	}

	/**
     * Lists all accounts in the whole bank.
     * 
     * @return A list containing all accounts. The list is empty if there 
     *         are no accounts.
     * @throws InstrumentException If unable to retrieve accounts.
     */
    public List<? extends Instrument> getAllNotRented() throws InstrumentException {
        try {
            return soundGoodDb.findAllNotRented();
        } catch (Exception e) {
            throw new InstrumentException("Unable to list accounts.", e);
        }
    }

    /**
     * Lists all accounts owned by the specified account holder.
     * 
     * @param kind The holder who's accounts shall be listed.
     * @return A list with all accounts owned by the specified holder. The list is
     *         empty if the holder does not have any accounts, or if there is no such holder.
     * @throws InstrumentException If unable to retrieve the holder's accounts.
     */
    public List<? extends Instrument> getAllNotRentedByKind(String kind)
                                                              throws InstrumentException {
        if (kind == null) {
            return new ArrayList<>();
        }

        try {
            return soundGoodDb.findAllNotRentedByKind(kind);
        } catch (Exception e) {
            throw new InstrumentException("Could not search for account.", e);
        }
    }

    /**
     * Deletes the account with the specified account number.
     * 
     * @param acctNo The number of the account that shall be deleted.
     * @throws InstrumentException If failed to delete the specified account.
     */
    public void terminateRent(String studentId, String instrumentId) throws InstrumentException {
        String failureMsg = "Could not terminate the rent for student: " + studentId + " and instrument: " + instrumentId;

        if (studentId == null || instrumentId == null) {
            throw new InstrumentException(failureMsg);
        }

        try {
            soundGoodDb.terminateRent(Integer.parseInt(studentId), Integer.parseInt(instrumentId));
        } catch (Exception e) {
            throw new InstrumentException(failureMsg, e);
        }
    }
}
