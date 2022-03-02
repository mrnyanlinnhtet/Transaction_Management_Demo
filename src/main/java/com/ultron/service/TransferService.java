package com.ultron.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;

import com.ultron.dto.TransferLog;
import com.ultron.utils.ConnectionManager;

public class TransferService {

	public TransferLog transfer(int fromId, int toId, double amount) {

		try (Connection con = ConnectionManager.getConnection()) {

			Savepoint point = null;

			try {
				// Isolation Level
				con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);

				// Start Transaction
//				con.setAutoCommit(false);

				// get amount from account from
				int fromAmount = getAmount(con, fromId);

				// get amount from account to
				int toAmount = getAmount(con, toId);

				// Start Point
				createTransferLog(con, fromId, toId, amount, fromAmount, toAmount, "Start");
				point = con.setSavepoint("Start Save Point");

				// check amount
				if (fromAmount < amount) {
					// Error Point
					createTransferLog(con, fromId, toId, amount, fromAmount, toAmount, "Error");
					point = con.setSavepoint("Error Save Point");
					throw new IllegalArgumentException("No Enough Amount To Transfer !");
				}

				// update from amount
				setAmount(con, fromId, fromAmount - amount);

				// update to amount
				setAmount(con, toId, toAmount + amount);

				// insert into transfer log
				int logId = createTransferLog(con, fromId, toId, amount, fromAmount, toAmount, "Success");
				point = con.setSavepoint("Success Save Point");

				// select transfer log data
				TransferLog result = getTransferLog(con, logId);

				// Commit End
				// con.commit();
				return result;

			} catch (Exception e) {
				if (null != point) {
					con.rollback(point);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	// get transfer log
	private TransferLog getTransferLog(Connection con, int logId) throws SQLException {

		var stm = con.prepareStatement("""
				SELECT fa.name,ta.name,transfer_time,t.amount,t.from_amount,t.to_amount FROM transfer_logs t
				 JOIN accounts fa ON fa.id = t.from_account
				 JOIN accounts ta ON ta.id = t.to_account
				 WHERE t.id = ?
				               """);

		stm.setInt(1, logId);

		ResultSet rs = stm.executeQuery();

		while (rs.next()) {
			System.out.println(rs.getString(1));
			System.out.println(rs.getString(2));
			System.out.println(rs.getTimestamp(3).toLocalDateTime());
			System.out.println(rs.getDouble(4));
			System.out.println(rs.getDouble(5));
			System.out.println(rs.getDouble(6));

			return new TransferLog(rs.getString(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime(),
					rs.getDouble(4), rs.getDouble(5), rs.getDouble(6));
		}

		return null;
	}

	// Create Transfer Log
	private int createTransferLog(Connection con, int fromId, int toId, double amount, int fromAmount, int toAmount,
			String status) throws SQLException {

		final String INSERTLOG = "INSERT INTO transfer_logs(from_account,to_account,amount,from_amount,to_amount,status)"
				+ "VALUES(?,?,?,?,?,?); ";

		PreparedStatement prep = con.prepareStatement(INSERTLOG, Statement.RETURN_GENERATED_KEYS);
		prep.setInt(1, fromId);
		prep.setInt(2, toId);
		prep.setDouble(3, amount);
		prep.setDouble(4, fromAmount);
		prep.setDouble(5, toAmount);
		prep.setString(6, status);

		prep.executeUpdate();

		ResultSet rs = prep.getGeneratedKeys();

		while (rs.next()) {
			return rs.getInt(1);
		}

		return 0;
	}

	// Set Amount
	private void setAmount(Connection con, int fromId, double amount) throws SQLException {

		final String UPDATEAMOUNT = "UPDATE accounts SET amount = ? WHERE id = ?";

		PreparedStatement stm = con.prepareStatement(UPDATEAMOUNT);
		stm.setDouble(1, amount);
		stm.setInt(2, fromId);
		stm.executeUpdate();

	}

	// get Amount
	private int getAmount(Connection con, int id) throws SQLException {

		final String SELECTAMOUNT = "SELECT amount FROM accounts WHERE id = ?";

		PreparedStatement stm = con.prepareStatement(SELECTAMOUNT);
		stm.setInt(1, id);

		ResultSet result = stm.executeQuery();

		while (result.next()) {
			return result.getInt(1);
		}

		return 0;
	}

}
