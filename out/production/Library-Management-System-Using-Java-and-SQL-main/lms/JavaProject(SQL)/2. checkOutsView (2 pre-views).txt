CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `preprecheckoutsview` AS
    SELECT 
        `patrons`.`patron_ID` AS `patron_ID`,
        `patrons`.`firstName` AS `firstName`,
        `patrons`.`lastName` AS `lastName`,
        `books`.`book_ID` AS `book_ID`,
        `books`.`title` AS `title`,
        `books`.`author` AS `author`,
        `books`.`onHold` AS `onHold`,
        `checkouts`.`checkOutDate` AS `checkOutDate`,
        `books`.`dailyFineAmount` AS `dailyFineAmount`,
        (`checkouts`.`checkOutDate` + INTERVAL `books`.`loanLength` DAY) AS `dueDate`
    FROM
        ((`books`
        JOIN `checkouts` ON ((`books`.`book_ID` = `checkouts`.`book_ID`)))
        JOIN `patrons` ON ((`patrons`.`patron_ID` = `checkouts`.`patron_ID`)));
        
CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `precheckOutsView` AS
    SELECT *,
		(CASE 
        WHEN DATEDIFF(CURDATE(), `dueDate`) < 0 THEN 0		# due date is after current date (not overdue)
        ELSE DATEDIFF(CURDATE(), `dueDate`)
		END) AS `daysLate`
    FROM `preprecheckoutsview`;

CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `checkOutsView` AS
    SELECT *,
       `dailyFineAmount` * `daysLate` AS `fineAmount`
	FROM `precheckoutsview`;