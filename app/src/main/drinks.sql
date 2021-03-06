DROP TABLE IF EXISTS "Cat_spec";
CREATE TABLE "Cat_spec" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "name" TEXT, "c_id" INTEGER);
INSERT INTO "Cat_spec" VALUES(1,'Milk',1);
INSERT INTO "Cat_spec" VALUES(2,'Cream',1);
INSERT INTO "Cat_spec" VALUES(3,'Vodka',2);
INSERT INTO "Cat_spec" VALUES(4,'Rum (white)',2);
INSERT INTO "Cat_spec" VALUES(5,'Rum (dark)',2);
INSERT INTO "Cat_spec" VALUES(6,'Tequila',2);
INSERT INTO "Cat_spec" VALUES(7,'Gin',2);
INSERT INTO "Cat_spec" VALUES(8,'Vodka (Vanilla)',2);
INSERT INTO "Cat_spec" VALUES(9,'Vodka (lemon)',2);
INSERT INTO "Cat_spec" VALUES(10,'Cointreau',3);
INSERT INTO "Cat_spec" VALUES(11,'Kahlua',3);
INSERT INTO "Cat_spec" VALUES(12,'White',4);
INSERT INTO "Cat_spec" VALUES(13,'Red',4);
INSERT INTO "Cat_spec" VALUES(14,'Rose',4);
INSERT INTO "Cat_spec" VALUES(15,'Orange juice',5);
INSERT INTO "Cat_spec" VALUES(16,'Cranberry juice',5);
INSERT INTO "Cat_spec" VALUES(17,'Cola',5);
INSERT INTO "Cat_spec" VALUES(18,'Tonic water',5);
INSERT INTO "Cat_spec" VALUES(19,'Club soda',5);
INSERT INTO "Cat_spec" VALUES(20,'Sprite',5);
INSERT INTO "Cat_spec" VALUES(21,'Lime',6);
INSERT INTO "Cat_spec" VALUES(22,'Lemon',6);
INSERT INTO "Cat_spec" VALUES(23,'Grenadine',6);
INSERT INTO "Cat_spec" VALUES(24,'Mint',6);
INSERT INTO "Cat_spec" VALUES(25,'Sugar (white)',7);
INSERT INTO "Cat_spec" VALUES(26,'Sugar (brown)',7);
INSERT INTO "Cat_spec" VALUES(27,'Sourz Apple',3);
INSERT INTO "Cat_spec" VALUES(28,'Sourz Pineapple',3);
INSERT INTO "Cat_spec" VALUES(29,'Sourz Rasberry',3);
DROP TABLE IF EXISTS "Categories";
CREATE TABLE "Categories" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "catName" TEXT);
INSERT INTO "Categories" VALUES(1,'Milk products');
INSERT INTO "Categories" VALUES(2,'Base');
INSERT INTO "Categories" VALUES(3,'Liqueur');
INSERT INTO "Categories" VALUES(4,'Wine');
INSERT INTO "Categories" VALUES(5,'Juices and lemonade');
INSERT INTO "Categories" VALUES(6,'Decoration');
INSERT INTO "Categories" VALUES(7,'Other');
DROP TABLE IF EXISTS "Connection";
CREATE TABLE "Connection" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "drink_id" INTEGER, "spec_id" INTEGER);
INSERT INTO "Connection" VALUES(1,1,3);
INSERT INTO "Connection" VALUES(2,1,15);
INSERT INTO "Connection" VALUES(3,2,4);
INSERT INTO "Connection" VALUES(4,2,21);
INSERT INTO "Connection" VALUES(5,2,25);
INSERT INTO "Connection" VALUES(6,2,24);
INSERT INTO "Connection" VALUES(7,2,19);
INSERT INTO "Connection" VALUES(8,3,7);
INSERT INTO "Connection" VALUES(9,3,18);
INSERT INTO "Connection" VALUES(10,3,22);
INSERT INTO "Connection" VALUES(11,4,1);
INSERT INTO "Connection" VALUES(12,4,3);
INSERT INTO "Connection" VALUES(13,4,11);
INSERT INTO "Connection" VALUES(14,5,4);
INSERT INTO "Connection" VALUES(15,5,17);
INSERT INTO "Connection" VALUES(16,5,21);
INSERT INTO "Connection" VALUES(17,6,3);
INSERT INTO "Connection" VALUES(18,6,21);
INSERT INTO "Connection" VALUES(19,6,25);
INSERT INTO "Connection" VALUES(20,7,6);
INSERT INTO "Connection" VALUES(21,7,15);
INSERT INTO "Connection" VALUES(22,7,23);
INSERT INTO "Connection" VALUES(23,8,9);
INSERT INTO "Connection" VALUES(24,8,10);
INSERT INTO "Connection" VALUES(25,8,16);
INSERT INTO "Connection" VALUES(26,8,21);
INSERT INTO "Connection" VALUES(27,9,27);
INSERT INTO "Connection" VALUES(28,9,8);
INSERT INTO "Connection" VALUES(29,9,20);
INSERT INTO "Connection" VALUES(30,9,21);
INSERT INTO "Connection" VALUES(31,10,8);
INSERT INTO "Connection" VALUES(32,10,28);
INSERT INTO "Connection" VALUES(33,10,20);
INSERT INTO "Connection" VALUES(34,10,21);
DROP TABLE IF EXISTS "Drinks";
CREATE TABLE "Drinks" ("id" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL  UNIQUE , "name" TEXT, "stars" INTEGER, "image" TEXT);
INSERT INTO "Drinks" VALUES(1,'Screwdriver',4,'R.drawable.screwdriver');
INSERT INTO "Drinks" VALUES(2,'Mojito',3,'R.drawable.mojito');
INSERT INTO "Drinks" VALUES(3,'Gin & Tonic',5,'R.drawable.gintonic');
INSERT INTO "Drinks" VALUES(4,'White Russian',2,'R.drawable.whiterussian');
INSERT INTO "Drinks" VALUES(5,'Cuba Libre',4,'R.drawable.cubalibre');
INSERT INTO "Drinks" VALUES(6,'Caprinoska',5,'R.drawable.caprinoska');
INSERT INTO "Drinks" VALUES(7,'Tequila Sunrise',2,'R.drawable.tequilasunrise');
INSERT INTO "Drinks" VALUES(8,'Cosmopolitan',3,'R.drawable.cosmopolitan');
INSERT INTO "Drinks" VALUES(9,'P2',2,'R.drawable.p2');
INSERT INTO "Drinks" VALUES(10,'Vanilla Sky',3,'R.drawable.vanillasky');
