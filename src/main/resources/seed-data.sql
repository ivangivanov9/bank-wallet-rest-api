DELETE FROM wallets;
DELETE FROM users;

ALTER TABLE users ALTER COLUMN id RESTART WITH 1;
ALTER TABLE wallets ALTER COLUMN id RESTART WITH 1;

INSERT INTO users (username, email, password, role) VALUES
                                                        ('ivan_p', 'ivan@example.com', '$2a$10$NkM3C8QqXkQFx8QqXkQFxO', 'USER'),
                                                        ('maria_g', 'maria@example.com', '$2a$10$NkM3C8QqXkQFx8QqXkQFxO', 'USER'),
                                                        ('georgi_d', 'georgi@example.com', '$2a$10$NkM3C8QqXkQFx8QqXkQFxO', 'USER'),
                                                        ('elena_v', 'elena@example.com', '$2a$10$NkM3C8QqXkQFx8QqXkQFxO', 'ADMIN'),
                                                        ('petar_n', 'petar@example.com', '$2a$10$NkM3C8QqXkQFx8QqXkQFxO', 'USER');

INSERT INTO wallets (balance, user_id) VALUES
                                           (1000.00, (SELECT id FROM users WHERE username = 'ivan_p')),
                                           (2500.00, (SELECT id FROM users WHERE username = 'maria_g')),
                                           (500.00, (SELECT id FROM users WHERE username = 'georgi_d')),
                                           (10000.00, (SELECT id FROM users WHERE username = 'elena_v')),
                                           (0.00, (SELECT id FROM users WHERE username = 'petar_n'));