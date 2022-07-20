# TinyDB
TinyDB is a small relational database management system written from scratch in pure Java, it can handle connection requests from 
multiple users at once, and execute a subset of SQL queries.

The structure of the database is kept in a text file 'dbinfo/dbinfo.txt' and it uses the following format

```
database_name
	table_name
		column1 TYPE
		column2 TYPE
		...
	...
...
```

The actual row data resides in binary files inside he 'databases' folder which contains a folder for each database, and each
database folder contains binary files that store the row data of each table it has:

```
rootdir
|_databases
  |_db1
  | |_table1.table
  | |_table2.table
  |_db2
    |_table3.table
```

---
TinyDB supports the following queries:

```sql
CREATE DATABASE database_name;

CREATE TABLE table_name (column1 TYPE, column2 TYPE, ...);

INSERT INTO table_name (column1, column2, ...) VALUES (value1, value2, ...);

SELECT column1, column2, ... FROM table_name WHERE CONDITION;

UPDATE table_name SET column1=value1, column2=value2, ... WHERE CONDITION;

DELETE FROM table_name WHERE CONDITION;
```

TYPE could be any one of the supported types: ```INTEGER```, ```REAL```, ```BOOL```, ```DATE```, ```VARCHAR```

CONDITION must respect the following syntax: ```CRITERION1 AND CRITERION2 AND ...```  (*)

where each CRITERION includes 2 operators and 1 operand; for example the following is a valid condition: ```col1 < col2 AND col2 = col3```

---
TinyDB also supports user authentication, users must provide a connection string along with their connection request.

The connection string has the format: ```username:password:dbName```

TinyDB verifies that username:password combination by looking for a record in the accounts table found in the reserved _tinyDB_accounts_ database.

---

Improvement ideas:
- Use a B-Tree instead of linear search to speed up operations.
- Write an expression compiler that can compile complex CONDITION expressions into binary trees and evaluate their values for each row. (This would get rid of restriction (*))
