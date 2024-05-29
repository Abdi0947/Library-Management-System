USE librarymanagementsystem;

CREATE TABLE books (
  book_ID int NOT NULL AUTO_INCREMENT,	
  title varchar(225) DEFAULT NULL,
  author varchar(225) DEFAULT NULL,
  genre varchar(225) DEFAULT NULL,
  checkedOut boolean DEFAULT False,
  onHold boolean DEFAULT False,
  loanLength int DEFAULT 14,
  dailyFineAmount float DEFAULT 0.25,
  PRIMARY KEY (book_ID)
);

ALTER TABLE books AUTO_INCREMENT=10;		# so that all id's have the same number of digits [2] (I don't think we'll have more than 90)

CREATE TABLE patrons (
  patron_ID int NOT NULL AUTO_INCREMENT,
  firstName varchar(225) DEFAULT NULL,
  lastName varchar(225) DEFAULT NULL,
  numBooksOut int DEFAULT 0,
  maxBooks int DEFAULT 10,
  numHolds int DEFAULT 0,
  maxHolds int DEFAULT 25,
  totalFineAmount float DEFAULT 0.0,
  PRIMARY KEY (patron_ID)
);

ALTER TABLE patrons AUTO_INCREMENT=10;

CREATE TABLE checkOuts (
    chkO_ID int NOT NULL AUTO_INCREMENT,
    book_ID int NOT NULL,
    patron_ID int NOT NULL,
    checkOutDate date NOT NULL,
    PRIMARY KEY (chkO_ID),
    FOREIGN KEY (book_ID) REFERENCES books(book_ID),
    FOREIGN KEY (patron_ID) REFERENCES patrons(patron_ID)
);

ALTER TABLE checkOuts AUTO_INCREMENT=10;

CREATE TABLE holds (
    hold_ID int NOT NULL AUTO_INCREMENT,
    book_ID int NOT NULL,
    patron_ID int NOT NULL,
    requestDate date NOT NULL,
    PRIMARY KEY (hold_ID),
    FOREIGN KEY (book_ID) REFERENCES books(book_ID),
    FOREIGN KEY (patron_ID) REFERENCES patrons(patron_ID)
);

ALTER TABLE holds AUTO_INCREMENT=10;