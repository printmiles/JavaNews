JavaNews
========

PSE monitoring tool for X.25 nodes which logs messages received and displays link status.

Interesting Code
----------------
- src/javanews/object/PortReader.java - Lines 224-359 - Reads data from the COM port and stores it within a StringBuffer.

Requires the following libraries
---------------------------------
- JFreeChart (http://www.jfree.org/jfreechart/) - Used for displaying link statistics
- RxTx (http://rxtx.qbang.org/wiki/index.php/Download) - Used for communication with the COM ports