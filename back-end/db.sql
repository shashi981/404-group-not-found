-- begin of create tables
CREATE TABLE USERS(
	UID MEDIUMINT NOT NULL AUTO_INCREMENT,
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    ProfileURL VARCHAR(500),
    MessageToken VARCHAR(200) NOT NULL,
    -- Bios VARCHAR(500),
    PRIMARY KEY (UID)
);


CREATE TABLE GROCERIES(
	UPC VARCHAR(20) NOT NULL,
    Name VARCHAR(50) NOT NULL,
    Brand VARCHAR(50),
    Category VARCHAR(50),
    -- Itemsize SMALLINT,
    PRIMARY KEY (UPC, Name)
);

CREATE TABLE OWNS(
	UID MEDIUMINT NOT NULL,
	UPC VARCHAR(20) NOT NULL,
    ExpireDate DATE NOT NULL,
    ItemCount INT NOT NULL,
    ItemID INT,
    PurchaseDate DATE NOT NULL,
    AboutExpire BIT(1) default 0 NOT NULL, -- 0 for not about to expiry, 1 for about to expiry
    Name VARCHAR(50) default 'whatever' NOT NULL,
    PRIMARY KEY (UPC, UID, ExpireDate, Name, PurchaseDate),
    FOREIGN KEY (UPC) REFERENCES GROCERIES(UPC)
    ON DELETE CASCADE,
    FOREIGN KEY (UID) REFERENCES USERS(UID)
    ON DELETE CASCADE
);

CREATE TABLE RECIPE(
	RID MEDIUMINT NOT NULL,
    Ingredient VARCHAR(50) NOT NULL,
    Amount VARCHAR(50),
    PRIMARY KEY (RID, Ingredient)
);

CREATE TABLE RECIPE_INFO(
    Rname VARCHAR(50) NOT NULL,
    Category VARCHAR(50),
    RID MEDIUMINT NOT NULL PRIMARY KEY,
    Instruction VARCHAR(5000),
    YoutubeLInk VARCHAR(100)
);

CREATE TABLE RATING(
	RID MEDIUMINT NOT NULL,
    Rating INT UNIQUE NOT NULL,
    PRIMARY KEY (RID),
    FOREIGN KEY (RID) REFERENCES RECIPE_INFO(RID)
    ON DELETE CASCADE
);

CREATE TABLE DIETICIAN(
    DID MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    MessageToken VARCHAR(200) NOT NULL,
    ProfileURL VARCHAR(500)
);

CREATE TABLE DIETICIAN_REQUEST(
    RID MEDIUMINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    UID MEDIUMINT UNIQUE NOT NULL,
    FOREIGN KEY (UID) REFERENCES USERS(UID)
    ON DELETE CASCADE
);

CREATE TABLE CHAT(
	CID MEDIUMINT NOT NULL AUTO_INCREMENT,
    UID MEDIUMINT NOT NULL,
    DID MEDIUMINT NOT NULL,
    Text VARCHAR(500) NOT NULL,
    Time TIMESTAMP NOT NULL,
    FROM_USER BIT(1) NOT NULL, -- 1 for from user, 0 for from dietician
    Delivered BIT(1) default 0 NOT NULL, -- 1 for from delivered, 0 for from not delievered
    PRIMARY KEY (CID, UID, DID),
    FOREIGN KEY (UID) REFERENCES USERS(UID)
    ON DELETE CASCADE,
    FOREIGN KEY (DID) REFERENCES DIETICIAN(DID)
    ON DELETE CASCADE
);

CREATE TABLE PREFERENCE(
    UID MEDIUMINT NOT NULL,
    Pref VARCHAR(25) NOT NULL ,
    PRIMARY KEY(UID, Pref),
    FOREIGN KEY (UID) REFERENCES USERS(UID)
    ON DELETE CASCADE,
    FOREIGN KEY (Pref) REFERENCES PREF_LIST(Pref)
    ON DELETE CASCADE
);

CREATE TABLE PREF_LIST(
    Pref VARCHAR(25) NOT NULL PRIMARY KEY
);

