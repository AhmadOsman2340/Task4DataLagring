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

package se.kth.iv1351.soundgoodjdbc.integration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import se.kth.iv1351.soundgoodjdbc.model.Instrument;

/**
 * This data access object (DAO) encapsulates all database calls in the bank
 * application. No code outside this class shall have any knowledge about the
 * database.
 */
public class SoundgoodDAO {
    private Connection connection;
    
    private PreparedStatement findAllNotRented;
    private PreparedStatement findAllNotRentedByKind;
    private PreparedStatement rentStmt;
    private PreparedStatement activeRentalsByUserStmt;
    private PreparedStatement terminateRentStmt;
    
    private static final String INSTRUMENT_TABLE_NAME = "instrument";
    private static final String INSTRUMENT_COLUMN_ID = "instrument_id";
    private static final String INSTRUMENT_COLUMN_NAME = "instrument_name";
    private static final String INSTRUMENT_COLUMN_PRICE = "instrument_price";
    private static final String INSTRUMENT_COLUMN_BRAND = "brands";

    /**
     * Constructs a new DAO object connected to the bank database.
     */
    public SoundgoodDAO() throws SoundgoodDBException {
        try {
            connectToSoundgoodDB();
            prepareStatements();
        } catch (ClassNotFoundException | SQLException exception) {
            throw new SoundgoodDBException("Could not connect to datasource.", exception);
        }
    }

