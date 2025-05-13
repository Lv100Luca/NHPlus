Running the application
=======================

To run the application, select (preferably) the OpenJDK 24 SDK and run `Main`.

Should this be the first run, the database will be created using the `SetUpDB` class.
Here are some things to keep in mind with the db setup:

* ensure a folder called `db` exists in the root of the project
* should the program mention a missing table after running `SetUpDB`, this issue is known and is resolved by running the program again
* should there be other issues when creating the database, try to delete the existing `.db` file and repeat the process

# Logins

# Tests