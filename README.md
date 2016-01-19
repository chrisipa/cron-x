Cron-X
=======

[![Build Status](https://papke.it/jenkins/buildStatus/icon?job=cron-x)](https://papke.it/jenkins/job/cron-x/)

Java based drop-in replacement for cron scheduler which allows executing jobs every second. 

Installation
-------------
* Create install directory:
```
sudo mkdir /opt/cron-x
```

* Download necessary files:
```
sudo wget https://raw.githubusercontent.com/chrisipa/cron-x/master/bin/cron-x -O /opt/cron-x/cron-x  
sudo wget https://papke.it/jenkins/job/cron-x/lastStableBuild/de.papke%24cron-x/artifact/de.papke/cron-x/1.0.0/cron-x-1.0.0.jar -O /opt/cron-x/cron-x.jar
```

* Make starter script executable:
```
sudo chmod +x /opt/cron-x/cron-x
```

Usage
-------------
* Run directly from console:
```
sudo /opt/cron-x/cron-x start
```
/opt/cron-x
