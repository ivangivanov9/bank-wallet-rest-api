SET REFERENTIAL_INTEGRITY FALSE;
TRUNCATE TABLE wallets;
TRUNCATE TABLE users;
SET REFERENTIAL_INTEGRITY TRUE;

INSERT INTO users (username, email) VALUES
                                        ('ivan_p', 'ivan@example.com'),
                                        ('maria_g', 'maria@example.com'),
                                        ('georgi_d', 'georgi@example.com'),
                                        ('elenа_v', 'elena@example.com'),
                                        ('petar_n', 'petar@example.com');

INSERT INTO wallets (balance, user_id) VALUES
                                           (1000.00, (SELECT id FROM users WHERE username = 'ivan_p')),
                                           (2500.00, (SELECT id FROM users WHERE username = 'maria_g')),
                                           (500.00, (SELECT id FROM users WHERE username = 'georgi_d')),
                                           (10000.00, (SELECT id FROM users WHERE username = 'elenа_v')),
                                           (0.00, (SELECT id FROM users WHERE username = 'petar_n'));