package com.ultron.dto;

import java.time.LocalDateTime;

public record TransferLog(String from_account, String to_account, LocalDateTime transfe_time, double amount,
		double from_amount, double to_amount) {

	
	
	

}