CREATE TABLE ADMIN(
	UID MEDIUMINT NOT NULL AUTO_INCREMENT,
    FirstName VARCHAR(25) NOT NULL,
    LastName VARCHAR(25) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    ProfileURL VARCHAR(500),
    PRIMARY KEY (UID)
);

CREATE TABLE Vegetarian_exclude (
    Ingredient VARCHAR(50) NOT NULL PRIMARY KEY
);

CREATE TABLE Nondairy_exclude (
    Ingredient VARCHAR(50) NOT NULL PRIMARY KEY
);

CREATE TABLE Vegan_exclude (
    Ingredient VARCHAR(50) NOT NULL PRIMARY KEY
);

-- end of create tables

-- start of inserting required data
INSERT INTO 
Vegetarian_exclude (Ingredient)
VALUES
("fish"),
("Prawns"),
("Bacon"),
("Meat"),
("Chicken"),
("Lamb loin chops"),
("Duck"),
("Anchovy"),
("Sardines"),
("Beef"),
("Pork"),
("Goat"),
("Ham");

INSERT INTO 
Nondairy_exclude (Ingredient)
VALUES
("Butter"),
("cheese"),
("yogurt"),
("Cream"),
("Parmesan"),
("Gruyère"),
("Milk"),
("Fromage Frais"),
("Creme Fraiche");

INSERT INTO 
Vegan_exclude (Ingredient)
VALUES
("fish"),
("Prawns"),
("Bacon"),
("Butter"),
("cheese"),
("yogurt"),
("Cream"),
("Parmesan"),
("Gruyère"),
("Milk"),
("Fromage Frais"),
("Creme Fraiche"),
("Meat"),
("Chicken"),
("Lamb loin chops"),
("Duck"),
("Anchovy"),
("Sardines"),
("Beef"),
("Pork"),
("Goat"),
("Ham"),
("Egg");

INSERT INTO 
PREF_LIST (Pref) 
VALUES 
( "Vegetarian"), 
("Non-dairy"),
("Vegan");

-- end of inserting required data

-- start of creating views

CREATE VIEW nondairy AS (SELECT r2.RID, r2.Ingredient, r2.Amount FROM RECIPE r2 WHERE r2.RID NOT IN (SELECT DISTINCT r.RID FROM RECIPE r, Nondairy_exclude e WHERE LOWER(r.Ingredient) LIKE CONCAT('%', LOWER(e.Ingredient), '%')));
CREATE VIEW vegetarian AS (SELECT r2.RID, r2.Ingredient, r2.Amount FROM RECIPE r2 WHERE r2.RID NOT IN (SELECT DISTINCT r.RID FROM RECIPE r, Vegetarian_exclude e WHERE LOWER(r.Ingredient) LIKE CONCAT('%', LOWER(e.Ingredient), '%')));
CREATE VIEW vegan AS (SELECT r2.RID, r2.Ingredient, r2.Amount FROM RECIPE r2 WHERE r2.RID NOT IN (SELECT DISTINCT r.RID FROM RECIPE r, Vegan_exclude e WHERE LOWER(r.Ingredient) LIKE CONCAT('%', LOWER(e.Ingredient), '%')));

-- end of createing views

-- start of data needed for testcase to run properly
INSERT INTO 
USERS (FirstName, LastName, Email, ProfileURL, MessageToken) 
VALUES ("User", "User", "user@example.com", NULL, "Token");

INSERT INTO 
DIETICIAN (FirstName, LastName, Email, ProfileURL, MessageToken) 
VALUES ("dietician", "dietician", "dietitian@example.com", NULL, "Token");

INSERT INTO
ADMIN (FirstName, LastName, Email, ProfileURL)
VALUES ("admin", "admin", "admin@example.com", NULL);

-- end of data need for testcases

-- query testing
INSERT INTO
USERS(FirstName, LastName, Email)
VALUES
('Test', 'test', 'testing@gmail.com'),
('Test2', 'test2', 'testing2@gmail.com');

