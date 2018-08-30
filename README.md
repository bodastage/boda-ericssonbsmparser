[![Build Status](https://travis-ci.org/bodastage/boda-ericssonbsmparser.svg?branch=master)](https://travis-ci.org/bodastage/boda-ericssonbsmparser)

# boda-ericssonbsmparser
Parses Ericsson bsm_hw_export dumps to csv

Below is the expected format of the input file:

```
BSM Hardware Data Export 
Created by user someuser on Wed Aug 20 10:17:54 EAST 2018

dn	rutag	rulogicalid	ruserialno	rurevision	ruposition	model	mo	vendor	ru

subnetwork="AXE",bsc="BSCNAME",site="SITENAME",ru="V21-TG-401",ru="CABI RBS 7702                 0"	RU2	CABI RBS 7702                 0	B123456789	133/BFL 888 001       R1D	C:0 R:- SH:-- SL:---	V21	RXOCF-502		CABI RBS 7702                 0
subnetwork="AXE",bsc="BSCNAME",site="SITENAME",ru="V21-TG-401",ru="CABI RBS 7702                 0"	RU2	CABI RBS 7702                 0	B123456789	133/BFL 888 001       R1D	C:0 R:- SH:-- SL:---	V21	RXOCF-502		CABI RBS 7702                 0
subnetwork="AXE",bsc="BSCNAME",site="SITENAME",ru="V21-TG-401",ru="CABI RBS 7702                 0"	RU2	CABI RBS 7702                 0	B123456789	133/BFL 888 001       R1D	C:0 R:- SH:-- SL:---	V21	RXOCF-502		CABI RBS 7702                 0

```

# Usage

```
usage: java -jar boda-ericssonbsmparser.jar
Parses Ericsson BSM hardware dumps to csv

 -h,--help                             show help
 -i,--input-file <INPUT_FILE>          input file or directory name
 -o,--output-file <OUTPUT_FILE>   output file name
 -v,--version                          display version

Examples:
java -jar boda-ericssonbsmparser.jar -i dump_file -o out_file.csv
java -jar boda-ericssonbsmparser.jar -i input_folder -o out_file.csv

Copyright (c) 2018 Bodastage Solutions(http://www.bodastage.com)
```

# Download and installation
Download the latest jar file  from the [latest release](https://github.com/bodastage/boda-ericssonbsmparser/releases/latest).

# Requirements
To run the jar file, you need Java version 1.8 and above.

# Getting help
To report issues with the application or request new features use the issue [tracker](https://github.com/bodastage/boda-ericssonbsmparser/issues). For help and customizations send an email to info@bodastage.com.

# Credits
[Bodastage Solutions](http://www.bodastage.com) - info@bodastage.com

# Contact
For any other concerns apart from issues and feature requests, send an email to info@bodastage.com.

# Licence
This project is licensed under the Apache 2.0 licence.  See LICENCE file for details.