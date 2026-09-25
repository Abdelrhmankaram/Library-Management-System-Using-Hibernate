CREATE TABLE book (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(255) NOT NULL,
    isbn            VARCHAR(20)  NOT NULL,
    published_year  INTEGER,
    available       BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE member (
    id      BIGSERIAL PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE borrowing (
    id           BIGSERIAL PRIMARY KEY,
    book_id      BIGINT NOT NULL REFERENCES book(id),
    member_id    BIGINT NOT NULL REFERENCES member(id),
    borrow_date  DATE NOT NULL,
    return_date  DATE
);