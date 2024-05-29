CREATE 
    ALGORITHM = UNDEFINED 
    DEFINER = `root`@`localhost` 
    SQL SECURITY DEFINER
VIEW `holdsview` AS
    SELECT 
        `books`.`book_ID` AS `book_ID`,
        `books`.`title` AS `title`,
        `books`.`author` AS `author`,
        `books`.`checkedOut` AS `checkedOut`,
        `holds`.`requestDate` AS `requestDate`,
        `patrons`.`patron_ID` AS `patron_ID`,
        `patrons`.`firstName` AS `firstName`,
        `patrons`.`lastName` AS `lastName`
    FROM
        ((`books`
        JOIN `holds` ON ((`books`.`book_ID` = `holds`.`book_ID`)))
        JOIN `patrons` ON ((`patrons`.`patron_ID` = `holds`.`patron_ID`)))