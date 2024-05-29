INSERT INTO patrons (firstName,lastName,numBooksOut,numHolds,totalFineAmount)
VALUES
	('Dan','Gallagher',1,1,0),
    ('Justin','Sherman',2,25,3.5),
    ('Michelle','Obama',0,1,0),
    ('Barak','Obama',1,0,0),
    ('Booker','Washington',10,1,0.5);

INSERT INTO books (title,author,genre,checkedOut,onHold)
VALUES
	('Java For Dummies','Brandon Krakowski','Tech',TRUE,FALSE),
	('Benjamin Franklin: A Biography','Upenn Historian','Biography',TRUE,FALSE),
	('University of Pennsylvania','John Provost','Non-Fiction',FALSE,FALSE),
	('The Circle','John Eggert','Sci-Fi',TRUE,TRUE),
	('Black Mirror','John Eggert','Sci-Fi',FALSE,FALSE),
	('Another Textbook','I. M. Student','Tech',TRUE,TRUE);

INSERT INTO checkouts (book_ID,patron_ID,checkOutDate)
VALUES
	(10,11,'2020-08-01'),
    (11,13,'2020-12-01'),
    (13,11,'2020-11-30'),
    (15,10,'2020-12-05');
    
INSERT INTO holds (book_ID,patron_ID,requestDate)
VALUES
	(13,10,'2020-11-03'),
    (13,12,'2020-12-01'),
    (15,14,'2020-11-15');