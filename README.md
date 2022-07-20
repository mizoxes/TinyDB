# TinyDB
TinyDB is a small relational database management system written from scratch in pure Java, it can handle connection requests from 
multiple users at once, and execute a subset of SQL queries.

The structure of the database is kept in a text file 'dbinfo/dbinfo.txt' and it uses the following format

```
database_name
	table_name
		column1_name column1_type
		column2_name column2_type
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
 

Improvement ideas:
- Use a B-Tree instead of linear search to speed up operations.
