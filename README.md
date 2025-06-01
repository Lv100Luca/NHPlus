Running the application
=======================

To run the application, select (preferably) the OpenJDK 24 SDK and run `Main`.

Should this be the first run, the database will be created using the `SetUpDB` class.
Here are some things to keep in mind with the db setup:

* ensure a folder called `db` exists in the root of the project
* should the program mention a missing table after running `SetUpDB`, this issue is known and is resolved by running the
  program again
* should there be other issues when creating the database, try to delete the existing `.db` file and repeat the process

# Logins

# Tests

# Vermögensstand:

| testfall                                              | implementation | working | 
|-------------------------------------------------------|----------------|---------|
| remove "Vermögensstand" Column from the table         | ✅              | ✅       |
| remove the ability to enter assets for patients       | ✅              | ✅       |
| remove all previous entries of assets in the Database | ✅              | ✅       |

# Archiving of Entries:

| Testcase                                                                                     | implementation | working |
|----------------------------------------------------------------------------------------------|----------------|---------|
| Entries younger than 10 years cant be deleted                                                | ✅              | ✅       |
| Entries can be manually archived                                                             | ✅              | ✅       |
| Archived entries are read only                                                               | ✅              | ✅       |
| Entries older than 10 years can be deleted<br/> **deletion will be automatic**               | ❌              | ➖       |
| Entries older than 10 years will be automatically deleted<br/> see `ArchiveServiceTest.java` | ✅              | ✅       |

# Login:

| testfall                                                 | implementation | working | 
|----------------------------------------------------------|----------------|---------|
| A valid Caregiver is able to login                       | ✅              | ✅       |
| Error Message if the wrong password is entered           | ✅              | ✅       |
| Error Message if User doesnt exist                       | ✅              | ✅       |
| Login is denied after entering the wrong password thrice | ✅              | ✅       |
| Login is possible again after denial period is over      | ✅              | ✅       |