INSERT INTO 
USERS (FirstName, LastName, Email, ProfileURL) 
VALUES ("a", "b", "c", NULL);

INSERT INTO 
DIETICIAN (FirstName, LastName, Email, ProfileURL) 
VALUES ("dietician", "dietician", "dietician.com", NULL);

INSERT INTO 
ADMIN (FirstName, LastName, Email, ProfileURL) 
VALUES ("admin", "admin", "admin1", NULL);

INSERT INTO
GROCERIES(UPC, Name, Brand)
VALUES
(123456789014, 'test1', 'testing'),
(123456789012, 'test2', 'testing');

INSERT INTO
GROCERIES(UPC, Name)
VALUES
(  -1, 'Salt'),
(  -1, 'Tomato'),
(  -1, 'Apple'),
(-1,'banana');

INSERT INTO
OWNS(UID, UPC,ExpireDate,ItemCount)
VALUES
( 2, 123456789012,'2023-10-19', 6),
( 2, 123456789014,'2023-10-16', 6);

INSERT INTO
OWNS(UID, UPC,ExpireDate,ItemCount, Name)
VALUES
( 2, -1,'2023-10-19', 6, 'Apple'),
( 2, -1,'2023-10-16', 6, 'banana');

INSERT INTO
OWNS(UID, UPC,ExpireDate,ItemCount, Name, PurchaseDate, AboutExpire)
VALUES
( 1, -1,'2023-11-19', 6, 'Tomato', '2023-10-19', 1),
( 1, -1,'2023-11-16', 6, 'Salt','2023-10-19',1);

INSERT INTO
OWNS(UID, UPC,ExpireDate,ItemCount, Name, PurchaseDate, AboutExpire)
VALUES
( 1, -1,'2023-11-02', 6, 'Tomato', '2023-10-19', 0);

UPDATE OWNS SET AboutExpire = CASE WHEN DATEDIFF(ExpireDate, CURDATE()) <= 2 THEN 1 ELSE 0 END WHERE UID=1;


INSERT INTO DIETICIAN (FirstName, LastName, Email, ProfileURL, MessageToken) 
SELECT u.FirstName, u.LastName, u.Email, u.ProfileURL, u.MessageToken
FROM USERS u
WHERE u.UID=23;

INSERT IGNORE INTO PREFERENCE (UID, Pref) VALUES (1, "Vegan");

INSERT INTO DIETICIAN_REQUEST (UID) VALUES (2);

SELECT d.RID, u.UID, u.FirstName, u.LastName, u.Email, u.ProfileURL 
FROM DIETICIAN_REQUEST d, USERS u 
WHERE u.UID=d.UID AND u.UID=1;

SELECT UID 
FROM USERS 
WHERE FirstName=a AND LastName=b AND Email=c;

SELECT DISTINCT Ingredient 
FROM RECIPE;

DELETE 
FROM OWNS 
WHERE UID=1 AND UPC=123456789012 AND ExpireDate='2023-10-26' AND ItemCount=8;

UPDATE OWNS o
JOIN (
    SELECT 
        o1.UPC,
        o1.UID,
        o1.ExpireDate,
        o1.ItemCount,
        ROW_NUMBER() OVER (PARTITION BY o1.UID ORDER BY o1.ExpireDate, o1.UPC ASC) AS NewItemID
    FROM OWNS o1
    WHERE o1.UID = 2
) AS result
ON o.UPC = result.UPC AND o.UID = result.UID AND o.ExpireDate=result.ExpireDate And o.ItemCount=result.ItemCount
SET o.ItemID = result.NewItemID
WHERE o.UID = 2;
SELECT *
FROM OWNS o
WHERE o.UID=2
ORDER BY o.ItemID ASC;

UPDATE USERS 
SET FirstName="testing1", LastName="testing1", Email="checkupdate.com", ProfileURL="testing" 
WHERE UID=1;

UPDATE OWNS 
SET ExpireDate='2023-12-24', ItemCount=100 
WHERE UID=1 AND UPC=123456789014 AND ItemID=1;

