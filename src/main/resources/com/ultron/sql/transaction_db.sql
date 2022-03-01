DROP DATABASE IF EXISTS transaction_db;
CREATE DATABASE IF NOT EXISTS transaction_db;
USE transaction_db;


DROP TABLE IF EXISTS accounts;
CREATE TABLE IF NOT EXISTS accounts(
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
name VARCHAR(80) NOT NULL,
amount DECIMAL(10,2)

);


INSERT INTO accounts(name,amount)
VALUES('Nyan Linn Htet',200000),
('Aye Myat Mon',200000);


DROP TABLE IF EXISTS transfer_logs;
CREATE TABLE IF NOT EXISTS transfer_logs(
id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
from_account INT NOT NULL,
to_account INT NOT NULL,
transfer_time TIMESTAMP DEFAULT NOW() NOT NULL,
amount DECIMAL(10,2),
from_amount DECIMAL(10,2),
to_amount DECIMAL(10,2),
FOREIGN KEY fk_transfer_account_1 (from_account)REFERENCES accounts(id),
FOREIGN KEY fk_transfer_account_2 (to_account)REFERENCES accounts(id)

);