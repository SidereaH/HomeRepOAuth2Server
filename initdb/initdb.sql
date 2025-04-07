CREATE DATABASE scitech_newsservice;
CREATE DATABASE scitech_newsparser;
CREATE DATABASE homerep_auth;
CREATE DATABASE scitech_auth;

-- Таблица статусов
CREATE TABLE status (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE
);

-- Таблица новостных объектов (обновленная структура)
CREATE TABLE news_object (
                             id BIGSERIAL PRIMARY KEY,
                             owner_id BIGINT NOT NULL,
                             theme VARCHAR(255),
                             status_id BIGINT,
                             title VARCHAR(255) NOT NULL,
                             description TEXT,
                             content TEXT,
                             url VARCHAR(512),
                             likes INT DEFAULT 0,
                             shows INT DEFAULT 0,
                             tags VARCHAR(1000),  -- Теперь храним теги как строку с разделителями
                             date_of_creation DATE,
                             FOREIGN KEY (status_id) REFERENCES status(id)
);
-- Вставка статусов
INSERT INTO status (name) VALUES
                              ('DRAFT'),
                              ('PUBLISHED'),
                              ('ARCHIVED'),
                              ('DELETED');

-- Вставка тестовых новостных объектов (с новыми полями)
INSERT INTO news_object (
    owner_id,
    theme,
    status_id,
    title,
    description,
    content,
    url,
    likes,
    shows,
    tags,
    date_of_creation
) VALUES
      (1, 'Technology', 2, 'New AI Breakthrough', 'Researchers develop new AI model',
       'Long content about AI...', 'https://example.com/ai-breakthrough', 150, 1200,
       'technology,science,ai', '2023-05-15'),

      (2, 'Health', 2, 'COVID-19 Update', 'Latest developments in pandemic',
       'Content about COVID...', 'https://example.com/covid-update', 85, 950,
       'health,world,pandemic', '2023-06-20'),

      (1, 'Politics', 1, 'Election Results Draft', 'Preliminary election results',
       'Draft content...', NULL, 0, 0, 'politics,elections', '2023-07-10');

CREATE INDEX idx_news_owner ON news_object(owner_id);
CREATE INDEX idx_news_theme ON news_object(theme);
CREATE INDEX idx_news_date ON news_object(date_of_creation);
CREATE INDEX idx_news_status ON news_object(status_id);