CREATE TABLE IF NOT EXISTS users (
        user_id serial NOT NULL PRIMARY KEY,
        login varchar(255) NOT NULL,
        name varchar(255) NOT NULL,
        email varchar(255) NOT NULL,
        birthday date NOT NULL
);

CREATE TABLE IF NOT EXISTS ratings (
        rating_id integer NOT NULL PRIMARY KEY,
        name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
        film_id serial NOT NULL PRIMARY KEY,
        name varchar(255) NOT NULL,
        description varchar(200) NOT NULL,
        release_date date NOT NULL,
        duration integer NOT NULL,
        rating_id integer REFERENCES ratings (rating_id)
);

CREATE TABLE IF NOT EXISTS genres (
        genre_id integer NOT NULL PRIMARY KEY,
        name varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
        film_id integer REFERENCES films (film_id),
        genre_id integer REFERENCES genres (genre_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
        film_id integer REFERENCES films (film_id),
        user_id integer REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS friends (
        user_id integer REFERENCES users (user_id),
        friend_id integer REFERENCES users (user_id)
);