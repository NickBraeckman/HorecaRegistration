# Horeca Registration

## Table of contents
* [Authors](#Authors)
* [Assignment description](#Assignment-description)
* [Features](#Features)
* [Run the registration system](#Run-the-registration-system)
* [Clone the project](#Clone-the-project)
* [Info](#Info)

## Authors
* Romeo Permentier
* Nick Braeckman

## Assignment description
Implement a privacy friendly tracing registration system for catering facilities [1]

## Features

### Catering Facility Application
* Minimal interface

### Doctor Application
* Minimal interface
* Data transfer 'Visitor' and 'Doctor': sharing a file

### Visitor Application
* Java FX GUI
* QR-code in the form of a data string

### Registrar
* Server Application: reacts on input of the 'Bar Owner', 'Visitor' and 'Matching Service'
* Interface shows current content of database at each time

### Mixing Proxy
* Interface shows queue at each time
* For demo purposes: a flush button to empty the queue

### Matching Service
* Interface visualizes the state of the matching service (content of the database)

## Run the registration system

1) Download the jar's

2) Execute the jar's in the following order:

## Clone the project

1. Clone project from github

2. Import project: prerequisites
   *  Project JDK: 1.8.0_241
   *  Project Language Level: 8
   
3. Setup modules:

4. Run configurations:

    * Run the Registrar module
    * Run the Catering facility module 
    * Enter the credentials of the catering facility
    * QR-code appears on the last line: 
    
   ```JuJ/rgnCZKxzcomzEGWbi8RnXwg=1234=+VGiIrFVlVq48ylpgP+RexvERVdjOpAIbt7umeIPMVA```
    
    * Run the Matching Service module
    * Run the Mixing Proxy module
    * Run the Visitor module
    * Enter phone number
    * Enter QR-code:
    
    ```JuJ/rgnCZKxzcomzEGWbi8RnXwg=1234=+VGiIrFVlVq48ylpgP+RexvERVdjOpAIbt7umeIPMVA=```
    
    * Press on exit when you leave the bar
    * Flush the content of the Mixing Proxy database (.csv file)
    * Run the Doctor Application
    * Read in the visitor log by providing the directory name of the visitor:
    ```dir_visitor_<name>```
    
   
## More Info
* https://www.javatpoint.com/socket-programming
* https://docs.oracle.com/javase/tutorial/networking/index.html
* [1] M. Willocx, D. Singelée, and V. Naessens, “Supporting Contact Tracing by Privacy-Friendly Registration at Catering Facilities,” pp. 3–8.