TRUNCATE TABLE recipe;
TRUNCATE TABLE recipe_info;

SELECT COUNT(*) FROM recipe_info;

SELECT *
FROM recipe r, (
SELECT store.RID
FROM (
SELECT * FROM vegan
INTERSECT
SELECT * FROM nondairy
INTERSECT
SELECT * FROM vegetarian) AS store
WHERE store.Ingredient LIKE '%Tomato%'
	ORDER BY RAND()
	LIMIT 5) AS temp
WHERE r.RID IN (temp.RID);

SELECT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID  FROM OWNS o, GROCERIES g WHERE o.UID=2 && g.UPC=o.UPC ORDER BY o.ItemID ASC;

SELECT whatever.RID 
FROM (
SELECT store.RID
FROM (
SELECT * FROM vegan
INTERSECT
SELECT * FROM nondairy
INTERSECT
SELECT * FROM vegetarian) AS store
WHERE LOWER(store.Ingredient) LIKE LOWER('%Tomato%')
INTERSECT
SELECT store.RID
FROM (
SELECT * FROM vegan
INTERSECT
SELECT * FROM nondairy
INTERSECT
SELECT * FROM vegetarian) AS store
WHERE LOWER(store.Ingredient) LIKE LOWER('%Salt%')) AS whatever
ORDER BY RAND()
LIMIT 5;

SELECT s.RID FROM (SELECT store.RID FROM (SELECT * FROM vegetarian INTERSECT SELECT * FROM nondairy INTERSECT SELECT * FROM vegan) AS store WHERE store.Ingredient
 LIKE '%Tomato%' INTERSECT SELECT store.RID FROM (SELECT * FROM vegetarian INTERSECT SELECT * FROM nondairy INTERSECT SELECT * FROM vegan) AS store WHERE store.Ingredient
 LIKE '%Salt%' INTERSECT SELECT store.RID FROM (SELECT * FROM vegetarian INTERSECT SELECT * FROM nondairy INTERSECT SELECT * FROM vegan) AS store WHERE store.Ingredient L
IKE ? ) AS s ORDER BY RAND() LIMIT 5

SELECT * FROM Recipe WHERE RID IN (52785,52995,52977,53000,52807)

SELECT g.Name, g.Brand, o.UPC, o.ExpireDate, o.ItemCount, o.ItemID FROM OWNS o INNER JOIN GROCERIES g ON g.UPC = o.UPC AND (o.Name = 'whatever' OR g.Name = o.Name) WHERE o.UID = 2 ORDER BY o.ItemID ASC;

SET SQL_SAFE_UPDATES = 0;
UPDATE OWNS SET AboutExpire = CASE WHEN DATEDIFF(ExpireDate, CURDATE()) <= 2 THEN 1 ELSE 0 END;
SET SQL_SAFE_UPDATES = 1;

ALTER TABLE USERS
ADD COLUMN MessageToken VARCHAR(200) NOT NULL;
SELECT DISTINCT UID FROM OWNS WHERE AboutExpire = CASE WHEN DATEDIFF(ExpireDate, CURDATE()) <= 2 THEN 1 ELSE 0 END

/*
-- Get a list of all tables in the database and generate DROP TABLE statements for each one.
SELECT CONCAT('DROP TABLE IF EXISTS `', table_name, '`;') 
FROM information_schema.tables 
WHERE table_schema = 'grocerymanger'; 

-- delete all tables 
DROP TABLE IF EXISTS `chat`;
DROP TABLE IF EXISTS `dietician`;
DROP TABLE IF EXISTS `dietician_request`;
DROP TABLE IF EXISTS `preference`;
DROP TABLE IF EXISTS `owns`;
DROP TABLE IF EXISTS `rating`;
DROP TABLE IF EXISTS `recipe`;
DROP TABLE IF EXISTS `recipe_info`;
DROP TABLE IF EXISTS `groceries`;
DROP TABLE IF EXISTS `pref_list`;
DROP TABLE IF EXISTS `users`;*/