    /**
     * Creates a new account.
     *
     * @param account The account to create.
     * @throws SoundgoodDBException If failed to create the specified account.
     * @throws SQLException 
     */
    public void rent(int studentId, int instrumentId, String fromDate, String toDate) throws SoundgoodDBException, SQLException {
        String failureMsg = "Could not rent the instrument with id: " + instrumentId;
        int updatedRows = 0;
        try {
            rentStmt.setInt(1, studentId);
            rentStmt.setInt(2, instrumentId);
            rentStmt.setString(3, fromDate);
            rentStmt.setString(4, toDate);
            updatedRows = rentStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            } else
            	connection.commit();
        } catch (Exception e) {
            connection.rollback();
            handleException(failureMsg, e);
        }
    }

    /**
     * Searches for all accounts whose holder has the specified name.
     *
     * @param kind The account holder's name
     * @return A list with all accounts whose holder has the specified name, 
     *         the list is empty if there are no such account.
     * @throws SoundgoodDBException If failed to search for accounts.
     * @throws SQLException 
     */
    public List<Instrument> findAllNotRentedByKind(String kind) throws SoundgoodDBException, SQLException {
        String failureMsg = "Could not search for specified instruments.";
        ResultSet result = null;
        List<Instrument> instruments = new ArrayList<>();
        try {
        	findAllNotRentedByKind.setString(1, kind);
            result = findAllNotRentedByKind.executeQuery();
            while (result.next()) {
                instruments.add(new Instrument(result.getString(INSTRUMENT_COLUMN_NAME),
                                         result.getString(INSTRUMENT_COLUMN_BRAND),
                                         result.getInt(INSTRUMENT_COLUMN_ID),
                                         result.getInt(INSTRUMENT_COLUMN_PRICE)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            connection.rollback();
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return instruments;
    }

    /**
     * Retrieves all existing accounts.
     *
     * @return A list with all existing accounts. The list is empty if there are no
     *         accounts.
     * @throws SoundgoodDBException If failed to search for accounts.
     */
    public List<Instrument> findAllNotRented() throws SoundgoodDBException {
        String failureMsg = "Could not list instruments.";
        List<Instrument> instruments = new ArrayList<>();
        try (ResultSet result = findAllNotRented.executeQuery()) {
            while (result.next()) {
                instruments.add(new Instrument(result.getString(INSTRUMENT_COLUMN_NAME),
                                         result.getString(INSTRUMENT_COLUMN_BRAND),
                                         result.getInt(INSTRUMENT_COLUMN_ID),
                                         result.getInt(INSTRUMENT_COLUMN_PRICE)));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
        return instruments;
    }

    /**
     * Deletes the account with the specified account number.
     *
     * @param acctNo The account to delete.
     * @throws SoundgoodDBException If unable to delete the specified account.
     */
    public void terminateRent(int studentId, int instrumentId) throws SoundgoodDBException {
    	String failureMsg = "Could not terminate the rent for student: " + studentId + " and instrument: " + instrumentId;
        try {
        	terminateRentStmt.setInt(1, studentId);
        	terminateRentStmt.setInt(2, instrumentId);
            int updatedRows = terminateRentStmt.executeUpdate();
            if (updatedRows != 1) {
                handleException(failureMsg, null);
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        }
    }

    private void connectToSoundgoodDB() throws ClassNotFoundException, SQLException {
        // connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bankdb",
        //                                          "postgres", "postgres");
        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/db",
                                                 "root", "DataIngenjor2340");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
    	findAllNotRented = connection.prepareStatement("SELECT *" + " FROM "
                + INSTRUMENT_TABLE_NAME + " i WHERE " + "i." + INSTRUMENT_COLUMN_ID +
                " NOT IN (SELECT r.instrument_id FROM rented_instrument r WHERE r.rental_start<=CURDATE() AND r.rental_end>=CURDATE() AND r.is_terminated = false)");
        
        findAllNotRentedByKind = connection.prepareStatement("SELECT *" + " FROM "
                + INSTRUMENT_TABLE_NAME + " i WHERE i.instrument_name = ? AND " + "i." + INSTRUMENT_COLUMN_ID +
                " NOT IN (SELECT r.instrument_id FROM rented_instrument r WHERE r.rental_start<=CURDATE() AND r.rental_end>=CURDATE() AND r.is_terminated = false)");
        
        rentStmt = connection.prepareStatement("INSERT INTO rented_instrument (student_id, instrument_id, rental_start, rental_end, is_terminated) VALUES (?, ?, ?, ?, false)");
        
        activeRentalsByUserStmt = connection.prepareStatement("SELECT * FROM rented_instrument r WHERE r.student_id = ? AND r.is_terminated = false");
        
        terminateRentStmt = connection.prepareStatement("UPDATE rented_instrument SET is_terminated = true WHERE student_id = ? AND instrument_id = ?");
    }
    private void handleException(String failureMsg, Exception cause) throws SoundgoodDBException {
        String completeFailureMsg = failureMsg;
        try {
            connection.rollback();
        } catch (SQLException rollbackExc) {
            completeFailureMsg = completeFailureMsg + 
            ". Also failed to rollback transaction because of: " + rollbackExc.getMessage();
        }

        if (cause != null) {
            throw new SoundgoodDBException(failureMsg, cause);
        } else {
            throw new SoundgoodDBException(failureMsg);
        }
    }

    private void closeResultSet(String failureMsg, ResultSet result) throws SoundgoodDBException {
        try {
            result.close();
        } catch (Exception e) {
            throw new SoundgoodDBException(failureMsg + " Could not close result set.", e);
        }
    }

	public List<Integer> getActiveRentalsByUser(int studentInt) throws SoundgoodDBException {
		String failureMsg = "Could not get all rented instruments for the student id: " + studentInt;
        ResultSet result = null;
		List<Integer> activeRentalsByUser = new ArrayList<Integer>();
        try {
        	activeRentalsByUserStmt.setInt(1, studentInt);
            result = activeRentalsByUserStmt.executeQuery();
            while (result.next()) {
            	activeRentalsByUser.add(result.getInt("rental_instrument_id"));
            }
            connection.commit();
        } catch (SQLException sqle) {
            handleException(failureMsg, sqle);
        } finally {
            closeResultSet(failureMsg, result);
        }
        return activeRentalsByUser;		
	}
}
