#Mini Web Server
A mini-webserver using only socket programming that listens to port 64062.

# Specifications
Listens to a specified port,
Parse the HTTP header (which is a GET (use browser) or POST (use POSTMAN or another tool))
Must print back the keys and values of the query GET or POST as an HTML formatted table,
And append the requested file (only html, css, js) in the same folder as where the web server program is.

# Compile and Run
javac *.java
java Webserver

#Created By
Mark V. Abrogar
2012-64062
CMSC137 B-1L

#References
https://docs.python.org/2/howto/sockets.html
http://www.java2s.com/Code/Java/Network-Protocol/ASimpleWebServer.htm
https://github.com/fullstackio/ng-diuno/tree/master/prez
http://stackoverflow.com/questions/409087/creating-a-web-server-in-pure-c
http://rosettacode.org/wiki/Hello_world/Web_server#Java
http://beej.us/guide/bgnet/output/html/singlepage/bgnet.html
