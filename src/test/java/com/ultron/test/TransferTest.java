package com.ultron.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ultron.dto.TransferLog;
import com.ultron.service.TransferService;
import com.ultron.utils.ConnectionManager;

class TransferTest {

	TransferService service;

	@BeforeEach
	void init() {
		service = new TransferService();

		try (Connection con = ConnectionManager.getConnection()) {

			con.prepareStatement("SET FOREIGN_KEY_CHECKS = 0").execute();
			con.prepareStatement("TRUNCATE TABLE transfer_logs").execute();
			con.prepareStatement("TRUNCATE TABLE accounts").execute();
			con.prepareStatement(
					"INSERT INTO accounts(name,amount)VALUES('Nyan Linn Htet',200000),('Aye Myat Mon',200000)")
					.execute();

			con.prepareStatement("SET FOREIGN_KEY_CHECKS = 1").execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Test
	void test() {
		TransferLog result = service.transfer(1, 2, 50000);

		assertNotNull(result);
		assertEquals("Nyan Linn Htet", result.from_account());
		assertEquals("Aye Myat Mon", result.to_account());
		assertEquals(50000, result.amount());
		assertEquals(200000, result.from_amount());
		assertEquals(200000, result.to_amount());
	}

}
