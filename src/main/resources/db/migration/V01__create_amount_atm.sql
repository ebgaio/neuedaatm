CREATE TABLE ATM (
	id_money BIGINT PRIMARY KEY AUTO_INCREMENT,
	value BIGINT NOT NULL,
	amount BIGINT NOT NULL,
	active BOOLEAN NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

INSERT INTO ATM (value, amount, active) VALUES (5, 20, true);
INSERT INTO ATM (value, amount, active) VALUES (10, 30, true);
INSERT INTO ATM (value, amount, active) VALUES (20, 30, true);
INSERT INTO ATM (value, amount, active) VALUES (50, 10, true);
